package cn.jhd.face.client.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import cn.jhd.face.client.R;
import cn.jhd.face.client.bean.ResponseBean;
import cn.jhd.face.client.bean.UserBean;
import cn.jhd.face.client.utils.ApiHttp;
import cn.jhd.face.client.utils.BeepManager;
import cn.jhd.face.client.utils.CommonUtil;
import cn.jhd.face.client.utils.FileUtil;
import cn.jhd.face.client.utils.InfoUtil;
import cn.jhd.face.client.utils.JSONUtil;
import cn.jhd.face.client.widget.TipDialog;
import cn.jhd.face.client.widget.view.BaseFindFaceView;
import cn.jhd.face.client.widget.view.FindFaceView;

@ContentView(R.layout.activity_adduser)
public class AddUserActivity extends Activity implements BaseFindFaceView.Delegate {
    private static final String TAG = AddUserActivity.class.getSimpleName();

    private Context mContext = this;
    public static final int REQUEST_FOR_ALBUM = 1;
    public static final int REQUEST_FOR_TAKEPICTURE = 2;

    private static final int IMAGE_REQUEST_CODE = 98;
    private static final int RESIZE_REQUEST_CODE = 99;

    private static final int SUCCESS = 0;
    private static final int ERROR = -1;

    private static final String TEMP_AVATAR_NAME = "temp_avatar" + System.currentTimeMillis() + ".jpg";

    @ViewInject(R.id.camera_panel)
    private RelativeLayout mCameraPanel;
    @ViewInject(R.id.preview)
    private FindFaceView mFindFaceView;
    @ViewInject(R.id.rotatingBtn)
    private ImageButton mRotatingBtn;
    @ViewInject(R.id.cameraBtn)
    private ImageButton mCameraBtn;
    @ViewInject(R.id.result_panel)
    private LinearLayout mResultPanel;
    @ViewInject(R.id.result_avatar)
    private ImageView mIvAvatar;
    @ViewInject(R.id.etId)
    private EditText mEtId;
    @ViewInject(R.id.etName)
    private EditText mEtName;
    @ViewInject(R.id.radioGroup)
    private RadioGroup mRadioGroup;
    @ViewInject(R.id.valueUnit)
    private EditText mEtUnit;
    @ViewInject(R.id.valueDepartment)
    private EditText mEtDepartment;
    @ViewInject(R.id.valuePosition)
    private EditText mEtPosition;
    @ViewInject(R.id.submitBtn)
    private Button mSubmitBtn;
    @ViewInject(R.id.warn_view)
    private TextView mWarnView;
    @ViewInject(R.id.info_view)
    private TextView mInfoView;
    @ViewInject(R.id.title)
    private TextView mTopbarTitle;

    private BeepManager beepManager;
    private TipDialog mTipDialog;
    private ToneGenerator tone;
    private Bitmap mBitmap;
    private String avatarFile;

    private int genderValue;
    private int mType = REQUEST_FOR_TAKEPICTURE;

    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;


    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            mInfoView.setVisibility(View.GONE);
            mWarnView.setVisibility(View.GONE);
            mSubmitBtn.setEnabled(true);
            if (msg.what == SUCCESS) {
                mFindFaceView.stopCamera();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mTipDialog = new TipDialog(this);
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mFindFaceView.setDelegate(this);
        initView();
    }


