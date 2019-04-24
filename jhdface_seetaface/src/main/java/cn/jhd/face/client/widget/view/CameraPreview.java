package cn.jhd.face.client.widget.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getSimpleName();
    private Camera mCamera;
    private boolean mPreviewing = true;
    private boolean mSurfaceCreated = false;
    private CameraConfigurationManager mCameraConfigurationManager;
    private byte[] mPreBuffer;
    private Camera.PreviewCallback mPreviewCallback;

    public CameraPreview(Context context) {
        super(context);
    }

    public void setCamera(Camera camera, Camera.PreviewCallback pCallback) {
        mCamera = camera;
        mPreviewCallback = pCallback;
        if (mCamera != null) {
            mCameraConfigurationManager = new CameraConfigurationManager(getContext());
            mCameraConfigurationManager.initFromCameraParameters(mCamera);

            getHolder().addCallback(this);
            if (mPreviewing) {
                requestLayout();
            } else {
                showCameraPreview();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        stopCameraPreview();

        post(() -> showCameraPreview());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = false;
        stopCameraPreview();
    }

    public void showCameraPreview() {
        if (mCamera != null) {
            try {
                mPreviewing = true;
                mCamera.setPreviewDisplay(getHolder());

                mCameraConfigurationManager.setDesiredCameraParameters(mCamera);
                int size = mCamera.getParameters().getPreviewSize().width * mCamera.getParameters().getPreviewSize().height;
                size = size * ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat()) / 8;

                mPreBuffer = new byte[size];
                mCamera.addCallbackBuffer(mPreBuffer);
                mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
                mCamera.startPreview();

                mCamera.autoFocus(autoFocusCB);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public int getOrientionOfCamera() {
        if (mCameraConfigurationManager != null) {
            return mCameraConfigurationManager.getOrientionOfCamera();
        } else {
            return 0;
        }
    }

    public Point getPreviewResolution() {
        if (mCameraConfigurationManager != null) {
            return mCameraConfigurationManager.getPreviewResolution();
        } else {
            return new Point();
        }
    }

    public void stopCameraPreview() {
        if (mCamera != null) {
            try {
                removeCallbacks(doAutoFocus);

                mPreviewing = false;
                mCamera.cancelAutoFocus();
                mCamera.setOneShotPreviewCallback(null);
                mCamera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, "stopCameraPreview: ", e);
            }
        }
    }

    public void openFlashlight() {
        if (flashLightAvailable()) {
            mCameraConfigurationManager.openFlashlight(mCamera);
        }
    }

    public void closeFlashlight() {
        if (flashLightAvailable()) {
            mCameraConfigurationManager.closeFlashlight(mCamera);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        if (mCameraConfigurationManager != null && mCameraConfigurationManager.getCameraResolution() != null) {
            Point cameraResolution = mCameraConfigurationManager.getCameraResolution();
            // 取出来的cameraResolution高宽值与屏幕的高宽顺序是相反的
            int cameraPreviewWidth = cameraResolution.x;
            int cameraPreviewHeight = cameraResolution.y;
            if (width * 1f / height < cameraPreviewWidth * 1f / cameraPreviewHeight) {
                float ratio = cameraPreviewHeight * 1f / cameraPreviewWidth;
                width = (int) (height / ratio + 0.5f);
            } else {
                float ratio = cameraPreviewWidth * 1f / cameraPreviewHeight;
                height = (int) (width / ratio + 0.5f);
            }
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }


    private boolean flashLightAvailable() {
        return mCamera != null && mPreviewing && mSurfaceCreated && getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (mCamera != null && mPreviewing && mSurfaceCreated) {
                try {
                    mCamera.autoFocus(autoFocusCB);
                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                }
            }
        }
    };

    Camera.AutoFocusCallback autoFocusCB = (success, camera) -> {
        if (success) {
            postDelayed(doAutoFocus, 1000);
        } else {
            postDelayed(doAutoFocus, 500);
        }
    };

}