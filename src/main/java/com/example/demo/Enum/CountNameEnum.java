package com.example.demo.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CountNameEnum {
    COMMENT_COUNT("COMMENT_COUNT"),
   REPLY_COUNT( "REPLY_COUNT");

    String countName;

}
