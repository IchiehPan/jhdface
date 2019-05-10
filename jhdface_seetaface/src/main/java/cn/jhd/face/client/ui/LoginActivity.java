package cn.jhd.face.client.ui;

import org.json.JSONException;
import org.xutils.common.Callback;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.jhd.face.client.AccessTokenManager;
import cn.jhd.face.client.R;
import cn.jhd.face.client.bean.LoginBean;
import cn.jhd.face.client.bean.ResponseBean;
import cn.jhd.face.client.utils.ApiHttp;
import cn.jhd.face.client.utils.CommonUtil;
import cn.jhd.face.client.utils.JSONUtil;
import cn.jhd.face.client.widget.TipDialog;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int SUCCESS = 0;
    private static final int ERROR = 1;

    private TextView mTopbarTitle;
    private ImageView mTopbarLeftBtn;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mLoginBtn;
    private TextView mWarnView;
    private TextView mInfoView;

    private TipDialog mTipDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            mInfoView.setVisibility(View.GONE);
            mWarnView.setVisibility(View.GONE);
            mLoginBtn.setEnabled(true);
            if (msg.what == SUCCESS) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTipDialog = new TipDialog(this);

        mTopbarTitle = findViewById(R.id.title);
        mTopbarLeftBtn = findViewById(R.id.leftBtn);

        mTopbarTitle.setText(R.string.login);
        mTopbarLeftBtn.setOnClickListener(v -> finish());

        mEtUsername = findViewById(R.id.username);
        mEtPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.login);
        mLoginBtn.setOnClickListener(v -> login());

        mWarnView = findViewById(R.id.warn_view);
        mInfoView = findViewById(R.id.info_view);
    }

    private void login() {
        if (CommonUtil.hasInternet(this)) {
            String username = mEtUsername.getText().toString().trim();
            if (TextUtils.isEmpty(username)) {
                showMessageView(ERROR, R.string.error_username_empty);
                return;
            }
            String password = mEtPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                showMessageView(ERROR, R.string.error_password_empty);
                return;
            }
            mLoginBtn.setEnabled(false);
            mTipDialog.setShowPic(true);
            mTipDialog.setTxt("提交中，请稍候...");
            mTipDialog.show();
            ApiHttp.login(username, password, new Callback.CommonCallback<String>() {

                @Override
                public void onCancelled(CancelledException arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onError(Throwable arg0, boolean arg1) {
                    Log.e(TAG, "onError: ", arg0);
                    showMessageView(ERROR, R.string.error_request_data);
                }

                @Override
                public void onFinished() {
                    if (mTipDialog != null) {
                        mTipDialog.hide();
                    }
                }

                @Override
                public void onSuccess(String response) {
                    Log.i(TAG, "onSuccess: " + response);
                    mLoginBtn.setEnabled(true);
                    if (TextUtils.isEmpty(response)) {
                        showMessageView(ERROR, R.string.error_request_data);
                        return;
                    }
                    ResponseBean mResult = new ResponseBean();
                    LoginBean loginBean = null;
                    try {
                        loginBean = (LoginBean) JSONUtil.parseObject(response, mResult, LoginBean.class);
                    } catch (JSONException e) {
                        Log.e(TAG, "onSuccess: ", e);
                    }
                    if (mResult.getCode() != ResponseBean.SUCCESS) {
                        showMessageView(ERROR, mResult.getMsg());
                    }
                    if (loginBean == null) {
                        showMessageView(ERROR, R.string.error_login);
                    }
                    String token = loginBean.getToken();
                    if (TextUtils.isEmpty(token)) {
                        showMessageView(ERROR, R.string.error_login);
                    }
                    AccessTokenManager.storeToken(token, loginBean.getExpire_time(), loginBean.getAccount());
                    showMessageView(SUCCESS, R.string.success_login);
                }
            });
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
