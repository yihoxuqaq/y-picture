package top.yihoxu.ypicturebackend.enums;

import cn.hutool.core.util.ObjUtil;
import io.swagger.models.auth.In;
import lombok.Getter;

/**
 * @author yihoxu
 * @date 2025/5/8  12:58
 * @description 空间类型
 */
@Getter
public enum SpaceTypeEnum {


    PRIVATE("个人空间", 0),
    TEAM("团队空间", 1);

    private final String text;

    private final Integer value;

    SpaceTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 根据value 获取spaceTypeEnum
     *
     * @param value
     * @return
     */
    public static SpaceTypeEnum getSpaceTypeEnumByValue(Integer value) {
        if (ObjUtil.isNull(value)) {
            return null;
        }
        for (SpaceTypeEnum typeEnum : SpaceTypeEnum.values()) {
            if (value.equals(typeEnum.value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
