package com.mmall.util;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormat;
import java.util.Date;

/**
 * 使用joda-time实现时间格式转换的工具类
 */
public class DataTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static Date strToData(String time){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(time);
        return dateTime.toDate();
    }
    public static String dateToStr(Date date){
        if(date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }


}
