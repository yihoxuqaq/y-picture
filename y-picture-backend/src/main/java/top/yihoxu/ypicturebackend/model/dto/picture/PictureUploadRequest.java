package top.yihoxu.ypicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/5/2  13:14
 * @description 图片上传请求
 */
@Data
public class PictureUploadRequest implements Serializable {

    private static final long serialVersionUID = 2012833172952176309L;
    /**
     * 图片id
     */
    private Long id;
    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 文件地址
     */
    private String fileUrl;

}
