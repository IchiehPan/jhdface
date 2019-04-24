package cn.jhd.face.client.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import cn.jhd.face.client.constant.Constants;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    //
    static {
        init();
    }

    static String init() {
        File file = new File(Constants.DEFAULT_SAVE_IMAGE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        File dFile = new File(Constants.DEFAULT_DOWNLOAD_PATH);
        if (!dFile.exists()) {
            dFile.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 保存Bitmap
     */
    public static String saveBitmap(Bitmap b) throws IOException {
        long dataTake = System.currentTimeMillis();
        String jpegName = dataTake + ".jpg";
        return saveBitmap(b, jpegName);
    }

    /**
     * 保存Bitmap
     */
    public static String saveBitmap(Bitmap b, String name) throws IOException {
        String path = init();
        String jpegName = path + "/" + name;
        Log.e(TAG, "saveBitmap:jpegName = " + jpegName);
        FileOutputStream fileOutputStream = new FileOutputStream(jpegName);
        BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
        b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return jpegName;
    }

    /**
     * 复制asset文件到指定目录
     *
     * @param oldPath asset下的路径
     * @param newPath SD卡下保存路径
     */
    public static void copyAssets(Context context, String oldPath, String newPath) throws Exception {
        String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
        if (fileNames.length > 0) {// 如果是目录
            File file = new File(newPath);
            file.mkdirs();// 如果文件夹不存在，则递归
            for (String fileName : fileNames) {
                copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
            }
        } else {// 如果是文件
            InputStream is = context.getAssets().open(oldPath);
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        }
    }

    public static boolean fileIsExists(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        return true;
    }

    public static String saveMyBitmap(byte[] data, int width, int high) throws Exception {
        String path = init();
        long dataTake = System.currentTimeMillis();
        String filePath = path + "/YuvImage_" + dataTake + ".jpg";
        File f = new File(filePath);
        f.createNewFile();
        FileOutputStream fOut = new FileOutputStream(f);
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, high, null);
        yuvImage.compressToJpeg(new Rect(0, 0, width, high), 100, fOut);
        fOut.flush();
        fOut.close();
        return filePath;
    }
}
