package top.yihoxu.ypicturebackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import top.yihoxu.ypicturebackend.annotation.AuthCheck;
import top.yihoxu.ypicturebackend.common.BaseResponse;
import top.yihoxu.ypicturebackend.common.DeleteRequest;
import top.yihoxu.ypicturebackend.common.ResultUtils;
import top.yihoxu.ypicturebackend.constant.UserConstant;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureQueryRequest;
import top.yihoxu.ypicturebackend.model.dto.picture.PictureUpdateRequest;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceAddRequest;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceQueryRequest;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceUpdateRequest;
import top.yihoxu.ypicturebackend.model.enums.SpaceLevelEnum;
import top.yihoxu.ypicturebackend.model.pojo.Picture;
import top.yihoxu.ypicturebackend.model.pojo.Space;
import top.yihoxu.ypicturebackend.model.pojo.User;
import top.yihoxu.ypicturebackend.model.vo.PictureVO;
import top.yihoxu.ypicturebackend.model.vo.SpaceLevel;
import top.yihoxu.ypicturebackend.model.vo.SpaceVO;
import top.yihoxu.ypicturebackend.model.vo.UserVO;
import top.yihoxu.ypicturebackend.service.SpaceService;
import top.yihoxu.ypicturebackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
@Slf4j
public class SpaceController {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;


    /**
     * 创建空间
     *
     * @param spaceAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long spaceId = spaceService.addSpace(spaceAddRequest, loginUser);
        return ResultUtils.success(spaceId);
    }

    /**
     * 更新空间仅管理员可用
     *
     * @param spaceUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 数据校验
        spaceService.validSpace(space, false);
        // 判断是否存在
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 删除空间
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = spaceService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取图片列表（仅管理员可用）
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> picturePage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(picturePage);
    }
    /**
     * 分页获取图片列表(脱敏)
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long size = spaceQueryRequest.getPageSize();
        // 查询数据库
        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spaceService.getSpaceVOPage(spacePage, request));
    }


    @GetMapping("/get/spaceLevelList")
    public BaseResponse<List<SpaceLevel>> getSpaceLevelList() {
        List<SpaceLevelEnum> list = Arrays.asList(SpaceLevelEnum.values());
        List<SpaceLevel> result = list.stream()
                .map(i -> new SpaceLevel(i.getText(), i.getValue(), i.getMaxCount(), i.getMaxSize())).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取空间（仅管理员可用）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(space);
    }

    /**
     * 根据 id 获取空间（封装类）
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = space.getUserId();
        if (!loginUserId.equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        User user = userService.getById(loginUserId);
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        spaceVO.setUser(userService.getUserVO(user));
        return ResultUtils.success(spaceVO);
    }


}
