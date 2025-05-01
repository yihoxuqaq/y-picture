package top.yihoxu.ypicturebackend.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * @author yihoxu
 * @date 2025/5/1  13:56
 * @description 用户权限枚举
 */
@Getter
public enum UserRoleEnum {

    USER("用户", "user"),

    ADMIN("管理员", "admin");

    private final String text;
    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value 获取 enum
     * @param value
     * @return
     */
    public static UserRoleEnum getUserEnumByValue(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        for (UserRoleEnum role : UserRoleEnum.values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        return null;
    }
}
