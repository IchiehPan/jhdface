package cn.jhd.face.client.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkUtil {

    //检测当前的网络状态

    //API版本23以下时调用此方法进行检测
    //因为API23后getNetworkInfo(int networkType)方法被弃用
    public static void checkState_21(Context context) {
        //步骤1：通过Context.getSystemService(Context.CONNECTIVITY_SERVICE)获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //步骤2：获取ConnectivityManager对象对应的NetworkInfo对象
        //NetworkInfo对象包含网络连接的所有信息
        //步骤3：根据需要取出网络连接信息
        //获取WIFI连接的信息
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Log.i("checkState_21", "Wifi是否连接:" + networkInfo.isConnected());

        //获取移动数据连接的信息
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Log.i("checkState_21", "移动数据是否连接:" + networkInfo.isConnected());
    }

    //API版本23及以上时调用此方法进行网络的检测
    //步骤非常类似
    @TargetApi(23)
    public static void checkState_21orNew(Context context) {
        //获得ConnectivityManager对象
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取所有网络连接的信息
        Network[] networks = connMgr.getAllNetworks();
        //用于存放网络连接信息
        StringBuilder sb = new StringBuilder();
        //通过循环将网络信息逐个取出来
        for (int i = 0; i < networks.length; i++) {
            //获取ConnectivityManager对象对应的NetworkInfo对象
            NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
            sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
        }
        Log.i("checkState_21", "连接信息:" + sb);
    }

    public static String netConnect(String urlAddress) {
        return netConnect(urlAddress, "GET", 5000, 5000);
    }

    public static String netConnect(String urlAddress, String requestMethod, int connectTimeout, int readTimeout) {
        Log.i("netConnect", "param: urlAddress=" + urlAddress + ", requestMethod=" + requestMethod + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout);
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlAddress);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod(requestMethod);
            //设置连接超时时间（毫秒）
            connection.setConnectTimeout(connectTimeout);
            //设置读取超时时间（毫秒）
            connection.setReadTimeout(readTimeout);

            //返回输入流
            InputStream in = connection.getInputStream();

            //读取输入流
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (MalformedURLException e) {
            Log.e("netConnect", e.getMessage());
        } catch (ProtocolException e) {
            Log.e("netConnect", e.getMessage());
        } catch (IOException e) {
            Log.e("netConnect", e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("netConnect", e.getMessage());
                }
            }
            if (connection != null) {//关闭连接
                connection.disconnect();
            }
        }

        return result.toString();
    }
}
