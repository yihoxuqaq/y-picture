package top.yihoxu.ypicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/4/29  22:45
 * @description 用户注册请求
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -5889829537585619907L;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
    /**
     * 确认密码
     */
    private String checkPassword;


}
