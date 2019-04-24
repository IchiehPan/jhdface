package cn.jhd.face.client.widget.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.util.Log;

import cn.jhd.face.client.utils.CommonUtil;
import cn.jhd.face.client.utils.FileUtil;
import cn.jhd.face.client.utils.InfoUtil;

@SuppressLint("NewApi")
public class FindFaceView extends BaseFindFaceView {

    private static final String TAG = FindFaceView.class.getSimpleName();
    public static final int SHOW_DRAW_FACE_RECT = 0;
    public static final int DISMISS_DRAW_FACE_RECT = 1;
    //假设最多有2张脸
    private int maxNumOfFaces = 2;
    private int findFaceNumber;// 识别的人脸数
    private FaceDetector mFaceDetector;
    private FaceDetector.Face[] mFaces;
    private String tempFaceFile;
    private Bitmap mCacheBitmap;
    private FaceDetectorResult mFaceDetectorResult;

    private float scale = 1;
    //private Time time = new Time("GMT+8");


    public FindFaceView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FindFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
        mCamera.takePicture(shutter, raw, jpeg);
    }

    @Override
    public String processData(byte[] data, int width, int height, boolean isRetry) {
        setSpotAble(false);
        float mRotateDegree = 0.0f;
        switch (mPreview.getOrientionOfCamera()) {
            case 0:
                mRotateDegree = 0.0f;
                break;
            case 90:
                if (InfoUtil.getOrientation(getContext()) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    mRotateDegree = -90.0f;
                } else {
                    mRotateDegree = 0.0f;
                }
                break;
            case 180:
                mRotateDegree = -180.0f;
                break;
            case 270:
                mRotateDegree = -270.0f;
                break;
        }
        tempFaceFile = "";
        mCacheBitmap = yuvToBitmap(data, width, height, mRotateDegree);

        if (mCacheBitmap != null) {
            detectorAndTrackFace();
            if (mFaceDetectorResult != null && mFaceDetectorResult.getUsableFaceCount() > 0) {
                try {
                    FileUtil.saveBitmap(mCacheBitmap, "temp_large.jpg");
                } catch (IOException e) {
                    Log.e(TAG, "processData: ", e);
                }
                Bitmap cutBitmap = null;
                int left = mFaceDetectorResult.getMinRect().left + 80 > 0 ? mFaceDetectorResult.getMinRect().left + 80 : 0;
                int right = mFaceDetectorResult.getMinRect().right - mFaceDetectorResult.getMinRect().left + 80 < width ? mFaceDetectorResult.getMinRect().right - mFaceDetectorResult.getMinRect().left + 80 : width;
                try {
                    cutBitmap = Bitmap.createBitmap(mCacheBitmap, left, mFaceDetectorResult.getMinRect().top - 50, right, mFaceDetectorResult.getMinRect().bottom - mFaceDetectorResult.getMinRect().top + 50, null, false);
                } catch (Exception e) {
                    System.gc();
                    System.runFinalization();
                    cutBitmap = Bitmap.createBitmap(mCacheBitmap, left, mFaceDetectorResult.getMinRect().top - 50, right, mFaceDetectorResult.getMinRect().bottom - mFaceDetectorResult.getMinRect().top + 50, null, false);
                }
                if (cutBitmap != null) {
                    try {
                        tempFaceFile = FileUtil.saveBitmap(cutBitmap, "temp.jpg");
                    } catch (IOException e) {
                        Log.e(TAG, "processData: ", e);
                    }
                    cutBitmap.recycle();
                }
            }
            mCacheBitmap.recycle();
            return tempFaceFile;
        } else {
            return "";
        }
    }

    //识别并追踪人脸
    public void detectorAndTrackFace() {
        //mHandler.sendEmptyMessage(SHOW_DRAW_FACE_RECT);
        mFaceDetector = new FaceDetector(mCacheBitmap.getWidth(), mCacheBitmap.getHeight(), maxNumOfFaces);
        mFaces = new FaceDetector.Face[maxNumOfFaces];
        findFaceNumber = mFaceDetector.findFaces(mCacheBitmap, mFaces);
        Rect rect = mScanBoxView.getScanBoxAreaRect();
        if (findFaceNumber != 0) {
            scale = 1;
            float scaleX = CommonUtil.getScreenWidth(getContext()) / mCacheBitmap.getWidth();
            float scaleY = CommonUtil.getScreenHeight(getContext()) / mCacheBitmap.getHeight();
            if (mTrackFaceRectView != null) {
                mFaceDetectorResult = mTrackFaceRectView.drawRect(mFaces, findFaceNumber, rect, mCacheBitmap.getWidth(), mCacheBitmap.getHeight(), scaleX, scaleY);
            }
            mTrackFaceRectView.clean();
        } else {
            mFaceDetectorResult = null;
        }
    }

    public Bitmap yuvToBitmap(byte[] data, int width, int height, float mRotateDegree) {
        Bitmap bitmap = null;
        try {
            YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            if (yuvimage != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
                byte[] rawImage = stream.toByteArray();
                stream.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;   //默认8888
                options.inDither = true;
                options.inSampleSize = 1;
                SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options));//方便回收
                bitmap = (Bitmap) softRef.get();
                //TODO：此处可以对位图进行处理，如显示，保存等
                Matrix matrix = new Matrix();
                matrix.postRotate(mRotateDegree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            }
        } catch (Exception ex) {
            Log.e("Sys", "Error:" + ex.getMessage());
        }
        return bitmap;
    }


}
