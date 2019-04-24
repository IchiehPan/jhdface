package cn.jhd.face.client.widget.view;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import cn.jhd.face.client.utils.CommonUtil;
import cn.jhd.face.client.utils.InfoUtil;

@SuppressLint("NewApi")
final class CameraConfigurationManager {
    private static final int TEN_DESIRED_ZOOM = 27;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private final Context mContext;
    private Point mScreenResolution;
    private Point mCameraResolution;
    private Point mPreviewResolution;
    private int orientionOfCamera; //前置摄像头layout角度

    public CameraConfigurationManager(Context context) {
        mContext = context;
    }

    @SuppressWarnings("deprecation")
    public void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        if (CameraConfigurationManager.autoFocusAble(camera)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        //parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);

        mScreenResolution = InfoUtil.getScreenResolution(mContext);
        Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = mScreenResolution.x;
        screenResolutionForCamera.y = mScreenResolution.y;

        // preview size is always something like 480*320, other 320*480
        int orientation = InfoUtil.getOrientation(mContext);

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            screenResolutionForCamera.x = mScreenResolution.y;
            screenResolutionForCamera.y = mScreenResolution.x;
        }

        mPreviewResolution = getPreviewResolution(parameters, screenResolutionForCamera);

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mCameraResolution = new Point(mPreviewResolution.y, mPreviewResolution.x);
        } else {
            mCameraResolution = mPreviewResolution;
        }
    }

    public static boolean autoFocusAble(Camera camera) {
        List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
        String focusMode = findSettableValue(supportedFocusModes, Camera.Parameters.FOCUS_MODE_AUTO);
        return focusMode != null;
    }

    public Point getCameraResolution() {
        return mCameraResolution;
    }

    public void setDesiredCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        List<int[]> frameRates = parameters.getSupportedPreviewFpsRange();
        int l_last = frameRates.size() - 1;
        int minFps = (frameRates.get(l_last))[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
        int maxFps = (frameRates.get(l_last))[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
        parameters.setPreviewFpsRange(minFps, maxFps);
        parameters.setPreviewSize(mPreviewResolution.x, mPreviewResolution.y);
        parameters.setPictureSize(mPreviewResolution.x, mPreviewResolution.y);
        setZoom(parameters);

        camera.setDisplayOrientation(getDisplayOrientation());
        camera.setParameters(parameters);
    }

    public void openFlashlight(Camera camera) {
        doSetTorch(camera, true);
    }

    public void closeFlashlight(Camera camera) {
        doSetTorch(camera, false);
    }

    private void doSetTorch(Camera camera, boolean newSetting) {
        Camera.Parameters parameters = camera.getParameters();
        String flashMode;
        /** 是否支持闪光灯 */
        if (newSetting) {
            flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_TORCH, Camera.Parameters.FLASH_MODE_ON);
        } else {
            flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_OFF);
        }
        if (flashMode != null) {
            parameters.setFlashMode(flashMode);
        }
        camera.setParameters(parameters);
    }

    private static String findSettableValue(Collection<String> supportedValues, String... desiredValues) {
        String result = null;
        if (supportedValues != null) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    result = desiredValue;
                    break;
                }
            }
        }
        return result;
    }

    public int getOrientionOfCamera() {
        return orientionOfCamera;
    }

    public Point getPreviewResolution() {
        return mPreviewResolution;
    }

    public int getDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        orientionOfCamera = info.orientation;

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private static Point getPreviewResolution(Camera.Parameters parameters, Point screenResolution) {
        /*Point previewResolution =
                findBestPreviewSizeValue(parameters.getSupportedPreviewSizes(), screenResolution);
        if (previewResolution == null) {
            previewResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
        }
        return previewResolution;
        */
        return findBestPreviewSizeValue(parameters.getSupportedPreviewSizes(), screenResolution);
    }

    private static Point findBestPreviewSizeValue(List<Camera.Size> supportSizeList, Point screenResolution) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) screenResolution.x / screenResolution.y;
        if (supportSizeList == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = screenResolution.y;

        // 试着找到一个尺寸匹配的纵横比和尺寸
        for (Camera.Size size : supportSizeList) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // 找不到一个匹配的纵横比，进行处理
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : supportSizeList) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return new Point(optimalSize.width, optimalSize.height);
    }

    private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom
                    - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }


    private void setZoom(Camera.Parameters parameters) {
        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (NumberFormatException nfe) {
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

}