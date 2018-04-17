package com.xtone.game87873.general.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JsonUtils.java
 *
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-8-5 上午10:51:36
 */
public class JsonUtils {

    /**
     * 解析josn
     *
     * @param aObject
     * @param name
     * @return
     */
    public final static String getJSONString(JSONObject aObject, String name) {
        String ret = "";
        if (aObject != null) {
            try {
                Object obj = aObject.isNull(name) ? null : aObject.get(name);
                if (obj != null && (obj instanceof String || obj instanceof Integer)) {//instanceof:用于判断其左边对象是否为其右边类的实例
                    ret = String.valueOf(obj);
                }
            } catch (JSONException e) {
            }
        }
        return ret;
    }

    /**
     * 解析josn
     *
     * @param aObject
     * @param name
     * @return
     */
    public final static JSONObject getJSONObject(JSONObject aObject, String name) {
        JSONObject ret = null;
        if (aObject != null) {
            try {
                Object obj = aObject.isNull(name) ? null : aObject.get(name);
                if (obj != null && obj instanceof JSONObject) {
                    ret = (JSONObject) obj;
                }
            } catch (JSONException e) {
            }
        }
        return ret;
    }

    /**
     * 解析josn
     *
     * @param aObject
     * @param name
     * @return
     */
    public final static JSONArray getJSONArray(JSONObject aObject, String name) {
        JSONArray ret = null;
        if (aObject != null) {
            try {
                Object obj = aObject.isNull(name) ? null : aObject.get(name);
                if (obj != null && obj instanceof JSONArray) {
                    ret = (JSONArray) obj;
                }
            } catch (JSONException e) {
            }
        }
        return ret;
    }

    /**
     * 解析josn
     *
     * @param aObject
     * @param name
     * @return
     */
    public static long getJSONLong(JSONObject aObject, String name) {
        long ret = 0;
        if (aObject != null) {
            try {
                ret = aObject.getLong(name);
            } catch (JSONException e) {
            }
        }
        return ret;
    }

    /**
     * 解析josn
     *
     * @param aObject
     * @param name
     * @return
     */
    public static Double getJSONDouble(JSONObject aObject, String name) {
        Double ret = 0.0;
        if (aObject != null) {
            try {
                ret = aObject.getDouble(name);
            } catch (JSONException e) {
            }
        }
        return ret;
    }

    /**
     * 解析josn
     *
     * @param aObject
     * @param name
     * @return
     */
    public static int getJSONInt(JSONObject aObject, String name) {
        int ret = 0;
        if (aObject != null) {
            try {
                ret = aObject.getInt(name);
            } catch (JSONException e) {
            }
        }
        return ret;
    }
}
  