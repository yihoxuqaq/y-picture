package top.yihoxu.ypicturebackend.model.dto.picture;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 批量编辑图片请求
 */
@Data
public class PictureEditByBatchRequest implements Serializable {

    private static final long serialVersionUID = 4853238689512834852L;
    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 图片id列表
     */
    private List<Long> pictureIdList;


    /**
     * 图片分类
     */
    private String category;
    /**
     * 命名规则
     */
    private String nameRule;


    /**
     * 图片标签列表
     */
    private List<String> tagList;


}
