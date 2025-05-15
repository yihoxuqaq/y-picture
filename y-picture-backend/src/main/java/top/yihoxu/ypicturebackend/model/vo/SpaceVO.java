package top.yihoxu.ypicturebackend.model.vo;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import top.yihoxu.ypicturebackend.model.entity.Space;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yihoxu
 * @date 2025/5/4  14:36
 * @description 空间视图
 */
@Data
public class SpaceVO implements Serializable {
    private static final long serialVersionUID = 942485224500146664L;
    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    private Long totalCount;

    /**
     * 空间类型：0-私有 1-团队
     */
    private Integer spaceType;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 权限列表
     */
    private List<String> permissionList = new ArrayList<>();


    public static SpaceVO objToVo(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtil.copyProperties(space, spaceVO);
        return spaceVO;
    }
}
