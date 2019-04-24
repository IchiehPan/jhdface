package cn.jhd.face.client.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVUtil {

    public static void trace(Mat firstFrame, Mat grey) {
        //计算当前帧和第一帧的不同
		/*Mat frameDelta = new Mat();
		Mat threshold = new Mat();
		Core.absdiff(firstFrame, grey, frameDelta);
		Imgproc.threshold(frameDelta, threshold, 25, 255, Imgproc.THRESH_BINARY);
		Imgproc.dilate(threshold, null, iterations=2);

		//扩展阀值图像填充孔洞，然后找到阀值图像上的轮廓
		# 计算当前帧和第一帧的不同
	    frameDelta = cv2.absdiff(firstFrame, gray)
	    thresh = cv2.threshold(frameDelta, 25, 255, cv2.THRESH_BINARY)[1]

	    # 扩展阀值图像填充孔洞，然后找到阀值图像上的轮廓
	    thresh = cv2.dilate(thresh, None, iterations=2)
	    (cnts, _) = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL,
	        cv2.CHAIN_APPROX_SIMPLE)

	    # 遍历轮廓
	    for c in cnts:
	        # if the contour is too small, ignore it
	        if cv2.contourArea(c) < args["min_area"]:
	            continue

	        # compute the bounding box for the contour, draw it on the frame,
	        # and update the text
	        # 计算轮廓的边界框，在当前帧中画出该框
	        (x, y, w, h) = cv2.boundingRect(c)
	        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
	        text = "Occupied"
	   */
    }

    //图片亮度调节
    public static Mat gamma_trans(Mat img, double gamma) {
        //具体做法是先归一化到1，然后gamma作为指数值求出新的像素值再还原
        Mat dst = new Mat();
        img.convertTo(dst, img.type(), 1, 50);
        return dst;
    }
}
