package top.yihoxu.ypicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.annotation.AuthCheck;
import top.yihoxu.ypicturebackend.common.BaseResponse;
import top.yihoxu.ypicturebackend.common.DeleteRequest;
import top.yihoxu.ypicturebackend.common.ResultUtils;
import top.yihoxu.ypicturebackend.constant.UserConstant;
import top.yihoxu.ypicturebackend.enums.PictureReviewStatusEnum;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureEditRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureQueryRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureReviewRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureUploadRequest;
import top.yihoxu.ypicturebackend.model.entity.Picture;
import top.yihoxu.ypicturebackend.model.entity.Space;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.PictureVO;
import top.yihoxu.ypicturebackend.model.vo.UserVO;
import top.yihoxu.ypicturebackend.service.PictureService;
import top.yihoxu.ypicturebackend.service.SpaceService;
import top.yihoxu.ypicturebackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author yihoxu
 * @date 2025/5/2  14:26
 * @description 图片接口
 */
@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    /**
     * 上传图片（可重新上传）
     */
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        Long spaceId = pictureUploadRequest.getSpaceId();
        if (spaceId != null) {
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
            if (!space.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 图片编辑（用户）
     *
     * @param pictureEditRequest
     * @param request
     * @return
     */
    @PostMapping("/editPicture")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null || pictureEditRequest.getId() < 0, ErrorCode.PARAMS_ERROR);
        Long id = pictureEditRequest.getId();
        List<String> tags = pictureEditRequest.getTags();
        Picture pic = pictureService.getById(id);
        if (pic == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String jsonStr = JSONUtil.toJsonStr(tags);
        User loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(pic.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureEditRequest, picture);
        picture.setEditTime(new Date());
        picture.setTags(jsonStr);
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据id获取封装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null || id < 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        if (picture == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(PictureVO.objToVo(picture));
    }

    /**
     * 图片列表
     *
     * @param pictureQueryRequest
     * @return
     */
    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<
            Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        //查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, pageSize), pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * 图片列表（脱敏）
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/vo")
    public BaseResponse<
            Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        //限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        if (pictureQueryRequest.getSpaceId() == null) {
            //仅允许审核通过的图片
            pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            pictureQueryRequest.setNullSpaceId(true);
        } else {
            User loginUser = userService.getLoginUser(request);
            Long spaceId = pictureQueryRequest.getSpaceId();
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
            if (!space.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
        }
        //查询数据库
        Page<Picture> picturePage = pictureService.page(new Page<>(current, pageSize), pictureService.getQueryWrapper(pictureQueryRequest));
        List<PictureVO> pictureVOList = pictureService.listUsersVO(picturePage.getRecords(), request);
        Page<PictureVO> pictureVOPage = new Page<>(current, pageSize, picturePage.getTotal());
        pictureVOPage.setRecords(pictureVOList);
        return ResultUtils.success(pictureVOPage);
    }


    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> pictureReview(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Boolean result = pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(result);

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest==null||deleteRequest.getId()<0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        boolean b = pictureService.deletePicture(deleteRequest, loginUser);

        return ResultUtils.success(b);
    }

}
