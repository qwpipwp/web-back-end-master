package org.fatmansoft.teach.util;

import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.util.Map;

public class CalculatorUtil {

    public static String getJSONString(int code){
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }
}