package cn.jhd.face.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class AccessTokenManager {
    private static SharedPreferences sp;

    private static final String USER_CONF = "user.conf";
    private static final String UID = "uid";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String ACCESS_ACCOUT = "account";
    private static final String TOKEN_EXPIRE_TIME = "tokenExpireTime";

    static {
        if (sp == null) {
            sp = BaseApp._context.getSharedPreferences(USER_CONF, Context.MODE_PRIVATE);
        }
    }

    /**
     * 是否登陆
     */
    public static boolean isLogin() {
        String accessToken = sp.getString(ACCESS_TOKEN, "");
        long tokenExpireTime = sp.getLong(TOKEN_EXPIRE_TIME, 0);
        if (!TextUtils.isEmpty(accessToken)) {
            if (System.currentTimeMillis() < tokenExpireTime * 1000) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 退出登录
     */
    public static void logout() {
        sp.edit().putString(ACCESS_TOKEN, "")
                .putLong(TOKEN_EXPIRE_TIME, 0)
                .putString(ACCESS_ACCOUT, "")
                .commit();
    }

    /**
     * 保存token
     */
    public static void storeToken(String token, long expireTime, String account) {
        sp.edit().putString(ACCESS_TOKEN, token)
                .putLong(TOKEN_EXPIRE_TIME, expireTime)
                .putString(ACCESS_ACCOUT, account)
                .commit();
    }

    public static String getToken() {
        return sp.getString(ACCESS_TOKEN, "");
    }

    public static String getAccount() {
        return sp.getString(ACCESS_ACCOUT, "");
    }

    public static long getExpireTime() {
        return sp.getLong(TOKEN_EXPIRE_TIME, 0);
    }
}
