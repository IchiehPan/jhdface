package cn.jhd.face.client.utils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class MatUtil {

    /**
     * 照片模糊度判断
     *
     * @param image
     * @return
     */
    public static boolean isBlur(Mat image) {
        Mat dst = new Mat();
        Imgproc.medianBlur(image, dst, 5);
        // 返回模糊度
        Imgproc.Laplacian(dst, dst, dst.depth());
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stdDev = new MatOfDouble();
        Core.meanStdDev(dst, mean, stdDev);
        double[] stdDevArr = stdDev.toArray();
        double variance = stdDevArr[0] * stdDevArr[0];
        if (variance > 6) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 调整亮度
     *
     * @param InputImg
     * @return
     */
    public static Mat adjustBrightness(Mat InputImg) {
        float[] brightness = brightnessException(InputImg);
        if (brightness[0] > 1) {
            if (brightness[1] > 0) { //过亮
                Core.convertScaleAbs(InputImg, InputImg, 2, -20);
            } else if (brightness[1] < 0) { //过暗
                Core.convertScaleAbs(InputImg, InputImg, 2, 20);
            }
        }
        return InputImg;
    }

    /*********************************************************************************************************************************************************
     *函数描述：  brightnessException     计算并返回一幅图像的色偏度以及，色偏方向
     *函数参数：  InputImg    需要计算的图片，BGR存放格式，彩色（3通道），灰度图无效
     *           cast        计算出的偏差值，小于1表示比较正常，大于1表示存在亮度异常；当cast异常时，da大于0表示过亮，da小于0表示过暗
     *函数返回值： 返回值通过cast、da两个引用返回，无显式返回值
     **********************************************************************************************************************************************************/
    public static float[] brightnessException(Mat InputImg) {
        float[] ret = new float[2];
        Mat GRAYimg = new Mat();
        Imgproc.cvtColor(InputImg, GRAYimg, Imgproc.COLOR_BGR2GRAY);
        float a = 0;
        int[] Hist = new int[256];
        for (int i = 0; i < 256; i++)
            Hist[i] = 0;
        for (int i = 0; i < GRAYimg.rows(); i++) {
            for (int j = 0; j < GRAYimg.cols(); j++) {
                a += (float) (GRAYimg.get(i, j)[0] - 128);//在计算过程中，考虑128为亮度均值点
                int x = (int) (GRAYimg.get(i, j)[0]);
                Hist[x]++;
            }
        }
        float da = a / (GRAYimg.rows() * InputImg.cols());
        float D = Math.abs(da);
        float Ma = 0;
        for (int i = 0; i < 256; i++) {
            Ma += Math.abs(i - 128 - da) * Hist[i];
        }
        Ma = Ma / (GRAYimg.rows() * GRAYimg.cols());
        float M = Math.abs(Ma);
        float K = D / M;
        float cast = K;
        ret[0] = cast;
        ret[1] = da;
        return ret;
    }

}
