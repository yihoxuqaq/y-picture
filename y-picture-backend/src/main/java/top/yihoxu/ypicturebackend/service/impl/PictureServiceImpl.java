package top.yihoxu.ypicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import top.yihoxu.ypicturebackend.api.aliyunai.AliYunAiApi;
import top.yihoxu.ypicturebackend.api.aliyunai.CreateOutPaintingTaskRequest;
import top.yihoxu.ypicturebackend.api.aliyunai.CreateOutPaintingTaskResponse;
import top.yihoxu.ypicturebackend.common.DeleteRequest;
import top.yihoxu.ypicturebackend.enums.PictureReviewStatusEnum;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.manager.upload.FileManager;
import top.yihoxu.ypicturebackend.manager.upload.dto.UploadPictureResult;
import top.yihoxu.ypicturebackend.manager.upload.template.PictureUpload;
import top.yihoxu.ypicturebackend.manager.upload.template.PictureUploadByURL;
import top.yihoxu.ypicturebackend.manager.upload.template.PictureUploadTemplate;
import top.yihoxu.ypicturebackend.mapper.PictureMapper;
import top.yihoxu.ypicturebackend.model.dto.picture.*;
import top.yihoxu.ypicturebackend.model.entity.Picture;
import top.yihoxu.ypicturebackend.model.entity.Space;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.PictureGradVO;
import top.yihoxu.ypicturebackend.model.vo.PictureVO;
import top.yihoxu.ypicturebackend.service.PictureService;
import top.yihoxu.ypicturebackend.service.SpaceService;
import top.yihoxu.ypicturebackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static top.yihoxu.ypicturebackend.rabbitmq.RabbitMQConfig.EXCHANGE_NAME;
import static top.yihoxu.ypicturebackend.rabbitmq.RabbitMQConfig.ROUTING_KEY;

