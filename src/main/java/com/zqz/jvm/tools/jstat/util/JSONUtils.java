package com.zqz.jvm.tools.jstat.util;

import com.alibaba.fastjson.JSONArray;

import java.util.Arrays;

/**
 * JSON工具类
 * Created by sangjian on 2018/2/17.
 */
public class JSONUtils {

    /**
     * 将以空格分隔的字符串转换为JSONArray
     * @param s 要被转换的字符串
     * @return
     */
    public static JSONArray stringWithSpace2JSONArray(String s){
        if(s == null){
            return new JSONArray();
        }
        String[] cols = s.trim().split("\\s+");
        JSONArray arr = new JSONArray(Arrays.asList(cols));
        return arr;
    }

}
