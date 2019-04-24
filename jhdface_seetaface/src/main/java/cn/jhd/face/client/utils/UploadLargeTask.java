package cn.jhd.face.client.utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;

public class UploadLargeTask extends AsyncTask<String, Void, String> {

    @SuppressLint("NewApi")
    public UploadLargeTask perform(String... params) {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute(params);
        }
        return this;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        if (params != null && params.length > 0) {
            String smallFile = params[0];
            String tempLargeFile = smallFile.replace("temp.jpg", "temp_large.jpg");
            String largeFileName = params[1];
            if (!TextUtils.isEmpty(largeFileName)) {
                ApiHttp.getPictureLarge(tempLargeFile, largeFileName, new Callback() {

                    @Override
                    public void onFailure(Call arg0, IOException arg1) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onResponse(Call arg0, Response arg1)
                            throws IOException {
                        // TODO Auto-generated method stub

                    }

                });
            }
        }
        return "";
    }

}
