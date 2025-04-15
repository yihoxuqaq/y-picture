package top.yihoxu.ypicturebackend.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.mapper.SpaceMapper;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceAddRequest;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceQueryRequest;
import top.yihoxu.ypicturebackend.model.enums.SpaceLevelEnum;
import top.yihoxu.ypicturebackend.model.pojo.Picture;
import top.yihoxu.ypicturebackend.model.pojo.Space;
import top.yihoxu.ypicturebackend.model.pojo.User;
import top.yihoxu.ypicturebackend.model.vo.PictureVO;
import top.yihoxu.ypicturebackend.model.vo.SpaceVO;
import top.yihoxu.ypicturebackend.model.vo.UserVO;
import top.yihoxu.ypicturebackend.service.SpaceService;
import top.yihoxu.ypicturebackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hushi
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-04-14 15:12:28
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
        implements SpaceService {


    @Resource
    private UserService userService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public void validSpace(Space space, Boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();

        SpaceLevelEnum enumByValue = SpaceLevelEnum.getEnumByValue(spaceLevel);
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
        }
        if (spaceName.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
        if (spaceLevel != null && enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不存在");
        }

    }


    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        // 默认值
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        // 填充数据
        this.fillSpaceBySpaceLevel(space);
        // 数据校验
        this.validSpace(space, true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // 权限校验
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户进行加锁
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                // 写入数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                // 返回新写入的数据 id
                return space.getId();
            });
            // 返回结果是包装类，可以做一些处理
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }


    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Long userId = spaceQueryRequest.getUserId();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        queryWrapper.like(ObjUtil.isNotEmpty(spaceName), "spaceName", spaceName);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        //对象转为分装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        //关联用户信息
        Long userId = spaceVO.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum enumByValue = SpaceLevelEnum.getEnumByValue(spaceLevel);
        if (enumByValue != null) {
            if (space.getMaxSize() == null) {
                space.setMaxSize(enumByValue.getMaxSize());
            }

            if (space.getMaxCount() == null) {
                space.setMaxCount(enumByValue.getMaxCount());
            }
        }

    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());

        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        //对象列表转为分装类列表
        List<SpaceVO> pictureVOList = spaceList.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        //1.关联用户信息
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        //2、填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(pictureVOList);

        return spaceVOPage;
    }
}




