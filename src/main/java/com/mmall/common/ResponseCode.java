package com.mmall.common;

/**
 *
 */
public enum ResponseCode {
    SUCCESS(0,"success"),
    ERROR(1,"error"),
    NEEG_LOGIN(10,"need_login"),
    ILLEGAL_ARGUEMENT(2,"illegal_argument");

    private final int code;
    private final String desc;
    ResponseCode(int code,String desc){
        this.code=code;
        this.desc=desc;
    }
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }

}
