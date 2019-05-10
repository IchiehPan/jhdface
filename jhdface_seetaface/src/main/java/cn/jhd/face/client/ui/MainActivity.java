package cn.jhd.face.client.ui;

import java.text.SimpleDateFormat;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.xutils.x;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.umeng.analytics.MobclickAgent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jhd.face.client.BaseApp;
import cn.jhd.face.client.R;
import cn.jhd.face.client.bean.UserBean;
import cn.jhd.face.client.constant.Constants;
import cn.jhd.face.client.listener.CvCameraViewListener2;
import cn.jhd.face.client.utils.BeepManager;
import cn.jhd.face.client.utils.FileUtil;
import cn.jhd.face.client.utils.GlideUtil;
import cn.jhd.face.client.utils.UIHelper;
import cn.jhd.face.client.widget.LoadableLayout;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Context mContext = this;
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    private String[] mDetectorName;

    private CameraBridgeViewBase cameraBridgeViewBase;

    private LoadableLayout mLoadableLayout;
    private LinearLayout mResultLayout;
    private TextView mTimeView;
    private LinearLayout mResultOneLayout;
    private ImageView coverView;
    private TextView valueNameView;
    private TextView valueGenderView;
    private TextView valueUnitView;
    private TextView valueDepartmentView;
    private TextView valuePositionView;
    private ImageView mSettingBtn;

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Handler mHandle = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            List<UserBean> userBeans = (List<UserBean>) msg.obj;
            switch (msg.what) {
                case Constants.SHOW_RESULT_PANEL:
//                    BeepManager.playBeepSoundAndVibrate(getApplicationContext());
                    showResultPanel(userBeans);
                    break;
                case Constants.DISMISS_RESULT_PANEL:
                    hideResultPanel();
                    break;
                case Constants.DISMISS_LOADABLE_PANEL:
                    mLoadableLayout.dismiss();
                    break;
                default:
                    break;
            }
        }
    };


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(BaseApp.context()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    try {
                        if (!FileUtil.fileIsExists(Constants.DEFAULT_FACEBIN_PATH + Constants.FACE_MODEL_FILE)) {
                            FileUtil.copyAssets(mContext, "seetaface", Constants.DEFAULT_FACEBIN_PATH);
                        }
                        if (!FileUtil.fileIsExists(Constants.DEFAULT_FACEBIN_PATH + Constants.FACE_MODEL_FILE)) {
                            return;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onManagerConnected: ", e);
                    }
                    cameraBridgeViewBase.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    public MainActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        methodRequiresAllPermission();
        x.view().inject(this);

        //android保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.face_detect_surface_view);

        mSettingBtn = findViewById(R.id.settingBtn);
        mSettingBtn.setOnClickListener(v -> UIHelper.jumpToSetting(mContext));

        cameraBridgeViewBase = findViewById(R.id.fd_activity_surface_view);
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        //设置摄像头为前置
//        cameraBridgeViewBase.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
//        cameraBridgeViewBase.setVisibility(CameraBridgeViewBase.VISIBLE);

        CvCameraViewListener2 cvCameraViewListener2 = new CvCameraViewListener2(mHandle, getWindowManager().getDefaultDisplay().getRotation());
        cameraBridgeViewBase.setCvCameraViewListener(cvCameraViewListener2);

        //设置相机帧的最大大小
//        Point point = InfoUtil.getScreenMetrics(this);
//        cameraBridgeViewBase.setMaxFrameSize(point.x, point.y);

//        cameraBridgeViewBase.getMaxFrameSize();
//        cameraBridgeViewBase.getFrameSize();

        mLoadableLayout = findViewById(R.id.loadableLayout);
        mResultLayout = findViewById(R.id.resultLayout);
        mTimeView = findViewById(R.id.timeView);
        mResultOneLayout = findViewById(R.id.result_one);
        coverView = findViewById(R.id.cover);
        valueNameView = findViewById(R.id.valueName);
        valueGenderView = findViewById(R.id.valueGender);
        valueUnitView = findViewById(R.id.valueUnit);
        valueDepartmentView = findViewById(R.id.valueDepartment);
        valuePositionView = findViewById(R.id.valuePosition);

        BeepManager.init(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onDestroy() {
        //Glide.with(mContext).pauseRequests();
        super.onDestroy();
//        if (beepManager != null) {
//            beepManager.close();
//        }
        mHandle.removeMessages(Constants.DISMISS_LOADABLE_PANEL);
        mHandle.removeMessages(Constants.DISMISS_RESULT_PANEL);
        mHandle.removeMessages(Constants.SHOW_RESULT_PANEL);
        cameraBridgeViewBase.disableView();
        if (Util.isOnMainThread()) {
            Glide.with(getApplicationContext()).pauseRequests();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i(TAG, "onConfigurationChanged: orientation=" + newConfig.orientation);
        recreate();

//        CvCameraViewListener2 cvCameraViewListener2 = new CvCameraViewListener2(mHandle, newConfig.orientation);
//        cameraBridgeViewBase.setCvCameraViewListener(cvCameraViewListener2);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(getApplicationContext(), "横屏", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getApplicationContext(), "竖屏", Toast.LENGTH_SHORT).show();
//        }
    }

    public void showResultPanel(List<UserBean> userBeans) {
        int userCount = userBeans.size();
        if (userCount == 1) {
            mHandle.removeMessages(Constants.DISMISS_RESULT_PANEL);
            mResultOneLayout.setVisibility(View.VISIBLE);
            mTimeView.setText(df.format(System.currentTimeMillis()));
            mTimeView.setVisibility(View.VISIBLE);
            mResultLayout.setVisibility(View.VISIBLE);
            if (userBeans != null && userBeans.size() > 0) {
                UserBean userBean = userBeans.get(0);
                if (Util.isOnMainThread()) {
                    GlideUtil.getInstance().glideLoad(this, userBean.getUrl(), coverView);
                }
                valueNameView.setText(userBean.getRealname());
                if (userBean.getGender() == UserBean.SECRET) {
                    valueGenderView.setText(R.string.secret);
                } else if (userBean.getGender() == UserBean.MALE) {
                    valueGenderView.setText(R.string.male);
                } else if (userBean.getGender() == UserBean.FEMALE) {
                    valueGenderView.setText(R.string.female);
                }
                if (!TextUtils.isEmpty(userBean.getUnit())) {
                    valueUnitView.setText(userBean.getUnit());
                } else {
                    valueUnitView.setText(userBean.getCompany());
                }

                valueDepartmentView.setText(userBean.getDepartment());
                valuePositionView.setText(userBean.getPosition());
            }
            mHandle.sendEmptyMessageDelayed(Constants.DISMISS_RESULT_PANEL, Constants.CLOSE_PANNEL_TIME);
        }

    }

    public void hideResultPanel() {
        Log.i(TAG, "hideResultPanel: ");
        mResultOneLayout.setVisibility(View.GONE);
        mTimeView.setVisibility(View.GONE);
        mResultLayout.setVisibility(View.GONE);
    }

    /**
     * 鐩戝惉杩斿洖閿�--鏄惁閫�鍑虹▼搴�
     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        	// 鏉�姝昏搴旂敤杩涚▼
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void methodRequiresAllPermission() {
        String[] perms = {Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            Log.d(TAG, "methodRequiresTwoPermission: 已经拥有权限");
        } else {
            // Do not have permissions, request them now
            Log.d(TAG, "methodRequiresTwoPermission: 开始申请权限");
            EasyPermissions.requestPermissions(this, getString(R.string.request_permission_tip), Constants.RC_ALL_PERMISSION, perms);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Some permissions have been denied
        // ...
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been granted
        // ...
        Log.d(TAG, "onPermissionsGranted: 申请权限通过");
    }

}
