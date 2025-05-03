package top.yihoxu.ypicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/5/3  13:03
 * @description 用户更新（管理员）
 */
@Data
public class UserUpdateRequest  implements Serializable {

    private static final long serialVersionUID = 9110756679198127649L;
    /**
     * id
     */
    private Long id;


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

    /**
     * 用户角色：user/admin
     */
    private String userRole;


}
