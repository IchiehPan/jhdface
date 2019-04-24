package cn.jhd.face.client.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import cn.jhd.face.client.bean.base.Entity;


public class VersionBean extends Entity {

    private static final String TAG = VersionBean.class.getSimpleName();
    public static final String VERSION_CODE = "versionCode";
    public static final String VERSION_NAME = "versionName";
    public static final String DOWNLOAD_URL = "downloadUrl";
    public static final String UPDATE_MSG = "updateMsg";

    private int versionCode;
    private String versionName;
    private String downloadUrl;
    private String updateMsg;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    public static VersionBean parseJSON(String response) {
        VersionBean mVersionBean = new VersionBean();
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject object = new JSONObject(response);
                if (object.has(VERSION_CODE)) {
                    mVersionBean.setVersionCode(object.getInt(VERSION_CODE));
                }
                if (object.has(VERSION_NAME)) {
                    mVersionBean.setVersionName(object.getString(VERSION_NAME));
                }
                if (object.has(DOWNLOAD_URL)) {
                    mVersionBean.setDownloadUrl(object.getString(DOWNLOAD_URL));
                }
                if (object.has(UPDATE_MSG)) {
                    mVersionBean.setUpdateMsg(object.getString(UPDATE_MSG));
                }
            } catch (JSONException e) {
                Log.e(TAG, "parseJSON: ", e);
                mVersionBean = null;
            }
        }
        return mVersionBean;
    }
}
