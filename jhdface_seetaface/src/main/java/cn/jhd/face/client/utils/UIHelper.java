package cn.jhd.face.client.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import cn.jhd.face.client.ICallbackResult;
import cn.jhd.face.client.service.DownloadService;
import cn.jhd.face.client.service.DownloadService.DownloadBinder;
import cn.jhd.face.client.ui.AddUserActivity;
import cn.jhd.face.client.ui.LoginActivity;
import cn.jhd.face.client.ui.SettingActivity;

public class UIHelper {

    private static final String TAG = UIHelper.class.getSimpleName();

    /**
     * 跳转到登录界面
     */
    public static void jumpToLogin(Context ctx) {
        Intent intent = new Intent(ctx, LoginActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * 跳转到登录界面
     */
    public static void jumpToAddUser(Context ctx) {
        Intent intent = new Intent(ctx, AddUserActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * 跳转到设置界面
     */
    public static void jumpToSetting(Context ctx) {
        Intent intent = new Intent(ctx, SettingActivity.class);
        ctx.startActivity(intent);
    }

    public static void openDownLoadService(Context context, String downloadUrl, String title) {
        final ICallbackResult callback = s -> {
        };
        ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadBinder binder = (DownloadBinder) service;
                binder.addCallback(callback);
                binder.start();
            }
        };
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(DownloadService.BUNDLE_KEY_TITLE, title);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 打开系统中的浏览器
     *
     * @param context
     * @param url
     */
    public static void openSysBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            Toast.makeText(context, "无法浏览此网页", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "openSysBrowser: ", e);
        }
    }
}
