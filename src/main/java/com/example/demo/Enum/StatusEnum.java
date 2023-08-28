package com.example.demo.Enum;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {

    FALSE(0,"false"),
    TRUE(1,"true");

    private int code;

    private String name;

    public static String GetName(Integer code) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (code.equals(statusEnum.getCode())) {
                return statusEnum.getName();
            }
        }
        return null;
    }
    public static Integer GetCode(String name) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (name.equals(statusEnum.getName())) {
                return statusEnum.getCode();
            }
        }
        return null;
    }
}
