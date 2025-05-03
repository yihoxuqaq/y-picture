package top.yihoxu.ypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.ypicturebackend.model.dto.user.UserLoginRequest;
import top.yihoxu.ypicturebackend.model.dto.user.UserQueryRequest;
import top.yihoxu.ypicturebackend.model.dto.user.UserRegisterRequest;
import top.yihoxu.ypicturebackend.model.entity.User;
import top.yihoxu.ypicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author hushi
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-04-29 22:30:52
*/
public interface UserService extends IService<User> {


    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    UserVO loginUser(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 用户注册
     * @param userLoginRequest
     * @return
     */
    long registerUser(UserRegisterRequest userLoginRequest);

    /**
     * 用户退出
     * @param request
     */
    boolean logoutUser(HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 构建查询
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    String md5WithSalt(String password);

    /**
     *
     * @param user
     * @return 对象转为封装类
     */
    public UserVO getUserVO(User user);


    /**
     * 列表对象转为列表封装类
     * @param userList
     * @return
     */
    List<UserVO> listUsersVO(List<User> userList);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}
