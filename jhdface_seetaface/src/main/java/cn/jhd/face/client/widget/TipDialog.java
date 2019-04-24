package cn.jhd.face.client.widget;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jhd.face.client.R;

public class TipDialog {
    private static final String TAG = TipDialog.class.getSimpleName();

    private Activity context;
    private Dialog dialog;
    private LinearLayout mBox;
    private ProgressBar mPic;
    private TextView mTxt;
    private double hideTime = 1.8;
    private boolean autoHide = true;
    private boolean showTxt = true;
    private boolean showPic = true;
    private double density;

    public TipDialog(Activity context) {
        this.context = context;
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_tip_dialog, null);

        mBox = view.findViewById(R.id.tipdialog_box);

        mPic = view.findViewById(R.id.tipdialog_img);
        mTxt = view.findViewById(R.id.tipdialog_txt);
        dialog = new Dialog(context, R.style.TipDialogStyle);
        dialog.setContentView(view);

    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public void setBackground(int color) {
        mBox.setBackgroundColor(color);
    }

    public void setBackground(Drawable background) {
        mBox.setBackgroundDrawable(background);
    }

    public void setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;
    }

    public double getHideTime() {
        return hideTime;
    }

    public void setHideTime(int hideTime) {
        this.hideTime = hideTime;
    }

    public ProgressBar getPic() {
        return mPic;
    }

    public void setPic(ProgressBar pic) {
        mPic = pic;
        setShowPic(true);
    }

    public TextView getTxt() {
        return mTxt;
    }

    public void setTxt(TextView txt) {
        mTxt = txt;
        setShowTxt(true);
    }

    public void setTxt(String txt) {
        getTxt().setText(txt);
        setShowTxt(true);
    }

    public boolean isShowTxt() {
        return showTxt;
    }

    public void setShowTxt(boolean showTxt) {
        this.showTxt = showTxt;
    }

    public boolean isShowPic() {
        return showPic;
    }

    public void setPicURL(int source) {
        Drawable d = context.getResources().getDrawable(source);
        d.setBounds(0, 0, (int) (52 * density), (int) (52 * density));
        getPic().setIndeterminateDrawable(d);

    }

    public void setTxtSource(int source) {
        getTxt().setText(source);
    }

    public void setShowPic(boolean showPic) {
        this.showPic = showPic;
    }

    private void setLayout() {
        if (showPic) {
            mPic.setVisibility(View.VISIBLE);
        }

        if (showTxt) {
            mTxt.setVisibility(View.VISIBLE);
        }

    }

    public void hide() {
        try {
            dialog.cancel();
        } catch (Exception e) {
            Log.e(TAG, "hide: ", e);
        }
    }

    public void show(int picUrl, int resourceId, boolean autoHide) {
        setPicURL(picUrl);
        setTxtSource(resourceId);
        setAutoHide(autoHide);
        show();
    }

    public void show(int picUrl, String txt, boolean autoHide) {
        setPicURL(picUrl);
        setTxt(txt);
        setAutoHide(autoHide);
        show();
    }

    public void show() {
        setLayout();
        try {
            dialog.show();
            if (isAutoHide()) {
                final Handler handler = new Handler();
                Runnable runnable = () -> hide();
                handler.postDelayed(runnable, (long) (hideTime * 1000));
            }

        } catch (Exception e) {
            Log.e(TAG, "show: ", e);
        }

    }

}