    /**
     * 旋转摄像头
     * type默认View.OnClickListener.class，故此处可以简化不写，@Event(R.id.bt_main)
     */
    @Event(type = View.OnClickListener.class, value = R.id.rotatingBtn)
    private void settingBtnOnClick(View v) {
        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mFindFaceView.stopCamera();
            mFindFaceView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            mFindFaceView.showScanRect();
        } else {
            mFindFaceView.stopCamera();
            mFindFaceView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            mFindFaceView.showScanRect();
        }
    }

    /**
     * 用注解的方式为按钮添加点击事件，方法声明必须为private
     * type默认View.OnClickListener.class，故此处可以简化不写，@Event(R.id.bt_main)
     */
    @Event(type = View.OnClickListener.class, value = R.id.leftBtn)
    private void leftBtnOnClick(View v) {
        finish();
    }

    /**
     * 拍照点击
     * type默认View.OnClickListener.class，故此处可以简化不写，@Event(R.id.bt_main)
     */
    @Event(type = View.OnClickListener.class, value = R.id.cameraBtn)
    private void cameraBtnOnClick(View v) {
        mFindFaceView.takePicture(shutterCallback, null, jpegCallback);
    }

    /**
     * 上传点击
     * type默认View.OnClickListener.class，故此处可以简化不写，@Event(R.id.bt_main)
     */
    @Event(type = View.OnClickListener.class, value = R.id.submitBtn)
    private void submitBtnOnClick(View v) {
        submit();
    }


    public void initView() {
        mTopbarTitle.setText("头像上传");
//    	mRotatingBtn = (ImageButton)findViewById(R.id.rotatingBtn);
//    	mRotatingBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if( currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
//		            mFindFaceView.stopCamera();
//		            mFindFaceView.startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
//		            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
//		            mFindFaceView.showScanRect();
//		        } else{
//		            mFindFaceView.stopCamera();
//		            mFindFaceView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
//		            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//		            mFindFaceView.showScanRect();
//		        }
//			}
//		});
//    	mCameraBtn = (ImageButton)findViewById(R.id.cameraBtn);
//    	mCameraBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mFindFaceView.takePicture(shutterCallback, null, jpegCallback);
//			}
//		});
        // TODO Auto-generated method stub
        /*mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.rb_male:
                        genderValue = 1;
                        break;
                    case R.id.rb_female:
                        genderValue = 2;
                        break;
                    case R.id.rb_secret:
                        genderValue = 0;
                        break;
                }
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        mFindFaceView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mFindFaceView.showScanRect();
        mFindFaceView.startSpot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFindFaceView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mFindFaceView.showScanRect();
        mFindFaceView.startSpot();

        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onStop() {
        mFindFaceView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        mFindFaceView.onDestroy();
        super.onDestroy();
    }


    //返回照片的JPEG格式的数据
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            float mRotateDegree = 0.0f;
            int height = mBitmap.getHeight();
            switch (mFindFaceView.getPreview().getOrientionOfCamera()) {
                case 0:
                    mRotateDegree = 0.0f;
                    height = mBitmap.getHeight();
                    break;
                case 90:
                    if (InfoUtil.getOrientation(mContext) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        mRotateDegree = -90.0f;
                        height = mBitmap.getWidth();
                    } else {
                        mRotateDegree = 0.0f;
                        height = mBitmap.getHeight();
                    }
                    break;
                case 180:
                    mRotateDegree = -180.0f;
                    height = mBitmap.getHeight();
                    break;
                case 270:
                    mRotateDegree = -270.0f;
                    height = mBitmap.getWidth();
                    break;
            }
            Rect rect = mFindFaceView.getScanBoxView().getScanBoxAreaRect(height);
            try {
                Matrix matrix = new Matrix();
                matrix.postRotate(mRotateDegree);
                Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
                newBitmap = Bitmap.createBitmap(newBitmap, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top, null, false);
                avatarFile = FileUtil.saveBitmap(newBitmap, TEMP_AVATAR_NAME);
                newBitmap.recycle();
                mIvAvatar.setImageURI(Uri.parse(avatarFile));
                toggleResultPanel();
            } catch (Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                mFindFaceView.getPreview().stopCameraPreview();
                mFindFaceView.getPreview().showCameraPreview();
            }
        }
    };

    //快门按下的时候onShutter()被回调
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // TODO Auto-generated method stub
            if (tone == null) {
                //发出提示用户的声音
                tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
            }
            tone.startTone(ToneGenerator.TONE_PROP_BEEP2);
        }
    };

    private void toggleResultPanel() {
        if (mResultPanel.getVisibility() == View.VISIBLE) {
            mResultPanel.setVisibility(View.GONE);
        } else {
            mResultPanel.setVisibility(View.VISIBLE);
        }
    }


    private void submit() {
        if (CommonUtil.hasInternet(mContext)) {
//            String name = mEtName.getEditableText().toString().trim();
//            if( StringUtils.isEmpty(name) ){
//                showMessageView(ERROR,R.string.error_name_empty);
//                return;
//            }
//            String unit = mEtUnit.getEditableText().toString().trim();
//            if( StringUtils.isEmpty(unit) ){
//                showMessageView(ERROR,R.string.error_unit_empty);
//                return;
//            }
//            String department = mEtDepartment.getEditableText().toString().trim();
//            if( StringUtils.isEmpty(department) ){
//                showMessageView(ERROR,R.string.error_department_empty);
//                return;
//            }
//            String position = mEtPosition.getEditableText().toString().trim();
//            if( StringUtils.isEmpty(position) ){
//                showMessageView(ERROR,R.string.error_position_empty);
//                return;
//            }

            mSubmitBtn.setEnabled(false);
            mTipDialog.setShowPic(true);
            mTipDialog.setTxt("提交中，请稍候...");
            mTipDialog.show();
            UserBean userBean = new UserBean();
            /*userBean.setUid(mEtId.getEditableText().toString().trim());
            userBean.setName(name);
            userBean.setGender(genderValue);
            userBean.setDepartment(unit);
            userBean.setPosition(position);
            userBean.setDepartment(department);
            */

            try {
                ApiHttp.upload(avatarFile, userBean, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        ResponseBean mResult = new ResponseBean();
                        try {
                            JSONUtil.parseObject(result, mResult, null);
                        } catch (JSONException e) {
                            Log.e(TAG, "onSuccess: ", e);
                        }

                        if (mResult.getCode() == ResponseBean.SUCCESS) {
                            showMessageView(SUCCESS, R.string.success_upload);
                        } else {
                            showMessageView(ERROR, mResult.getMsg());
                        }
                        if (!TextUtils.isEmpty(avatarFile)) {
                            File delFile = new File(avatarFile);
                            if (delFile != null && delFile.exists()) {
                                delFile.delete();
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        showMessageView(ERROR, R.string.error_request_data);
                        //showMessageView(ERROR, ex.getMessage());
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                        if (mTipDialog != null) {
                            mTipDialog.hide();
                        }
                    }
                });
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "submit: ", e);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "submit: ", e);
            }
        } else {
            showMessageView(ERROR, R.string.loadable_view_network_error);
        }
    }

    private void showMessageView(int type, int resId) {
        showMessageView(type, getResources().getString(resId));
    }

    private void showMessageView(int type, String msg) {
        mHandler.removeMessages(type);
        switch (type) {
            case ERROR:
                mWarnView.setText(msg);
                mInfoView.setVisibility(View.GONE);
                mWarnView.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                mInfoView.setText(msg);
                mWarnView.setVisibility(View.GONE);
                mInfoView.setVisibility(View.VISIBLE);
        }
        mHandler.sendEmptyMessageDelayed(type, 3 * 1000);
    }

    @Override
    public void onScanFaceSuccess(String result, long t) {
    }

    @Override
    public void onScanFaceOpenCameraError() {
        Toast.makeText(this, "无法打开摄像头，请检查权限", Toast.LENGTH_SHORT).show();
    }
}
