package cn.jhd.face.client.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

@SuppressLint("NewApi")
public class CommonUtil {
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }

    public static float getScreenHeight(Context ctx) {
        return getDisplayMetrics(ctx).heightPixels;
    }

    public static float getScreenWidth(Context ctx) {
        return getDisplayMetrics(ctx).widthPixels;
    }

    public static boolean hasInternet(Context ctx) {
        boolean flag;
        if (((ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public static int getBright(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int r, g, b;
        int count = 0;
        int bright = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count++;
                int localTemp = bm.getPixel(i, j);
                r = (localTemp | 0xff00ffff) >> 16 & 0x00ff;
                g = (localTemp | 0xffff00ff) >> 8 & 0x0000ff;
                b = (localTemp | 0xffffff00) & 0x0000ff;
                bright = (int) (bright + 0.299 * r + 0.587 * g + 0.114 * b);
            }
        }
        return bright / count;
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static Bitmap adjustPhotoRotation(Bitmap inputBitmap, int orientationDegree) {
        if (inputBitmap == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) inputBitmap.getWidth() / 2, (float) inputBitmap.getHeight() / 2);
        float outputX, outputY;
        if (orientationDegree == 90) {
            outputX = inputBitmap.getHeight();
            outputY = 0;
        } else {
            outputX = inputBitmap.getHeight();
            outputY = inputBitmap.getWidth();
        }

        final float[] values = new float[9];
        matrix.getValues(values);
        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];
        matrix.postTranslate(outputX - x1, outputY - y1);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getHeight(), inputBitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(outputBitmap);
        canvas.drawBitmap(inputBitmap, matrix, paint);
        return outputBitmap;
    }

    public static Bitmap makeTintBitmap(Bitmap inputBitmap, int tintColor) {
        if (inputBitmap == null) {
            return null;
        }

        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), inputBitmap.getConfig());
        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inputBitmap, 0, 0, paint);
        return outputBitmap;
    }

}
