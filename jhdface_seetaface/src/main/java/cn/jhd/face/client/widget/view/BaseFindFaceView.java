package cn.jhd.face.client.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.jhd.face.client.R;
import cn.jhd.face.client.utils.CommonUtil;
import cn.jhd.face.client.utils.InfoUtil;

@SuppressLint("NewApi")
public abstract class BaseFindFaceView extends RelativeLayout implements Camera.PreviewCallback, ProcessDataTask.Delegate {
    private static final String TAG = BaseFindFaceView.class.getSimpleName();
    protected Camera mCamera;
    protected CameraPreview mPreview;
    protected ScanBoxView mScanBoxView;
    protected TrackFaceRectView mTrackFaceRectView;
    protected Delegate mDelegate;
    protected Handler mHandler;
    protected boolean mSpotAble = false;
    protected ProcessDataTask mProcessDataTask;
    protected int mOrientation;

    public BaseFindFaceView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseFindFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new Handler();
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mPreview = new CameraPreview(getContext());

        mScanBoxView = new ScanBoxView(getContext());
        mScanBoxView.initCustomAttrs(context, attrs);
        mPreview.setId(R.id.face_camera_preview);
        addView(mPreview);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(context, attrs);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, mPreview.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, mPreview.getId());
        addView(mScanBoxView, layoutParams);

        mTrackFaceRectView = new TrackFaceRectView(getContext());
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addView(mTrackFaceRectView, layoutParams);

        mOrientation = InfoUtil.getOrientation(context);
    }

    /**
     * 设置扫描二维码的代理
     *
     * @param delegate 扫描二维码的代理
     */
    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public ScanBoxView getScanBoxView() {
        return mScanBoxView;
    }

    public CameraPreview getPreview() {
        return mPreview;
    }

    /**
     * 显示扫描框
     */
    public void showScanRect() {
        if (mScanBoxView != null) {
            mScanBoxView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏扫描框
     */
    public void hiddenScanRect() {
        if (mScanBoxView != null) {
            mScanBoxView.setVisibility(View.GONE);
        }
    }

    /**
     * 打开前置摄像头开始预览，但是并未开始识别
     */
    public void startCamera() {
        startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * 打开指定摄像头开始预览，但是并未开始识别
     *
     * @param cameraFacing
     */
    public void startCamera(int cameraFacing) {
        if (mCamera != null) {
            return;
        }
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                startCameraById(cameraId);
                break;
            }
        }
    }

    private void startCameraById(int cameraId) {
        try {
            mCamera = Camera.open(cameraId);
            mPreview.setCamera(mCamera, this);
        } catch (Exception e) {
            Toast.makeText(getContext(), "open camera error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (mDelegate != null) {
                mDelegate.onScanFaceOpenCameraError();
            }
        }
    }

    /**
     * 关闭摄像头预览，并且隐藏扫描框
     */
    public void stopCamera() {
        try {
            stopSpotAndHiddenRect();
            if (mCamera != null) {
                mPreview.stopCameraPreview();
                mPreview.setCamera(null, null);
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "stopCamera: ", e);
        }
    }

    /**
     * 延迟0.2秒后开始识别
     */
    public void startSpot() {
        startSpotDelay(200);
    }

    /**
     * 延迟delay毫秒后开始识别
     *
     * @param delay
     */
    public void startSpotDelay(int delay) {
        mSpotAble = true;

        startCamera();
        // 开始前先移除之前的任务
        mHandler.removeCallbacks(mPreviewCallbackWithBufferTask);
        mHandler.postDelayed(mPreviewCallbackWithBufferTask, delay);
    }

    /**
     * 停止识别
     */
    public void stopSpot() {
        cancelProcessDataTask();

        mSpotAble = false;
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallbackWithBuffer(null);
            } catch (Exception e) {
                Log.e(TAG, "stopSpot: ", e);
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mPreviewCallbackWithBufferTask);
        }
    }


    public boolean isSpotAble() {
        return this.mSpotAble;
    }

    public void setSpotAble(boolean spotAble) {
        this.mSpotAble = spotAble;
    }

    /**
     * 停止识别，并且隐藏扫描框
     */
    public void stopSpotAndHiddenRect() {
        stopSpot();
        hiddenScanRect();
    }

    /**
     * 显示扫描框，并且延迟0.2秒后开始识别
     */
    public void startSpotAndShowRect() {
        startSpot();
        showScanRect();
    }

    /**
     * 打开闪光灯
     */
    public void openFlashlight() {
        mPreview.openFlashlight();
    }

    /**
     * 关闭散光灯
     */
    public void closeFlashlight() {
        mPreview.closeFlashlight();
    }

    /**
     * 销毁二维码扫描控件
     */
    public void onDestroy() {
        stopCamera();
        mHandler = null;
        mDelegate = null;
        mPreviewCallbackWithBufferTask = null;
    }

    /**
     * 取消数据处理任务
     */
    protected void cancelProcessDataTask() {
        if (mProcessDataTask != null) {
            mProcessDataTask.cancelTask();
            mProcessDataTask = null;
        }
    }


    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        final long t = System.currentTimeMillis();
        mCamera.addCallbackBuffer(data);
        if (isSpotAble()) {
            cancelProcessDataTask();
            mProcessDataTask = new ProcessDataTask(camera, data, this, mOrientation) {
                @Override
                protected void onPostExecute(String result) {
                    if (mDelegate != null && !TextUtils.isEmpty(result)) {
                        try {
                            mDelegate.onScanFaceSuccess(result, t);
                        } catch (Exception e) {
                        }
                    } else {
                        setSpotAble(true);
                        try {
                            camera.setPreviewCallbackWithBuffer(BaseFindFaceView.this);
                        } catch (Exception e) {
                            Log.e(TAG, "onPostExecute: ", e);
                        }
                    }
                }
            }.perform();
        }
    }


    private Runnable mPreviewCallbackWithBufferTask = new Runnable() {
        @Override
        public void run() {
            if (mCamera != null && mSpotAble) {
                try {
                    mCamera.setPreviewCallbackWithBuffer(BaseFindFaceView.this);
                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                }
            }
        }
    };


    public interface Delegate {
        /**
         * 处理扫描结果
         *
         * @param result
         */
        void onScanFaceSuccess(String result, long t);

        /**
         * 处理打开相机出错
         */
        void onScanFaceOpenCameraError();
    }

}
