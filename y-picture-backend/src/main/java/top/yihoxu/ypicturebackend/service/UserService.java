package top.yihoxu.ypicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import top.yihoxu.ypicturebackend.model.dto.user.UserQueryRequest;
import top.yihoxu.ypicturebackend.model.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yihoxu.ypicturebackend.model.vo.LoginUserVO;
import top.yihoxu.ypicturebackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author hushi
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-04-09 12:48:32
 */
public interface UserService extends IService<User> {


    /**
     * 账号注册
     *
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 账号登陆
     *
     * @param userAccount  账号
     * @param userPassword 密码
     * @param request
     * @return 脱敏登陆用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 账号退出
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取托敏的已登陆用户信息
     *
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取单个用户脱敏后信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取列表用户脱敏后信息
     */
    List<UserVO> getUserVOList(List<User> userList);


     QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取当前登陆用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);


    /**
     * 密码加密
     *
     * @param userPassword 原始密码
     * @return 加密密码
     */
    String getEncryptPassword(String userPassword);
}
