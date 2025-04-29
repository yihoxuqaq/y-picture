package top.yihoxu.ypicturebackend.common;

import lombok.Data;

/**
 * @author yihoxu
 * @date 2025/4/29  19:39
 * @description 通用分页请求
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

