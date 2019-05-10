package cn.jhd.face.client;

import org.xutils.x;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.umeng.commonsdk.UMConfigure;

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

        UMConfigure.setLogEnabled(true);
        /*
        注意：如果您已经在AndroidManifest.xml中配置过appkey和channel值，可以调用此版本初始化函数。
        */
//        参数1:上下文，必须的参数，不能为空。
//        参数2:设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机。
//        参数3:Push推送业务的secret，需要集成Push功能时必须传入Push的secret，否则传空。
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);

        String deviceInfo[] = UMConfigure.getTestDeviceInfo(this);

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
