package cn.jhd.face.client.listener;

import android.content.res.Configuration;
import android.os.Handler;
import android.text.TextUtils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.Map;

import cn.jhd.face.client.constant.Constants;
import cn.jhd.face.client.observer.FrameObserver;
import cn.jhd.face.client.seetaface.JniClient;

public class CvCameraViewListener2 implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = CvCameraViewListener2.class.getSimpleName();
    Map<String, Object> faceInfoMap = new HashMap();

    private Mat mRgba;
    private Mat mGray;
    //大图

    private JniClient jniClient = new JniClient();

    private Handler mHandle;
    private int mOrientation;

    //创建观察者对象
    FrameObserver frameObserver;

    public CvCameraViewListener2(Handler handler, int orientation) {
        mHandle = handler;
        mOrientation = orientation;
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        frameObserver = new FrameObserver(mHandle);
    }

    @Override
    public void onCameraViewStopped() {
        if (mGray != null) {
            mGray.release();
        }
        if (mRgba != null) {
            mRgba.release();
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            Core.rotate(mRgba, mRgba, Core.ROTATE_90_COUNTERCLOCKWISE);
            Core.rotate(mGray, mGray, Core.ROTATE_90_COUNTERCLOCKWISE);
        }

        //上下翻转代码
        Core.flip(mRgba, mRgba, 0);
        Core.flip(mGray, mGray, 0);

        int scale = 4;
        Rect[] facesArray = null;
        Mat smallMat = new Mat();
        Size size = new Size(mGray.width() / scale, mGray.height() / scale);
        Imgproc.resize(mGray, smallMat, size, 0, 0, Imgproc.INTER_LINEAR);

        byte[] grayData = new byte[smallMat.cols() * smallMat.rows()];
        smallMat.get(0, 0, grayData);
        //妫�娴嬩汉鑴�
        String tRetStr = jniClient.DetectFace(grayData, smallMat.width(), smallMat.height(), smallMat.channels(), Constants.DEFAULT_FACEBIN_PATH, 20, 2.0f, 0.8f);
        if (!TextUtils.isEmpty(tRetStr) && !tRetStr.equals("")) {
            String[] tFaceStrs = tRetStr.split(";");
            int face_num = tFaceStrs.length;
            facesArray = new Rect[face_num];
            for (int i = 0; i < face_num; i++) {
                // TODO 此处有坑, 已经填上
                String[] vals = tFaceStrs[i].split(",");
                int x = Integer.valueOf(vals[0]) * scale;
                int y = Integer.valueOf(vals[1]) * scale;
                int w = Integer.valueOf(vals[2]) * scale;
                int h = Integer.valueOf(vals[3]) * scale;
                facesArray[i] = new Rect(x, y, w, h);
            }
        }

        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2RGB, 3);

        if (facesArray != null && facesArray.length != 0) {
            faceInfoMap.put("facesArray", facesArray);
            faceInfoMap.put("mRgba", mRgba);
            frameObserver.onChanged(faceInfoMap);
        }

        // 绘制区域
        Scalar FACE_RECT_COLOR = new Scalar(255, 255, 255, 255);
        if (facesArray != null) {
            for (int i = 0; i < facesArray.length; i++) {
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 1);
            }
        }


        //每个设备不一样，有些可能要先转成rgb
        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_RGB2BGR, 3);

        return mRgba;
    }

}