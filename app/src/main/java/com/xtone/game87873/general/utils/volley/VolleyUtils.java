package com.xtone.game87873.general.utils.volley;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.xtone.game87873.contants.ApiUrl;
import com.xtone.game87873.general.utils.AppLog;
import com.xtone.game87873.section.dialog.UserProgressDialog;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * VolleyUtils.java
 *
 * @author yangwj
 * @version 1.0
 * @creat-time：2015-6-10 下午3:52:23
 */
public class VolleyUtils {

    public static String SECRET = "87873API";

    private static RequestQueue mQueue;
    private static ImageLoader imageLoader;

    /**
     * 获取RequestQueue对象
     *
     * @param context 上下文
     * @return 返回RequestQueue对象
     */
    public static synchronized RequestQueue getRequestQueue(Context context) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mQueue;
    }

    /**
     * 获取imageLoader
     *
     * @param context
     * @return
     */
    public static synchronized ImageLoader getImageLoader(Context context) {
        if (imageLoader == null) {
            imageLoader = new ImageLoader(getRequestQueue(context),new BitmapCache());
        }
        return imageLoader;
    }

    /**
     * 发起请求，结果转为String
     *
     * @param url
     * @param params
     * @param tag
     * @param callback
     */
    public static void requestString(Context context, String url,
                                     final HashMap<String, String> params, Object tag,
                                     final VolleyCallback<String> callback) {
        requestToString(context, url, params, tag, false, true, callback);
    }

    public static void requestStringWithLoading(Context context, String url,
                                                final HashMap<String, String> params, Object tag,
                                                final VolleyCallback<String> callback) {
        requestToString(context, url, params, tag, true, true, callback);

    }

    public static void requestStringWithLoadingNoRetry(Context context,
                                                       String url, final HashMap<String, String> params, Object tag,
                                                       final VolleyCallback<String> callback) {
        requestToString(context, url, params, tag, true, false, callback);

    }

    public static void setURLImage(Context context, ImageView imageView,
                                   String url, int defaultImageRes, int failedImageRes) {
        setURLImage(context, imageView, url, defaultImageRes, failedImageRes,
                0, 0);

    }

    public static void setURLImage(Context context, ImageView imageView,
                                   String url, int defaultImageRes, int failedImageRes, int width,
                                   int height) {
        if (url == null) {
            url = "";
        }
        try {

            ImageListener listener = ImageLoader.getImageListener(imageView,defaultImageRes, failedImageRes);
            getImageLoader(context).get(url, listener, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发起请求，返回结果转化为String
     *
     * @param url           接口地址
     * @param params        需要传入的参数
     * @param tag           request设置的tag，一般传入当前类对象
     * @param isShowLoading 是否显示加载框,true-显示加载框
     * @param callback      结果处理的回调
     */
    public static void requestToString(Context context, String url,
                                       final HashMap<String, String> params, Object tag,
                                       final boolean isShowLoading, final VolleyCallback<String> callback) {
        requestToString(context, url, params, tag, isShowLoading, true,callback);
    }

    /**
     * 发起请求，返回结果转化为String
     *
     * @param url           接口地址
     * @param params        需要传入的参数
     * @param tag           request设置的tag，一般传入当前类对象
     * @param isShowLoading 是否显示加载框,true-显示加载框
     * @param isRetry       请求失败时是否重试
     * @param callback      结果处理的回调
     */
    public static void requestToString(Context context, final String url,
                                       final HashMap<String, String> params, Object tag,
                                       final boolean isShowLoading, boolean isRetry,
                                       final VolleyCallback<String> callback) {
        StringRequest stringRequest = new StringRequest(Method.POST, ApiUrl.BASE_URL + url,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (isShowLoading) {
                            UserProgressDialog.getInstane().dismiss();
                        }
                        if (callback != null) {
                            callback.onResponse(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (isShowLoading) {
                            UserProgressDialog.getInstane().dismiss();
                        }
                        if (callback != null) {
                            callback.onErrorResponse(error);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 重写该方法添加请求参数
                HashMap<String, String> map = new HashMap<>();
//				map.put("sign", MD5Utils.getMd5Str("87873API"));
                map.put("timestamp", System.currentTimeMillis() / 1000L + "");
                if (params != null) {
                    map.putAll(params);
                }
                //sign修改
                try {
                    map.put("sign", getSignature(map, SECRET));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                AppLog.redLog("---------url-------", ApiUrl.BASE_URL+url);
                AppLog.redLog("---------params-------", map.toString());
                return map;

            }
        };
        stringRequest.setTag(tag);
        if (!isRetry) {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
        if (isShowLoading) {
            UserProgressDialog.getInstane().show(context);
        }
        getRequestQueue(context).add(stringRequest);

    }

    /**
     * 签名生成算法
     *
     * @param  params 请求参数集，所有参数必须已转换为字符串类型
     * @param  secret 签名密钥
     * @return 签名
     * @throws IOException
     */
    public static String getSignature(HashMap<String, String> params,String secret) throws IOException {
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new TreeMap<>(params);
        Set<Entry<String, String>> entrys = sortedParams.entrySet();

        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder basestring = new StringBuilder();
        for (Entry<String, String> param : entrys) {
            basestring.append(param.getKey()).append("=") .append(param.getValue());
        }
        basestring.append(secret);

        // 使用MD5对待签名串求签
        byte[] bytes = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
        } catch (GeneralSecurityException ex) {
            throw new IOException(ex);
        }

        // 将MD5输出的二进制结果转换为小写的十六进制
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex);
        }
        return sign.toString();
    }
}
