package com.pwhxbdk.utils;

import java.util.Objects;

/**
 * @author pwhxbdk
 * @date 2020/4/12
 */
public enum BaseTypeEnum {
    /**
     * 基本数据类型 包装类
     */
    BYTE("byte", "java.lang.Byte"),
    CHAR("char", "java.lang.Character"),
    DOUBLE("double", "java.lang.Double"),
    FLOAT("float", "java.lang.Float"),
    INT("int", "java.lang.Integer"),
    LONG("long", "java.lang.Long"),
    SHORT("short", "java.lang.Short"),
    BOOLEAN("boolean", "java.lang.Boolean"),

    STRING("string", "java.lang.String"),

    ;

    /**
     * 基础类的名称
     */
    private String name;
    /**
     * 包装类的名称
     */
    private String boxedTypeName;

    BaseTypeEnum(String name, String boxedTypeName) {
        this.name = name;
        this.boxedTypeName = boxedTypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoxedTypeName() {
        return boxedTypeName;
    }

    public void setBoxedTypeName(String boxedTypeName) {
        this.boxedTypeName = boxedTypeName;
    }

    /**
     * 根据包装类名称获取基本数据类型
     * @param boxedTypeName 包装类名
     * @return 基础数据类名
     */
    public static String findByName(String boxedTypeName) {
        for (BaseTypeEnum type : values()) {
            if (Objects.equals(type.getBoxedTypeName(), boxedTypeName)) {
                return type.getName();
            }
        }
        return "";
    }

    public static boolean isName(String name) {
        for (BaseTypeEnum type : values()) {
            if (Objects.equals(type.getName(), name)) {
                return true;
            }
        }
        return false;
    }
}
