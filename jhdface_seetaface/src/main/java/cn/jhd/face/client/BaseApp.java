package cn.jhd.face.client;

import org.xutils.x;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import cn.jhd.face.client.constant.Constants;
import cn.jhd.face.client.utils.InfoUtil;

public class BaseApp extends Application {
    private static final String TAG = BaseApp.class.getSimpleName();
    private static BaseApp instance;
    static Context _context;
    static Resources _resource;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        _context = getApplicationContext();
        _resource = _context.getResources();

        x.Ext.init(this);
        x.Ext.setDebug(false);

        try {
            Constants.macAddress = InfoUtil.getLocalMacAddressFromIp();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }


        // 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid

        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
//        MSCUtil.initMSC(_context);
//        MSCUtil.speech(_context, "");
    }

    public static BaseApp getInstance() {
        return instance;
    }

    public static synchronized BaseApp context() {
        return (BaseApp) _context;
    }

    public static Resources resources() {
        return _resource;
    }

}
