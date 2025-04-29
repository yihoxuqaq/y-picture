package top.yihoxu.ypicturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/4/29  19:38
 * @description 通用删除请求
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
