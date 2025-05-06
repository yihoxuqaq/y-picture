package top.yihoxu.ypicturebackend.model.vo;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class PictureGradVO {

    // 页面URL（原始JSON字段名 purl）
    @Alias("purl")
    private String pageUrl;

    // 主图URL（原始JSON字段名 murl）
    @Alias("murl")
    private String mainImageUrl;

    // 缩略图URL（原始JSON字段名 turl）
    @Alias("turl")
    private String thumbnailUrl;


    // 标题（原始JSON字段名 t）
    @Alias("t")
    private String title;



}