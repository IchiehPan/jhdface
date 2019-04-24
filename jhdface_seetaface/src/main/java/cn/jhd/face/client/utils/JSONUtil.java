package cn.jhd.face.client.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.text.TextUtils;

import cn.jhd.face.client.bean.ResponseBean;

public class JSONUtil {
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String PAGES = "pages";
    public static final String FILE_NAME = "file_name";
    public static final String DATA = "data";

    public static List parseList(String response, ResponseBean mResult, Class oClass) throws JSONException {
        List list = new ArrayList();
        if (!TextUtils.isEmpty(response)) {
            JSONObject object = new JSONObject(response);
            if (object.has(CODE)) {
                mResult.setCode(object.getInt(CODE));
            }
            if (object.has(MSG)) {
                mResult.setMsg(object.getString(MSG));
            }
            if (object.has(PAGES)) {
                mResult.setPages(object.getInt(PAGES));
            }
            if (object.has(FILE_NAME)) {
                mResult.setFile_name(object.getString(FILE_NAME));
            }
            if (object.has(DATA)) {
                JSONArray array = object.getJSONArray(DATA);
                Gson gson = new Gson();
                for (int i = 0; i < array.length(); i++) {
                    list.add(gson.fromJson(array.getJSONObject(i).toString(), oClass));
                }
            }
        } else {
            mResult.setCode(ResponseBean.NETWORK_ERROR);
        }

        return list;
    }

    public static Object parseObject(String response, ResponseBean mResult, Class oClass) throws JSONException {
        Object objectBean = null;
        if (!TextUtils.isEmpty(response)) {
            JSONObject object = new JSONObject(response);
            if (object.has(CODE)) {
                mResult.setCode(object.getInt(CODE));
            }
            if (object.has(MSG)) {
                mResult.setMsg(object.getString(MSG));
            }
            if (object.has(PAGES)) {
                mResult.setPages(object.getInt(PAGES));
            }
            if (oClass != null) {
                if (object.has(DATA)) {
                    JSONObject obj = object.getJSONObject(DATA);
                    Gson gson = new Gson();
                    objectBean = gson.fromJson(obj.toString(), oClass);
                }
            }
        } else {
            mResult.setCode(ResponseBean.NETWORK_ERROR);
        }
        return objectBean;
    }
}
