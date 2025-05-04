package top.yihoxu.ypicturebackend.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.*;
import top.yihoxu.ypicturebackend.common.BaseResponse;
import top.yihoxu.ypicturebackend.common.ResultUtils;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.model.dto.space.SpaceAddRequest;
import top.yihoxu.ypicturebackend.model.entity.Space;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.SpaceVO;
import top.yihoxu.ypicturebackend.service.SpaceService;
import top.yihoxu.ypicturebackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author yihoxu
 * @date 2025/5/4  13:55
 * @description 空间接口
 */
@RestController
@RequestMapping("/space")
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
    public BaseResponse<Long> spaceAdd(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        String spaceName = spaceAddRequest.getSpaceName();
        Integer spaceLevel = spaceAddRequest.getSpaceLevel();
        if (StrUtil.isBlank(spaceName) || ObjUtil.isNull(spaceLevel)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long spaceId = spaceService.spaceAdd(spaceAddRequest, loginUser);

        return ResultUtils.success(spaceId);
    }

    /**
     * 获取登录下用户空间
     *
     * @param request
     * @return
     */
    @GetMapping
    public BaseResponse<Long> getUserSpace(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Space space = spaceService.lambdaQuery()
                .eq(Space::getUserId, loginUser.getId())
                .one();
        if (space == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请先创建空间");
        }

        return ResultUtils.success(space.getId());
    }

    /**
     * 获取登录下用户空间
     *
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getUserSpaceById(Long id, HttpServletRequest request) {
        if (id==null||id<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        Space space = spaceService.getById(id);
        if (!loginUser.getId().equals(space.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限访问");
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtil.copyProperties(space, spaceVO);
        return ResultUtils.success(spaceVO);
    }
}
