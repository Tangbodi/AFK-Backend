package com.example.demo.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ObjectNameEnum {

    POST_LIKE_SET(0, "post_like"),
    COMMENT_LIKE_SET(1, "comment_like"),
    REPLY_LIKE_SET(2, "reply_like"),
    POST_SAVE_SET(3, "post_save");

    private int type;

    private String typeName;

    /**
     * traverse all enum
     */
    public static String GetTypeName(Integer typeId) {
        for (ObjectNameEnum objectNameEnum : ObjectNameEnum.values()) {
            if (typeId.equals(objectNameEnum.getType())) {
                return objectNameEnum.getTypeName();
            }
        }
        return null;
    }

}
