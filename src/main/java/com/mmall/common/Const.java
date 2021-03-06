package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;


public class Const {
    public static final String CURRENT_USER="currentUser";
    public static final String EMAIL="email";
    public static final String USERNAME="username";
    public static final String SALT="";
    public interface Role{
        int ROLE_COSTUMER=0;//普通用户
        int ROLE_ADMIN=1;//管理员

    }
    public interface ProductListOrderby{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }
    public enum ProductStatusEnum{
        ON_SALE(1,"在售");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code=code;
            this.value=value;

        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
