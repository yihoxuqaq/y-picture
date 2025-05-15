package top.yihoxu.ypicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import top.yihoxu.ypicturebackend.constant.UserConstant;
import top.yihoxu.ypicturebackend.enums.UserRoleEnum;
import top.yihoxu.ypicturebackend.exception.BusinessException;
import top.yihoxu.ypicturebackend.exception.ErrorCode;
import top.yihoxu.ypicturebackend.exception.ThrowUtils;
import top.yihoxu.ypicturebackend.manager.auth.StpKit;
import top.yihoxu.ypicturebackend.model.dto.user.UserLoginRequest;
import top.yihoxu.ypicturebackend.model.dto.user.UserQueryRequest;
import top.yihoxu.ypicturebackend.model.dto.user.UserRegisterRequest;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.UserVO;
import top.yihoxu.ypicturebackend.service.UserService;
import top.yihoxu.ypicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hushi
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-04-29 22:30:52
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public UserVO loginUser(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "账号不能为空");
        String userPassword = userLoginRequest.getUserPassword();
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "密码不能为空");
        User user = this.lambdaQuery()
                .eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, md5WithSalt(userPassword))
                .one();
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账号或密码错误");
        }
        UserVO userVO = this.getUserVO(user);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        //记录用户登录态到Sa-token，
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(UserConstant.USER_LOGIN_STATE,user);
        return userVO;
    }

    @Override
    public long registerUser(UserRegisterRequest userRegisterRequest) {

        String userAccount = userRegisterRequest.getUserAccount();
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "账号不能为空");
        if (userAccount.length() < 3) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        String userPassword = userRegisterRequest.getUserPassword();
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "密码不能为空");
        if (userPassword.length() < 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        String checkPassword = userRegisterRequest.getCheckPassword();
        ThrowUtils.throwIf(StrUtil.isBlank(checkPassword), ErrorCode.PARAMS_ERROR, "确认密码不能为空");
        if (checkPassword.length() < 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "确认密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        boolean exists = this.lambdaQuery()
                .eq(User::getUserAccount, userAccount)
                .exists();
        ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "账号重复");
        User user = new User();
        BeanUtil.copyProperties(userRegisterRequest, user);
        user.setUserPassword(md5WithSalt(userPassword));
        user.setUserName("匿名用户");
        user.setUserProfile("该用户没有留下任何足迹。。。。");
        user.setUserRole(UserConstant.DEFAULT_ROLE);
        boolean result = save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "系统操作失败");
        }
        return user.getId();
    }

    @Override
    public boolean logoutUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户未登录");
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        user = getById(user.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userQueryRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        queryWrapper.eq(StrUtil.isNotBlank(userRole),"userRole",userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount),"userAccount",userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName),"userName",userName);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }


    /***
     * 密码md5加密
     * @param password
     * @return
     */
    @Override
    public String md5WithSalt(String password) {
        String salt = "yihoxu";
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        md5.setSalt(salt.getBytes());
        return md5.digestHex(password);
    }

    /**
     *
     * @param user
     * @return 对象转为封装类
     */
    @Override
    public UserVO getUserVO(User user){
        if (user==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> listUsersVO(List<User> userList) {
        if (CollUtil.isEmpty(userList)){
            return new ArrayList<>();
        }
        List<UserVO> userVOList = userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
        return userVOList;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

}




