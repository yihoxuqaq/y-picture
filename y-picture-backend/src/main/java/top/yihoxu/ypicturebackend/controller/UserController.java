package top.yihoxu.ypicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yihoxu.ypicturebackend.annotation.AuthCheck;
import top.yihoxu.ypicturebackend.common.BaseResponse;
import top.yihoxu.ypicturebackend.common.DeleteRequest;
import top.yihoxu.ypicturebackend.common.ResultUtils;
import top.yihoxu.ypicturebackend.constant.UserConstant;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.manager.upload.FileManager;
import top.yihoxu.ypicturebackend.model.dto.user.*;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.UserVO;
import top.yihoxu.ypicturebackend.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author yihoxu
 * @date 2025/4/29  22:33
 * @description 用户模块接口
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private FileManager fileManager;

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> loginUser(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        UserVO userVO = userService.loginUser(userLoginRequest, request);
        return ResultUtils.success(userVO);
    }

    /**
     * 用户注册
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> registerUser(@RequestBody UserRegisterRequest userLoginRequest) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        long id = userService.registerUser(userLoginRequest);
        return ResultUtils.success(id);
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */

    @PostMapping("/logout")
    public BaseResponse<Boolean> logoutUser(HttpServletRequest request) {
        boolean result = userService.logoutUser(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登陆用户
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(loginUser, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/listUsers")
    public BaseResponse<Page<UserVO>> listUsersByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        Page<User> page = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        List<User> userList = page.getRecords();
        List<UserVO> listUsersVO = userService.listUsersVO(userList);
        Page<UserVO> userVOPage = new Page<>(current, pageSize, page.getTotal());
        userVOPage.setRecords(listUsersVO);
        return ResultUtils.success(userVOPage);

    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 用户更新（管理员）
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }


    @PostMapping("/edit")
    public BaseResponse<Boolean> userEdit(@RequestBody UserEditRequest userEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userEditRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userEditRequest, user);
        if (!StrUtil.isBlank(user.getUserPassword())) {
            String md5Password = userService.md5WithSalt(loginUser.getUserPassword());
            user.setUserPassword(md5Password);
        }
        user.setEditTime(new Date());
        boolean result = userService.updateById(user);
        if (!result) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(result);

    }

    /**
     * 用户上传头像
     *
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload/avatar")
    public BaseResponse<String> uploadUserAvatar(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        String userAvatarPath = fileManager.uploadUserAvatar(multipartFile);
        User user = new User();
        user.setId(loginUser.getId());
        user.setUserAvatar(userAvatarPath);
        userService.updateById(user);
        return ResultUtils.success(userAvatarPath);

    }


}
