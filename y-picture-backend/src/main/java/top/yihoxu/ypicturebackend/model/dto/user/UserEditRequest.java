package top.yihoxu.ypicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/5/3  16:10
 * @description 用户编辑
 */
@Data
public class UserEditRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

}
