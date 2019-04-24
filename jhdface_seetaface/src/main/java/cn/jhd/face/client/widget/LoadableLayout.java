package cn.jhd.face.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jhd.face.client.R;
import cn.jhd.face.client.utils.CommonUtil;

public class LoadableLayout extends RelativeLayout implements android.view.View.OnClickListener {
    private static final String TAG = LoadableLayout.class.getSimpleName();

    public static final int HIDE_LAYOUT = 0;
    public static final int NETWORK_ERROR = 1;
    public static final int NETWORK_LOADING = 2;
    public static final int NODATA = 3;
    public static final int NODATA_ENABLE_CLICK = 4;
    public static final int NO_LOGIN = 5;

    private final Context context;
    private boolean clickEnable = true;
    private String strNoDataContent = "";
    private int mErrorState;

    private ImageView imgView;
    private TextView textView;
    private OnClickListener listener;
    private RelativeLayout mLayout;
    private ProgressBar animProgress;

    public LoadableLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public LoadableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.view_loadable_layout, null);
        imgView = view.findViewById(R.id.error_img);
        textView = view.findViewById(R.id.error_text);
        mLayout = view.findViewById(R.id.pageErrLayout);
        animProgress = view.findViewById(R.id.animProgress);

        setOnClickListener(this);
        mLayout.setOnClickListener(v -> {
            if (clickEnable) {
                if (listener != null)
                    listener.onClick(v);
            }
        });

        addView(view);

    }

    public void dismiss() {
        mErrorState = HIDE_LAYOUT;
        setVisibility(View.GONE);
    }

    public int getErrorState() {
        return mErrorState;
    }

    @Override
    public void onClick(View v) {
        if (clickEnable) {
            if (listener != null)
                listener.onClick(v);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onSkinChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onSkinChanged() {
    }

    public void setDayNight(boolean flag) {
    }

    public void setErrorMessage(String msg) {
        if (!msg.equals(""))
            textView.setText(msg);
        else
            textView.setText(R.string.loadable_view_network_error);
    }

    public void setErrorMessage(int resourceId) {
        if (resourceId != 0)
            textView.setText(resourceId);
        else
            textView.setText(R.string.loadable_view_network_error);
    }

    public void setErrorImage(int imgResource) {
        try {
            imgView.setImageResource(imgResource);
        } catch (Exception e) {
            Log.e(TAG, "setErrorImage: ", e);
        }
    }

    public void setType(int i) {
        setVisibility(View.VISIBLE);
        switch (i) {
            case NETWORK_ERROR:
                mErrorState = NETWORK_ERROR;
                if (CommonUtil.hasInternet(context)) {
                    textView.setText(R.string.loadable_view_load_error_click_to_refresh);
                    imgView.setBackgroundResource(R.drawable.pagefailed_bg);
                } else {
                    textView.setText(R.string.loadable_view_network_error_click_to_refresh);
                    imgView.setBackgroundResource(R.drawable.page_icon_network);
                }
                imgView.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                clickEnable = true;
                break;
            case NETWORK_LOADING:
                mErrorState = NETWORK_LOADING;
                animProgress.setVisibility(View.VISIBLE);
                imgView.setVisibility(View.GONE);
                textView.setText(R.string.loadable_view_loading);
                clickEnable = false;
                break;
            case NODATA:
                mErrorState = NODATA;
                imgView.setBackgroundResource(R.drawable.page_icon_empty);
                imgView.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                setErrorMessage(R.string.loadable_view_no_data);
                clickEnable = false;
                break;
            case HIDE_LAYOUT:
                setVisibility(View.GONE);
                break;
            case NODATA_ENABLE_CLICK:
                mErrorState = NODATA_ENABLE_CLICK;
                imgView.setBackgroundResource(R.drawable.page_icon_empty);
                imgView.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                setErrorMessage(R.string.loadable_view_load_error_click_to_refresh);
                clickEnable = true;
                break;
            default:
                break;
        }
    }


    public void setOnLayoutClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            mErrorState = HIDE_LAYOUT;
        super.setVisibility(visibility);
    }
}
