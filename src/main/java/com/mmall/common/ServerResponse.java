package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**有些返回d不需要data 实际返回的是key v=null
 * JsonSerialize 调用没有data的构造器 就没有data了 就不生成key
 * @param <T>
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    /**
     * 私有构造器
     * @param status
     */
    private ServerResponse(int status){
        this.status=status;
    }
    private ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private ServerResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }

    /**返回的时候 会把ServerResponse序列化返回给前端 但是isSuccess（） public 序列化也会显示在json
     *加上Jsonignore 不显示  其他public方法显示 (对data...三个字段序列化)
     * @return
     */
    @JsonIgnore
   public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();//status 是0返回true
   }
   public int getStatus(){
        return status;
   }
   public T getData(){
        return data;
   }
   public String getMsg(){
        return msg;
   }
   public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
   }

    /**
     * 成功，返回文本供给前端提示使用
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    /**
     *
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }
    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
       return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorcode,String msg){
        return new ServerResponse<T>(errorcode,msg);
    }
}
