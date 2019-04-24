# 人脸识别检测端开发文档
###开发人员：杜斌, 潘宜杰
--------
#####开发工具：window64 + ecplise + android SDK + OpenCV3.4.0 SDK + android-ndk-r14b/ Android Studio
#####版本：v1.0
#####功能：
######实现APP端调用摄像头，通过OpenCV库进行人脸检测，检测到人脸后以http协议上传图片到人脸识别服务器进行对比识别，从而实现人脸识别功能
#####应用场景：
######1. 门禁
######2. 安全监控
--------
- 环境搭建：

		1. 用eclipse导入Opencv库
		2. 导入Okhttp3包 okhttp-3.9.1.jar和okio-1.13.0.jar
			OkHttp是一个精巧的网络请求库，有如下特性:
			1)支持http2，对一台机器的所有请求共享同一个socket
			2)内置连接池，支持连接复用，减少延迟
			3)支持透明的gzip压缩响应体
			4)通过缓存避免重复的请求
			5)请求失败时自动重试主机的其他ip，自动重定向
			6)好用的API
		3. 导入rxjava2包(已经废弃)
		4. 创建jhdface的android项目并以libs的形式引入Opencv库，将OpenCV里sample下面face-detection项目中jni文件夹复制到项目中
			【可能遇到的问题】：
				项目的Properties里面没有 [C/C++ build] 和 [C/C++ General] 选项
			【解决】:
				项目右键-->Android Tools-->Add Native support...-->输入合适的名称-->确定.这里我就直接使用的是"OpenCV"。

		5. 抛弃OpenCV Manager。安装一个额外的apk对用户来说非常不友好
			【解决】:
				Android.mk文件中加入：
				  OPENCV_CAMERA_MODULES:=on
				  OPENCV_INSTALL_MODULES:=on

- 开发过程：

		1. jni调用包名修改
			将jni文件夹中的DetectionBasedTracker_jni.h和DetectionBasedTracker_jni.cpp文件中的org_opencv_samples_facedetect改成应用包名，这里我是cn_jhd_face_client_widget
		2. 保存图片蓝色问题
		    保存图片之前用Imgproc.cvtColor(drawable, mBgr, Imgproc.COLOR_RGBA2BGR, 3)转换
		    // yijie 12/28 这个画面蓝色与设备有关
		3. 保存图片的图片太大
		    Imgproc.resize(Mat src,Mat desc,Size size)缩小

