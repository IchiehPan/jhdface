package cn.jhd.face.client.constant;

import android.os.Environment;

import java.io.File;

public class Constants {
    public static final int RC_ALL_PERMISSION = 30001;
    public static String macAddress = "02:00:00:00:00:00";

    public final static String DEFAULT_SAVE_PATH = Environment.getExternalStorageDirectory() + File.separator + "jhdface" + File.separator;

    public final static String DEFAULT_FACEBIN_PATH = DEFAULT_SAVE_PATH + "faceBin" + File.separator;
    //默认图片保存路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = DEFAULT_SAVE_PATH + "images" + File.separator;

    public final static String DEFAULT_DOWNLOAD_PATH = DEFAULT_SAVE_PATH + "download" + File.separator;

    public static final String FACE_MODEL_FILE = "seeta_fd_frontal_v1.0.bin"; //facebin文件名

    public static final int SHOW_RESULT_PANEL = 1;
    public static final int DISMISS_RESULT_PANEL = 2;
    public static final int DISMISS_LOADABLE_PANEL = 3;

    public final static int CLOSE_PANNEL_TIME = 3 * 1000; //面板延迟关闭时间
}