/**
 * @author hushi
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-05-02 13:25:21
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;
    @Resource
    private SpaceService spaceService;

    @Resource
    private PictureUpload pictureUpload;

    @Resource
    private PictureUploadByURL pictureUploadByURL;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private AliYunAiApi aliYunAiApi;

    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {

        //用于判断是新增还是更新
        Long pictureId;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        } else {
            pictureId = null;
        }
        if (pictureId != null) {
            boolean exists = this.lambdaQuery()
                    .eq(Picture::getId, pictureId)
                    .exists();
            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }

        //根据用户id划分目录
        Long spaceId = pictureUploadRequest.getSpaceId();
        String uploadPathPrefix = null;
        //根据空间id分区上传目录
        if (spaceId == null) {
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        } else {
            uploadPathPrefix = String.format("space/%s", loginUser.getId());
        }
        //上传图片的到图片信息
        PictureUploadTemplate pictureUploadTemplate = pictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = pictureUploadByURL;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        //构造入库图片信息
        Picture picture = new Picture();
        picture.setSpaceId(spaceId);
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
            Picture oldPicture = this.getById(pictureId);
            picture.setSpaceId(oldPicture.getSpaceId());
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

        }
        fillPictureReviewStatus(picture, loginUser);
        transactionTemplate.execute(status -> {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "picture:*");
                        }
                    }
            );
            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            if (spaceId != null) {
                Space space = spaceService.getById(spaceId);
                if (space == null) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "空间不存在");
                }
                if (!space.getUserId().equals(loginUser.getId())) {
                    throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限");
                }
                //校验额度
                if (space.getTotalCount() >= space.getMaxCount()) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间条数不足");
                }
                if (space.getTotalSize() >= space.getMaxSize()) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间大小不足");
                }
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("totalCount = totalCount + 1")
                        .setSql("totalSize = totalSize + " + picture.getPicSize())
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "更新额度失败");
            }
            return picture;
        });
        return PictureVO.objToVo(picture);
    }


    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long spaceId = pictureQueryRequest.getSpaceId();
        Boolean nullSpaceId = pictureQueryRequest.getNullSpaceId();
        Date startCreateTime = pictureQueryRequest.getStartCreateTime();
        Date endCreateTime = pictureQueryRequest.getEndCreateTime();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.ge(ObjUtil.isNotEmpty(startCreateTime), "createTime", startCreateTime);
        queryWrapper.lt(ObjUtil.isNotEmpty(endCreateTime), "createTime", endCreateTime);
        queryWrapper.isNull(nullSpaceId, "spaceId");
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public List<PictureVO> listUsersVO(List<Picture> pictureList, HttpServletRequest request) {
        if (CollUtil.isEmpty(pictureList)) {
            return new ArrayList<PictureVO>();
        }
        //对象列表->分装类列表
        List<PictureVO> pictureVOList = pictureList.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
        //管理用户信息
        Set<Long> userIdSet = pictureList.stream()
                .map(Picture::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> listUser = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        for (PictureVO pictureVO : pictureVOList) {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (listUser.containsKey(userId)) {
                user = listUser.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        }
        return pictureVOList;
    }

    @Override
    public Boolean doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id < 0 || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Picture picture = this.getById(id);
        if (picture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        }
        if (picture.getReviewStatus().equals(reviewStatusEnum.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复审核");
        }
        String reviewMessage = pictureReviewRequest.getReviewMessage();
        if (PictureReviewStatusEnum.PASS.equals(reviewStatusEnum) && StrUtil.isBlank(reviewMessage)) {
            pictureReviewRequest.setReviewMessage("默认信息：审核通过");
        }
        if (PictureReviewStatusEnum.REJECT.equals(reviewStatusEnum) && StrUtil.isBlank(reviewMessage)) {
            pictureReviewRequest.setReviewMessage("默认信息：审核拒绝");
        }

        Picture newPicture = new Picture();
        BeanUtil.copyProperties(pictureReviewRequest, newPicture);
        newPicture.setReviewerId(loginUser.getId());
        newPicture.setReviewTime(new Date());
        boolean result = this.updateById(newPicture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;


    }

    @Override
    public void fillPictureReviewStatus(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewMessage("管理自动审核通过");
        } else {
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    @Override
    public boolean deletePicture(DeleteRequest deleteRequest, User loginUser) {
        Long id = deleteRequest.getId();
        Picture picture = this.getById(id);
        if (picture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        transactionTemplate.execute(status -> {
            if (picture.getSpaceId() != null) {
                Space space = spaceService.getById(picture.getSpaceId());
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, picture.getSpaceId())
                        .setSql("totalCount = totalCount - 1")
                        .setSql("totalSize = totalSize - " + picture.getPicSize())
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "更新额度失败");

            }
            boolean deletePicture = this.removeById(deleteRequest.getId());
            ThrowUtils.throwIf(!deletePicture, ErrorCode.OPERATION_ERROR, "删除图片失败");
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "picture:*");
                        }
                    }
            );
            return deletePicture;
        });
        return true;
    }

    @Override
    public List<PictureGradVO> uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        String searchText = pictureUploadByBatchRequest.getSearchText();
        // 格式化数量
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多 30 条");
        // 要抓取的地址
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取页面失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isNull(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
        }
        List<PictureGradVO> pictureGradVOList = new ArrayList<>();
        Elements imgElementList = div.select("a.iusc");
        for (Element imgElement : imgElementList) {
            pictureGradVOList.add(JSONUtil.toBean(imgElement.attr("m"), PictureGradVO.class));
            System.out.println(imgElement.attr("m"));
        }
        return pictureGradVOList.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        Long id = pictureEditRequest.getId();
        List<String> tags = pictureEditRequest.getTags();
        Picture pic = this.getById(id);
        if (pic == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String jsonStr = JSONUtil.toJsonStr(tags);
        if (!loginUser.getId().equals(pic.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureEditRequest, picture);
        picture.setEditTime(new Date());
        picture.setTags(jsonStr);
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "picture:*");
                    }
                }
        );
        return result;
    }

    @Override
    public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser) {
        //获取图片信息
        Long pictureId = createPictureOutPaintingTaskRequest.getPictureId();
        Picture picture = Optional.ofNullable(this.getById(pictureId))
                .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR));
        if (!picture.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //构建请求参数
        CreateOutPaintingTaskRequest createOutPaintingTaskRequest = new CreateOutPaintingTaskRequest();
       CreateOutPaintingTaskRequest.Input input=new CreateOutPaintingTaskRequest.Input();
       input.setImageUrl(picture.getUrl());
       createOutPaintingTaskRequest.setInput(input);
       BeanUtil.copyProperties(createPictureOutPaintingTaskRequest,createOutPaintingTaskRequest);

        return aliYunAiApi.createOutPaintingTask(createOutPaintingTaskRequest);
    }


}




