package com.ningso.ningsodemo.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 获取 list数组
 * List<Bean> bList= new Bean().fromArray(jsonStr);
 * 获取 bean 对象
 * Bean b= new Bean().fromBean(jsonStr);
 * 这个方法会根据传入的 jsonStr 判断是数组还是对象自动返回正确的类型,就是说你可以用数组去接收它也可以用对象去接收它,这个要看你传递过去的 jsonStr
 * Bean b= new Bean().from(jsonStr);
 * <p/>
 * 使用 new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();创建Gson对象，没有@Expose注释的属性将不会被序列化
 */
public class JsonUtils {

    private static Gson gson = null;

    private static JsonParser jsonParser;

    public JsonUtils() {
    }

    static {
        if (gson == null) {
            gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
        }
    }

    /**
     * 将对象转换成json格式
     *
     * @param ts
     * @return
     */
    public static String objectToJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }

    /**
     * 将Str转换成cla类型的list数组
     *
     * @param s
     * @param cla
     * @return
     */
    public static <T extends Object> T jsonToBeanList(String s, Class<?> cla) {
        List<Object> ls = new ArrayList<>();
        JSONArray ss;
        try {
            ss = new JSONArray(s);
            for (int i = 0; i < ss.length(); i++) {
                String str = ss.getString(i);
                Object a = jsonToBean(str, cla);
                ls.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) ls;
    }

    /**
     * 返回cla 类型的list数组
     *
     * @param s
     * @param cla
     * @return
     */
    public static <T extends Object> T jsonToBeanList(String s, String cla) {

        List<Object> ls = new ArrayList<>();
        JSONArray ss;
        try {
            ss = new JSONArray(s);
            for (int i = 0; i < ss.length(); i++) {
                String str = ss.getString(i);
                Object a = jsonToBean(str, cla);
                ls.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (T) ls;
    }

    /**
     * 将jsonStr转换成cl对象
     *
     * @param jsonStr
     * @return
     */
    public static <T extends Object> T jsonToBean(String jsonStr, Class<?> cl) {
        Object obj = null;
        if (gson != null) {
            if (!TextUtils.isEmpty(jsonStr))
                obj = gson.fromJson(jsonStr, cl);
        }
        return (T) obj;
    }


    /**
     * 根据jsonStr转化成对象
     *
     * @param jsonStr
     * @param classType
     * @param <T>
     * @return
     */
    public static <T extends Object> T jsonToBean(String jsonStr, String classType) {
        Class c;
        try {
            c = Class.forName(classType);
        } catch (ClassNotFoundException e) {
            c = Object.class;
            e.printStackTrace();
        }
        return (T) jsonToBean(jsonStr, c);
    }


    /**
     * 根据jsonStr 自动转化成对象或者对象数组
     *
     * @param jsonStr
     * @param classType
     * @param <T>
     * @return
     */
    public static <T extends Object> T jsonAutoToBeanOrBeanList(String jsonStr, String classType) {
        if (jsonStr.trim().startsWith("[")) {
            return jsonToBeanList(jsonStr, classType);

        } else {
            return jsonToBean(jsonStr, classType);
        }
    }

    /**
     * 根据给的jsonStr自动转化成对象或者对象数组
     *
     * @param jsonStr
     * @param classType
     * @param <T>
     * @return
     */
    public static <T extends Object> T jsonAutoToBeanOrBeanList(String jsonStr, Class classType) {


        if (jsonStr.trim().startsWith("[")) {
            return (T) jsonToBeanList(jsonStr, classType);

        } else {
            return (T) jsonToBean(jsonStr, classType);
        }


    }

    /**
     * 将json格式转换成map对象
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }


    public static JsonParser getJsonParser() {
        if (jsonParser == null)
            jsonParser = new JsonParser();
        return jsonParser;
    }

    public static JsonObject parserJson(String paramString) {
        return (JsonObject) getJsonParser().parse(paramString);
    }

}
