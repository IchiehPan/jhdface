package cn.jhd.face.client.utils;

import com.bumptech.glide.Glide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import cn.jhd.face.client.R;

@SuppressLint("NewApi")
public class GlideUtil {

    private static final String TAG = GlideUtil.class.getSimpleName();

    /**
     * 借助内部类 实现线程安全的单例模式
     * 属于懒汉式单例，因为Java机制规定，内部类SingletonHolder只有在getInstance()
     * 方法第一次调用的时候才会被加载（实现了lazy），而且其加载过程是线程安全的。
     * 内部类加载的时候实例化一次instance。
     */
    public GlideUtil() {
    }

    private static class GlideLoadUtilsHolder {
        private final static GlideUtil INSTANCE = new GlideUtil();
    }

    public static GlideUtil getInstance() {
        return GlideLoadUtilsHolder.INSTANCE;
    }

    /**
     * Glide 加载 简单判空封装 防止异步加载数据时调用Glide 抛出异常
     *
     * @param context
     * @param url       加载图片的url地址  String
     * @param imageView 加载图片的ImageView 控件
     */
    public void glideLoad(Context context, String url, ImageView imageView) {
        if (context != null) {
            Glide.with(context).load(url).error(R.drawable.placeholder_h).dontAnimate().into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public void glideLoad(Activity activity, String url, ImageView imageView) {
        if (!activity.isDestroyed()) {
            Glide.with(activity).load(url).error(R.drawable.placeholder_h).dontAnimate().into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,activity is Destroyed");
        }
    }

    public void glideLoad(Fragment fragment, String url, ImageView imageView) {
        if (fragment != null && fragment.getActivity() != null) {
            Glide.with(fragment).load(url).error(R.drawable.placeholder_h).dontAnimate().into(imageView);

        } else {
            Log.i(TAG, "Picture loading failed,fragment is null");
        }
    }

    public void glideLoad(android.app.Fragment fragment, String url, ImageView imageView) {
        if (fragment != null && fragment.getActivity() != null) {
            Glide.with(fragment).load(url).error(R.drawable.placeholder_h).dontAnimate().into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,android.app.Fragment is null");
        }
    }
}
