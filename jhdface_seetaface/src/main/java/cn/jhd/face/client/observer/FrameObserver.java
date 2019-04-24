package cn.jhd.face.client.observer;

import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jhd.face.client.bean.ResponseBean;
import cn.jhd.face.client.bean.UserBean;
import cn.jhd.face.client.constant.Constants;
import cn.jhd.face.client.utils.ApiHttp;
import cn.jhd.face.client.utils.JSONUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FrameObserver implements Observer {
    private static final String TAG = FrameObserver.class.getSimpleName();

    private String file;
    private String largeFile = Constants.DEFAULT_SAVE_IMAGE_PATH + "temp_large.jpg";
    private Handler mHandle;

    public FrameObserver(Handler mHandle) {
        this.mHandle = mHandle;
    }

    @Override
    public void onChanged(@Nullable Object object) {
        Map<String, Object> faceInfoMap = (Map<String, Object>) object;
        Rect[] facesArray = (Rect[]) faceInfoMap.get("facesArray");
        Mat mRgba = (Mat) faceInfoMap.get("mRgba");

        List<String> files = new ArrayList<>();
        Mat mLargeBgr = new Mat();
        Size zeroSize = new Size();

        //drawable = MatUtil.adjustBrightness(drawable);
        //if( !MatUtil.isBlur(drawable) ) {
        for (int i = 0; i < facesArray.length; i++) {
            file = Constants.DEFAULT_SAVE_IMAGE_PATH + "temp" + i + ".jpg";
//            Log.i(TAG, "onChanged: roi.x=" + facesArray[i].x + ", roi.y=" + facesArray[i].y + ", roi.width=" + facesArray[i].width + ", roi.height=" + facesArray[i].height);
            if (0 > facesArray[i].x || 0 > facesArray[i].width || facesArray[i].x + facesArray[i].width > mRgba.cols() || 0 > facesArray[i].y || 0 > facesArray[i].height || facesArray[i].y + facesArray[i].height > mRgba.rows()) {
                continue;
            }

            Mat mSmallBgr = mRgba.submat(facesArray[i]);
            if (Imgcodecs.imwrite(file, mSmallBgr)) {
                files.add(file);
            }
        }

        if (files.size() == 0) {
            return;
        }

        Imgproc.resize(mRgba, mLargeBgr, zeroSize, 0.5, 0.5, Imgproc.INTER_LINEAR);
        Imgcodecs.imwrite(largeFile, mLargeBgr);

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("mac_addr", Constants.macAddress);

        ApiHttp.uploadFaceFile(files, new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String response = arg1.body().string();
                ResponseBean mResponseBean = new ResponseBean();
                List<UserBean> userBeans = new ArrayList<>();
                if (TextUtils.isEmpty(response)) {
                    return;
                }

                try {
                    userBeans = JSONUtil.parseList(response, mResponseBean, UserBean.class);
                } catch (JSONException e) {
                    userBeans.clear();
                    Log.e(TAG, "onResponse: ", e);
                }
                Log.d(TAG, "onResponse: response=" + response);
                if (mResponseBean.getCode() != ResponseBean.SUCCESS) {
                    return;
                }

                ApiHttp.getPictureLarge(largeFile, mResponseBean.getFile_name(), new Callback() {
                    @Override
                    public void onResponse(Call arg0, Response resp) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onFailure(Call arg0, IOException ioe) {
                        // TODO Auto-generated method stub
                    }
                });
                Message message = new Message();
                message.what = Constants.SHOW_RESULT_PANEL;
                message.obj = userBeans;
                mHandle.sendMessage(message);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO Auto-generated method stub
            }
        }, dataMap);
    }
}
