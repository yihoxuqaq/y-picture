package top.yihoxu.ypicturebackend.model.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.yihoxu.ypicturebackend.common.PageRequest;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/5/1  13:24
 * @description 用户请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 7659710798922048158L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

}
