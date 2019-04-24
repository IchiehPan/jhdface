package cn.jhd.face.client.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import android.text.TextUtils;

import cn.jhd.face.client.AccessTokenManager;
import cn.jhd.face.client.bean.UserBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ApiHttp {
    //private static final String DOMAIN = "http://ilaers.f3322.org:8066";
    private static final String DOMAIN = "http://192.168.8.211:8066";
    private static String secretKey = "whatthefuck??????????";
    private static final String FROM = "android";
    private static OkHttpClient mOkHttpClient;
    private static final MediaType MEDIA_TYPE_DATA = MediaType.parse("multipart/form-data");

    static {
        int cacheSize = 10 * 1024 * 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }

    //妫�鏌ユ洿鏂�
    public static void checkUpdate(CommonCallback callback) {
        //RequestParams params = new RequestParams("http://192.168.8.211:8888/update/");
        RequestParams params = new RequestParams(DOMAIN + "/update/");
        params.addQueryStringParameter("type", String.valueOf(1));
        x.http().get(params, callback);
    }

    /**
     * 璇锋眰鐧诲綍
     *
     * @param username String
     *                 password String
     * @param callback
     */
    public static void login(String username, String password, CommonCallback<String> callback) {
        RequestParams params = new RequestParams(DOMAIN + "/login/");
        params.addParameter("account", username);
        params.addParameter("password", password);
        params.addParameter("from", FROM);
        x.http().post(params, callback);
    }

    /**
     * 锟较达拷图片
     */
    public static void uploadFaceFile(List<String> faceFiles, Callback mCallback, Map<String, String> dataMap) {
        String url = DOMAIN + "/get_pictures/";
        /*File file = new File(faceFile);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM) 
                .addFormDataPart("file[]", "temp.jpg", fileBody)
				.addFormDataPart("flag","1")
				.addFormDataPart("step","1")
				.addFormDataPart("push","0")
                .build();
        */
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        //閬嶅巻map涓墍鏈夊弬鏁板埌builder
        multipartBodyBuilder.addFormDataPart("flag", "1");
        multipartBodyBuilder.addFormDataPart("step", "1");
        multipartBodyBuilder.addFormDataPart("push", "0");
        multipartBodyBuilder.addFormDataPart("from", "FROM");

        // 将参数带上
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            multipartBodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        //閬嶅巻paths涓墍鏈夊浘鐗囩粷瀵硅矾寰勫埌builder锛屽苟绾﹀畾key濡傗�渦pload鈥濅綔涓哄悗鍙版帴鍙楀寮犲浘鐗囩殑key
        if (faceFiles != null) {
            for (int i = 0; i < faceFiles.size(); i++) {
                File file = new File(faceFiles.get(i));
                multipartBodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_DATA, file));
            }
        }
        RequestBody requestBody = multipartBodyBuilder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(mCallback);
    }

    /**
     * 锟斤拷锟斤拷锟斤拷证锟斤拷锟斤拷
     */
    public static void getPictureLarge(String tempLargeFile, String file_name, Callback mCallback) {
        if (TextUtils.isEmpty(file_name)) {
            return;
        }
        String url = DOMAIN + "/get_pictures/large/";
        File file = new File(tempLargeFile);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "temp_large.jpg", fileBody)
                .addFormDataPart("file_name", file_name)
                .addFormDataPart("from", FROM)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(mCallback);
    }


    /**
     * 璇锋眰璁よ瘉浜鸿劯
     *
     * @param tempFaceFile
     * @param callback
     */
    public static void getPicture(String tempFaceFile, CommonCallback callback) {
        RequestParams params = new RequestParams(DOMAIN + "/get_pictures/");
        params.setMultipart(true);
        params.setConnectTimeout(5 * 1000);
        params.addBodyParameter("file", new File(tempFaceFile));
        params.addBodyParameter("flag", "1");
        params.addBodyParameter("step", "1");
        params.addBodyParameter("push", "0");
        params.addBodyParameter("from", FROM);
        x.http().post(params, callback);
    }


    /**
     * 璇锋眰璁よ瘉浜鸿劯
     *
     * @param callback
     */
    public static void getPictureLarge(String tempLargeFile, String file_name, CommonCallback callback) {
        RequestParams params = new RequestParams(DOMAIN + "/get_pictures/large/");
        params.setMultipart(true);
        params.setConnectTimeout(5 * 1000);
        params.addBodyParameter("file", new File(tempLargeFile));
        params.addBodyParameter("file_name", file_name);
        params.addBodyParameter("from", FROM);
        x.http().post(params, callback);
    }

    /**
     * upload
     *
     * @param tempFaceFile
     * @param callback
     */
    public static void upload(String tempFaceFile, UserBean userBean, CommonCallback callback) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        RequestParams params = new RequestParams(DOMAIN + "/upload_picture/first_upload/");
        params.setMultipart(true);
        params.setConnectTimeout(30 * 1000);
        params.addBodyParameter("file", new File(tempFaceFile));
        params.addBodyParameter("id", userBean.getUid());
        params.addBodyParameter("name", userBean.getRealname());
        params.addBodyParameter("gender", String.valueOf(userBean.getGender()));
        params.addBodyParameter("company", userBean.getDepartment());
        params.addBodyParameter("department", userBean.getDepartment());
        params.addBodyParameter("position", userBean.getPosition());
        int time = (int) (System.currentTimeMillis() / 1000);
        params.addBodyParameter("time", String.valueOf(time));
        params.addBodyParameter("from", FROM);
        params.addBodyParameter("token", AccessTokenManager.getToken());
        String sign = md5(time + AccessTokenManager.getToken() + "file" + secretKey);
        params.addBodyParameter("sign", sign);
        x.http().post(params, callback);
    }

    /**
     * 灏嗗瓧绗︿覆杞垚MD5鍊�
     *
     * @param string
     * @return
     */
    public static String md5(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
