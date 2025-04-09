package top.yihoxu.ypicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号注册请求
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 5113704455329790329L;
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
