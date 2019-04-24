package cn.jhd.face.client.utils;

import org.xutils.common.Callback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import cn.jhd.face.client.R;
import cn.jhd.face.client.bean.VersionBean;
import cn.jhd.face.client.widget.TipDialog;

public class UpdateManager {
    private static final String TAG = UpdateManager.class.getSimpleName();
    private Context mContext;
    private boolean isShow = false;

    private TipDialog _waitDialog;

    private VersionBean mVersionBean;

    private UpdateCallBackListener mUpdateCallBackListener;

    private Callback.CommonCallback<String> callback = new Callback.CommonCallback<String>() {

        @Override
        public void onCancelled(CancelledException arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onError(Throwable arg0, boolean arg1) {
            hideCheckDialog();
            if (isShow) {
                showFailDialog();
            }
            if (mUpdateCallBackListener != null) {
                mUpdateCallBackListener.onNetworkErr();
            }
        }

        @Override
        public void onFinished() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSuccess(String response) {
            hideCheckDialog();
            mVersionBean = VersionBean.parseJSON(response);
            onFinishCheck();
        }
    };

    public UpdateManager(Context context, boolean isShow) {
        this.mContext = context;
        this.isShow = isShow;
        _waitDialog = new TipDialog((Activity) mContext);
    }

    public void setUpdateCallBackListener(UpdateCallBackListener l) {
        mUpdateCallBackListener = l;
    }

    public void checkUpdate() {
        if (isShow) {
            showCheckDialog();
        }
        ApiHttp.checkUpdate(callback);
    }

    public boolean haveNew() {
        if (this.mVersionBean == null) {
            return false;
        }
        boolean haveNew = false;
        long curVersionCode = TDevice.getVersionCode(mContext);
        Log.i(TAG, "haveNew: curVersionCode=" + curVersionCode + ", getVersionCode()=" + mVersionBean.getVersionCode());
        if (curVersionCode < mVersionBean.getVersionCode()) {
            haveNew = true;
        }
        return haveNew;
    }

    private void onFinishCheck() {
        if (haveNew()) {
            showUpdateInfo();
        } else {
            if (isShow) {
                showLatestDialog();
            }
            if (mUpdateCallBackListener != null) {
                mUpdateCallBackListener.onNoUpdate();
            }
        }
    }

    public TipDialog showWait(String msg) {
        _waitDialog.setPicURL(R.drawable.tipdialog_loading);
        _waitDialog.getTxt().setText(msg);
        _waitDialog.show();
        return _waitDialog;
    }

    private void showCheckDialog() {
        if (_waitDialog == null) {
            _waitDialog = showWait("正在获取新版本信息...");
        }
        _waitDialog.show();
    }


    private void hideCheckDialog() {
        if (_waitDialog != null) {
            _waitDialog.hide();
        }
    }

    private void showUpdateInfo() {
        if (mVersionBean == null) {
            return;
        }
        new AlertDialog.Builder(mContext).setTitle("发现新版本").setMessage(mVersionBean.getUpdateMsg())
                .setPositiveButton("确定", (dialog, which) -> {
                    UIHelper.openDownLoadService(mContext, mVersionBean.getDownloadUrl(), mVersionBean.getVersionName());
                    if (mUpdateCallBackListener != null) {
                        mUpdateCallBackListener.onDownloadClick();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    if (mUpdateCallBackListener != null) {
                        mUpdateCallBackListener.onCanncelClick();
                    }
                }).show();

    }

    private void showLatestDialog() {
        Toast.makeText(mContext, "已经是新版本了", Toast.LENGTH_SHORT).show();
    }

    private void showFailDialog() {
        Toast.makeText(mContext, "网络异常，无法获取新版本信息", Toast.LENGTH_SHORT).show();
    }

    public interface UpdateCallBackListener {
        public void onNetworkErr();

        public void onDownloadClick();

        public void onCanncelClick();

        public void onNoUpdate();
    }
}
