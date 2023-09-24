package com.example.demo.Constant.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ObjectNameEnum {
    POST_LIKE_SET(0, "POST_LIKE"),
    COMMENT_LIKE_SET(1, "COMMENT_LIKE"),
    REPLY_LIKE_SET(2, "REPLY_LIKE"),
    POST_SAVE_SET(3, "POST_SAVE"),
    SAVED_GAME_SET(4, "SAVED_GAME");

    private int typeCode;

    private String typeName;

    /**
     * traverse all enum
     */
    public static String GetTypeName(Integer typeId) {
        for (ObjectNameEnum objectNameEnum : ObjectNameEnum.values()) {
            if (typeId.equals(objectNameEnum.getTypeCode())) {
                return objectNameEnum.getTypeName();
            }
        }
        return null;
    }
    public static Integer GetTypeCode(String typeName) {
        for (ObjectNameEnum objectNameEnum : ObjectNameEnum.values()) {
            if (typeName.equals(objectNameEnum.getTypeName())) {
                return objectNameEnum.getTypeCode();
            }
        }
        return null;
    }
}
