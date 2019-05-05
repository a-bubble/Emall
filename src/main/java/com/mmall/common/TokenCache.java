package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.LoggerFactory;

public class TokenCache {
    private static Logger loger= LoggerFactory.getLogger(TokenCache.class);
    public static final String TOKEN_PREFIX="Token_";
    private static LoadingCache<String, String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).build(new CacheLoader<String, String>() {
        @Override//默认的数据加载实现 调用get取值 key无值 就调用这个方法匿名实现
        public String load(String s) throws Exception {
            return "null";
        }
    });
    public static void setKey(String key,String value){
     localCache.put(key,value);
    }
    public static String getKey(String key){
        String value=null;
        try{
            value=localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
          loger.error("Cache get error",e);
        }
        return null;
    }

}
