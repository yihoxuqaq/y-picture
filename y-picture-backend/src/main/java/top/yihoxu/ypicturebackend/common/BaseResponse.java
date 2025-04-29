package top.yihoxu.ypicturebackend.common;

import lombok.Data;
import top.yihoxu.ypicturebackend.exception.ErrorCode;

import java.io.Serializable;

/**
 * @author yihoxu
 * @date 2025/4/29  19:34
 * @description 自定义响应
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
