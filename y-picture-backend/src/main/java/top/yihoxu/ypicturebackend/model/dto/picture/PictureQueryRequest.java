package top.yihoxu.ypicturebackend.model.dto.picture;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.yihoxu.ypicturebackend.common.PageRequest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author yihoxu
 * @date 2025/5/2  18:03
 * @description 图片查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 7301560463261560069L;
    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 搜索词（同时搜名称、简介等）
     */
    private String searchText;

    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 id
     */
    private Long reviewerId;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 是否访问公共空间
     */
    private Boolean nullSpaceId;

    /**
     * 开始上传时间
     */
    private Date startCreateTime;

    /**
     * 结束上传时间
     */
    private Date endCreateTime;



}

