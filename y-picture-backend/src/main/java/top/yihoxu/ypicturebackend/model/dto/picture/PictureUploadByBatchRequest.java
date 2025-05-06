package top.yihoxu.ypicturebackend.model.dto.picture;

import lombok.Data;

/**
 * @author yihoxu
 * @date 2025/5/6  15:21
 * @description 图片抓取
 */
@Data
public class PictureUploadByBatchRequest {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;
}

