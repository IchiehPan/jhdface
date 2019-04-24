package cn.jhd.face.client.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class InfoUtil {
    private static final String TAG = InfoUtil.class.getSimpleName();

    /**
     * 安卓7版本会返回"02:00:00:00:00:00"
     */
    public static String getMacAddress(Context mContext) {
        String macAddress = null;
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());
        if (!wifiManager.isWifiEnabled()) {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }

    /**
     * 根据IP地址获取MAC地址
     */
    public static String getLocalMacAddressFromIp() throws Exception {
        String strMacAddr = null;
        //获得IpD地址
        InetAddress ip = getLocalInetAddress();
        byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            if (i != 0) {
                buffer.append(':');
            }
            String str = Integer.toHexString(b[i] & 0xFF);
            buffer.append(str.length() == 1 ? 0 + str : str);
        }
        strMacAddr = buffer.toString().toUpperCase();

        return strMacAddr;
    }

    /**
     * 获取移动设备本地IP
     */
    public static InetAddress getLocalInetAddress() throws SocketException {
        InetAddress ip = null;
        //列举
        Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
        while (en_netInterface.hasMoreElements()) {//是否还有元素
            NetworkInterface ni = en_netInterface.nextElement();//得到下一个元素
            Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
            while (en_ip.hasMoreElements()) {
                ip = en_ip.nextElement();
                if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                    break;
                else
                    ip = null;
            }

            if (ip != null) {
                break;
            }
        }
        return ip;
    }


    /**
     * 获取最佳预览大小
     *
     * @param parameters       相机参数
     * @param screenResolution 屏幕宽高
     */
    public static Point getBestCameraResolution(Camera.Parameters parameters, Point screenResolution) {
        float tmp = 0f;
        float minDiff = 100f;
        float x_d_y = (float) screenResolution.x / (float) screenResolution.y;
        Camera.Size best = null;
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size s : supportedPreviewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - x_d_y);
            if (tmp < minDiff) {
                minDiff = tmp;
                best = s;
            }
        }
        return new Point(best.width, best.height);
    }

    /**
     * 获取屏幕宽度和高度，单位为px
     */
    public static Point getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.i(TAG, "getScreenMetrics: widthPixels=" + dm.widthPixels);
        Log.i(TAG, "getScreenMetrics: heightPixels=" + dm.heightPixels);
        return new Point(w_screen, h_screen);
    }

    public static final int getOrientation(Context context) {
        Point screenResolution = getScreenResolution(context);
        return screenResolution.x > screenResolution.y ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public static Point getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenResolution = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenResolution);
        } else {
            screenResolution.set(display.getWidth(), display.getHeight());
        }
        return screenResolution;
    }
}
