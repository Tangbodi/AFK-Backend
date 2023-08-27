package com.example.demo.Enum;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {

    FALSE(0,"false"),
    TRUE(1,"true");

    /**
     * 点赞状态码
     */
    private int code;
    /**
     * 点赞状态
     */
    private String name;

    public static String GetName(Integer code) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum.getName();
            }
        }
        return null;
    }
}
