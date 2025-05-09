package top.yihoxu.ypicturebackend.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/5/9  12:40
 * @description 团队空间增加成员
 */
@Data
public class SpaceUserAddRequest implements Serializable {

    private static final long serialVersionUID = -1148638660022746548L;
    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 空间角色
     */
    private String spaceRole;
}
