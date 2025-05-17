package top.yihoxu.ypicturebackend.model.dto.picture;

import lombok.Data;
import top.yihoxu.ypicturebackend.api.aliyunai.CreateOutPaintingTaskRequest;

import java.io.Serializable;

@Data
public class CreatePictureOutPaintingTaskRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1621501251122471363L;
}
