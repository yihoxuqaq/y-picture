package top.yihoxu.ypicturebackend.model.dto.space;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.yihoxu.ypicturebackend.common.PageRequest;

import java.io.Serializable;

/**
 * 查询空间请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;


}
