package top.yihoxu.ypicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/5/3  19:16
 * @description 图片审核
 */
@Data
public class PictureReviewRequest implements Serializable {

    private static final long serialVersionUID = 7945096765129805679L;
    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;


}
