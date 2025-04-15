package top.yihoxu.ypicturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑空间请求（用户）
 */
@Data
public class SpaceEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

}
