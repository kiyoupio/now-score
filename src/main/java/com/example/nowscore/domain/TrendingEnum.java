package com.example.nowscore.domain;

import lombok.Getter;

@Getter
public enum TrendingEnum {
    NULL(null, -1),
    GREEN_RED_RED("全绿红红", 1),
    GREEN_GREEN_RED("全绿绿红", 2),
    RED_RED_GREEN("全红红绿", 3),
    RED_GREEN_GREEN("全红绿绿", 4),
    RED_GREEN_RED("全红绿红", 5);

    private String desc;
    private int type;

     TrendingEnum(String desc, int type){
        this.desc = desc;
        this.type = type;
    }

    public static String getTypeName(Integer code) {
        String result = "";
        for (TrendingEnum typeEnum : TrendingEnum.values()) {
            if (typeEnum.getType() == code) {
                result = typeEnum.getDesc();
                break;
            }
        }
        return result;
    }
}
