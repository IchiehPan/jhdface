package cn.jhd.face.client.ui;

import org.xutils.x;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jhd.face.client.AccessTokenManager;
import cn.jhd.face.client.R;
import cn.jhd.face.client.utils.ApiHttp;
import cn.jhd.face.client.utils.UIHelper;
import cn.jhd.face.client.utils.UpdateManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends Activity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    private Context mContext = this;
    @ViewInject(R.id.title)
    private TextView mTopBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mTopBarTitle.setText("设置");
    }

    /**
     * 用注解的方式为按钮添加点击事件，方法声明必须为private
     * type默认View.OnClickListener.class，故此处可以简化不写，@Event(R.id.bt_main)
     */
    @Event(type = View.OnClickListener.class, value = R.id.setting_checkUpdate)
    private void checkUpdateOnClick(View v) {
        Handler handler = new Handler();
        handler.postDelayed(() -> new UpdateManager(SettingActivity.this, true).checkUpdate(), 10);
    }

    @Event(type = View.OnLongClickListener.class, value = R.id.setting_checkUpdate)
    private boolean backSleepOnClick(View v) {
        List<String> files = new ArrayList<>();
        files.add("/sdcard/jhdface/images/temp0.jpg");
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("flag", "0");

        ApiHttp.uploadFaceFile(files, new Callback() {
            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                String response = arg1.body().string();
                Log.i(TAG, "onResponse: response=" + response);
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                Log.e(TAG, "onFailure: ------------------", arg1);
            }
        }, dataMap);
        Toast.makeText(SettingActivity.this, "彩蛋", Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * 用注解的方式为按钮添加点击事件，方法声明必须为private
     * type默认View.OnClickListener.class，故此处可以简化不写，@Event(R.id.bt_main)
     */
    @Event(type = View.OnClickListener.class, value = R.id.setting_addBtn)
    private void addBtnOnClick(View v) {
        if (!AccessTokenManager.isLogin()) {
            UIHelper.jumpToLogin(mContext);
        } else {
            UIHelper.jumpToAddUser(mContext);
        }
    }

    /**
     * 用注解的方式为按钮添加点击事件，方法声明必须为private
     * type默认View.OnClickListener.class，故此处可以简化不写，@Event(R.id.bt_main)
     */
    @Event(type = View.OnClickListener.class, value = R.id.leftBtn)
    private void leftBtnOnClick(View v) {
        if (AccessTokenManager.isLogin()) {
            AccessTokenManager.logout();
        }
        finish();
    }
}
