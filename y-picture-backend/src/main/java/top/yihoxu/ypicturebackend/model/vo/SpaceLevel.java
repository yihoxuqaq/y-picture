package top.yihoxu.ypicturebackend.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 空间级别
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    /**
     * 名称
     */
    private final String text;

    /**
     * 值
     */
    private final int value;

    /**
     * 图片条数
     */
    private final long maxCount;

    /**
     * 空间大小
     */
    private final long maxSize;
}
