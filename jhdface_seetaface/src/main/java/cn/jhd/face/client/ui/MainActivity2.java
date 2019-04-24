//package cn.jhd.face.client.ui;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//
//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
//import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//import org.xutils.x;
//
//import com.bumptech.glide.util.Util;
//import com.iflytek.cloud.ErrorCode;
//import com.iflytek.cloud.InitListener;
//import com.iflytek.cloud.SpeechConstant;
//import com.iflytek.cloud.SpeechError;
//import com.iflytek.cloud.SpeechSynthesizer;
//import com.iflytek.cloud.SynthesizerListener;
//import com.iflytek.cloud.util.ResourceUtil;
//import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.media.FaceDetector;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import cn.jhd.face.client.R;
//import cn.jhd.face.client.bean.Face;
//import cn.jhd.face.client.bean.UserBean;
//import cn.jhd.face.client.utils.BeepManager;
//import cn.jhd.face.client.utils.FileUtil;
//import cn.jhd.face.client.utils.GlideUtil;
//import cn.jhd.face.client.utils.UIHelper;
//import cn.jhd.face.client.widget.DetectionBasedTracker;
//import cn.jhd.face.client.widget.LoadableLayout;
//import cn.jhd.face.client.widget.TipDialog;
//
//
//public class MainActivity2 extends Activity implements CvCameraViewListener2 {
//    private static final String TAG = MainActivity2.class.getSimpleName();
//
//    private static int MAX_ERROR_COUNT = 5; //���ô����������
//    private static int CLOSE_PANNEL_TIME = 3; //��ʾ������ʱ��
//    private static final int SHOW_RESULT_PANEL = 1;
//    private static final int DISMISS_RESULT_PANEL = 2;
//    private static final int DISMISS_LOADABLE_PANEL = 3;
//
//    private Context mContext = this;
//    private static final Scalar FACE_RECT_COLOR = new Scalar(255, 255, 255, 255);
//    public static final int JAVA_DETECTOR = 0;
//    public static final int NATIVE_DETECTOR = 1;
//
//    private Mat mRgba;
//    private Mat mGray;
//    //һ�����������ڱ�����Ƭ��
//    private Mat mBgr;
//    private Mat mSmallBgr;
//    private Mat mLargeBgr;
//    private File mCascadeFile;
//    private CascadeClassifier mJavaDetector;
//    private DetectionBasedTracker mNativeDetector;
//
//    private int mDetectorType = NATIVE_DETECTOR;
//    //private int                    mDetectorType       = JAVA_DETECTOR;
//    private String[] mDetectorName;
//
//    private float mRelativeFaceSize = 0.1f;
//    private int mAbsoluteFaceSize = 0;
//
//    private CameraBridgeViewBase mOpenCvCameraView;
//    private Size mSubSize;
//
//    private boolean isSaveAndRequest = false;
//
//
//    // �����ϳɶ���
//    private SpeechSynthesizer mTts;
//    // Ĭ�ϱ��ط�����
//    public static String voicerLocal = "xiaoyan";
//    //�������
//    private int mPercentForBuffering = 0;
//    //���Ž���
//    private int mPercentForPlaying = 0;
//    // ��������
//    private String mEngineType = SpeechConstant.TYPE_LOCAL;
//
//    private LoadableLayout mLoadableLayout;
//    private RelativeLayout mResultLayout;
//    private LinearLayout mResultOneLayout;
//    private ImageView coverView;
//    private TextView valueNameView;
//    private TextView valueGenderView;
//    private TextView valueUnitView;
//    private TextView valueDepartmentView;
//    private TextView valuePositionView;
//    private LinearLayout mResultTwoLayout;
//    private ImageView coverViewOne;
//    private TextView valueNameViewOne;
//    private TextView valueGenderViewOne;
//    private TextView valueUnitViewOne;
//    private TextView valueDepartmentViewOne;
//    private TextView valuePositionViewOne;
//    private ImageView coverViewTwo;
//    private TextView valueNameViewTwo;
//    private TextView valueGenderViewTwo;
//    private TextView valueUnitViewTwo;
//    private TextView valueDepartmentViewTwo;
//    private TextView valuePositionViewTwo;
//    private LinearLayout mResultErrorLayout;
//    private ImageView mSettingBtn;
//
//    private BeepManager beepManager;
//    private TipDialog mTipDialog;
//
//    private int errorCount = 0;
//    private int recognitionUserCount = 0; //ʶ���û���
//    private String file;
//    private String largeFile;
//    private List<UserBean> users;
//    private Rect[] facesArray;
//    private Rect smallRect;
//    private Face mFace;
//    private Bitmap mCacheBitmap;
//    FaceDetector detector;
//
//    private Handler mHandle = new Handler() {
//        @Override
//        public void dispatchMessage(Message msg) {
//            // TODO Auto-generated method stub
//            super.dispatchMessage(msg);
//            //isSaveAndRequest = false;
//            switch (msg.what) {
//
//                case SHOW_RESULT_PANEL:
//                    if (recognitionUserCount > 0) {
//                        beepAndVibrate();
//                    }
//                    showResultPanel(recognitionUserCount);
//                    break;
//                case DISMISS_RESULT_PANEL:
//                    hideResultPanel();
//                    break;
//                case DISMISS_LOADABLE_PANEL:
//                    mLoadableLayout.dismiss();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS: {
//                    // Load native library after(!) OpenCV initialization
//                    System.loadLibrary("detection_based_tracker");
//
//                    try {
//                        // load cascade file from application resources
//                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
//                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//                        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
//                        FileOutputStream os = new FileOutputStream(mCascadeFile);
//
//                        byte[] buffer = new byte[4096];
//                        int bytesRead;
//                        while ((bytesRead = is.read(buffer)) != -1) {
//                            os.write(buffer, 0, bytesRead);
//                        }
//                        is.close();
//                        os.close();
//
//                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
//                        if (mJavaDetector.empty()) {
//                            mJavaDetector = null;
//                        } else
//                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
//
//                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
//
//                        cascadeDir.delete();
//
//                    } catch (IOException e) {
//                        Log.e(TAG, "onManagerConnected: ", e);
//                    }
//
//                    mOpenCvCameraView.enableView();
//                }
//                break;
//                default: {
//                    super.onManagerConnected(status);
//                }
//                break;
//            }
//        }
//    };
//
//    public MainActivity2() {
//        mDetectorName = new String[2];
//        mDetectorName[JAVA_DETECTOR] = "Java";
//        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
//
//        Log.i(TAG, "Instantiated new " + this.getClass());
//    }
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.i(TAG, "called onCreate");
//        super.onCreate(savedInstanceState);
//        x.view().inject(this);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        setContentView(R.layout.face_detect_surface_view);
//
//        mSettingBtn = (ImageView) findViewById(R.id.settingBtn);
//        mSettingBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                UIHelper.jumpToSetting(mContext);
//            }
//        });
//
//        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
//        //���ó�ǰ������ͷ
//        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
//        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
//        mOpenCvCameraView.setCvCameraViewListener(this);
//
//        // ��ʼ���ϳɶ���
//        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
//        //String path = ResourceUtil.TTS_RES_PATH+"="+ ResourceLoader.getResPath(this,mSharedPreferences,"tts")+","+ResourceUtil.ENGINE_START+"=tts";
//        //Boolean  ret = SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START,path);
//        //Log.e("����������������", ""+ ret);
//        mTipDialog = new TipDialog(this);
//        //������Ļ����
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        mLoadableLayout = (LoadableLayout) findViewById(R.id.loadableLayout);
//        mResultLayout = (RelativeLayout) findViewById(R.id.resultLayout);
//        mResultOneLayout = (LinearLayout) findViewById(R.id.result_one);
//        coverView = (ImageView) findViewById(R.id.cover);
//        valueNameView = (TextView) findViewById(R.id.valueName);
//        valueGenderView = (TextView) findViewById(R.id.valueGender);
//        valueUnitView = (TextView) findViewById(R.id.valueUnit);
//        valueDepartmentView = (TextView) findViewById(R.id.valueDepartment);
//        valuePositionView = (TextView) findViewById(R.id.valuePosition);
//        mResultTwoLayout = (LinearLayout) findViewById(R.id.result_two);
//        coverViewOne = (ImageView) findViewById(R.id.coverOne);
//        valueNameViewOne = (TextView) findViewById(R.id.valueNameOne);
//        valueGenderViewOne = (TextView) findViewById(R.id.valueGenderOne);
//        valueUnitViewOne = (TextView) findViewById(R.id.valueUnitOne);
//        valueDepartmentViewOne = (TextView) findViewById(R.id.valueDepartmentOne);
//        valuePositionViewOne = (TextView) findViewById(R.id.valuePositionOne);
//        coverViewTwo = (ImageView) findViewById(R.id.coverTwo);
//        valueNameViewTwo = (TextView) findViewById(R.id.valueNameTwo);
//        valueGenderViewTwo = (TextView) findViewById(R.id.valueGenderTwo);
//        valueUnitViewTwo = (TextView) findViewById(R.id.valueUnitTwo);
//        valueDepartmentViewTwo = (TextView) findViewById(R.id.valueDepartmentTwo);
//        valuePositionViewTwo = (TextView) findViewById(R.id.valuePositionTwo);
//        mResultErrorLayout = (LinearLayout) findViewById(R.id.result_error);
//
//
//        file = FileUtil.DEFAULT_SAVE_IMAGE_PATH + "temp.jpg";
//        largeFile = FileUtil.DEFAULT_SAVE_IMAGE_PATH + "temp_large.jpg";
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!OpenCVLoader.initDebug()) {
//            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
//        } else {
//            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//        }
//    }
//
//    public void onDestroy() {
//        //Glide.with(mContext).pauseRequests();
//        super.onDestroy();
//        mHandle.removeMessages(DISMISS_LOADABLE_PANEL);
//        mHandle.removeMessages(DISMISS_RESULT_PANEL);
//        mHandle.removeMessages(SHOW_RESULT_PANEL);
//        mOpenCvCameraView.disableView();
//    }
//
//    public void onCameraViewStarted(int width, int height) {
//        mGray = new Mat();
//        mRgba = new Mat();
//        mBgr = new Mat();
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            mSubSize = new Size(height / 2, width / 2);
//        } else {
//            mSubSize = new Size(width / 2, height / 2);
//        }
//        mSmallBgr = new Mat();
//        mLargeBgr = new Mat();
//        mFace = new Face();
//    }
//
//    public void onCameraViewStopped() {
//        mGray.release();
//        mRgba.release();
//        mBgr.release();
//        mSmallBgr.release();
//        mLargeBgr.release();
//    }
//
//    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
//        mRgba = inputFrame.rgba();
//        mGray = inputFrame.gray();
//
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Core.rotate(mRgba, mRgba, Core.ROTATE_90_COUNTERCLOCKWISE);
//            Core.rotate(mGray, mGray, Core.ROTATE_90_COUNTERCLOCKWISE);
//        }
//        Core.flip(mRgba, mRgba, 1);
//        Core.flip(mGray, mGray, 1);
//
//        if (mAbsoluteFaceSize == 0) {
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0) {
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
//        }
//
//
//        MatOfRect faces = new MatOfRect();
//
//        if (mDetectorType == JAVA_DETECTOR) {
//            if (mJavaDetector != null)
//                mJavaDetector.detectMultiScale(mGray, faces, 1.3, 7, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//        } else if (mDetectorType == NATIVE_DETECTOR) {
//            if (mNativeDetector != null)
//                mNativeDetector.detect(mGray, faces);
//        }
//
//        facesArray = faces.toArray();
//        /*if(facesArray == null || facesArray.length == 0) {
//            try {
//        		mCacheBitmap = Bitmap.createBitmap(mGray.width(), mGray.height(), Config.RGB_565);
//        		detector = new FaceDetector(mGray.width(), mGray.height(), 1);
//                Utils.matToBitmap(mGray, mCacheBitmap);
//            } catch(Exception e) {
//                Log.e(TAG, "Mat type: " + mRgba);
//                Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
//            }
//            if(mCacheBitmap != null) {
//            	FaceDetector.Face[] mFaces = new FaceDetector.Face[1];
//            	recognitionUserCount = detector.findFaces(mCacheBitmap, mFaces);
//            	/*if(recognitionUserCount > 0) {
//            		facesArray = new Rect[recognitionUserCount];
//            		for (int i = 0; i < recognitionUserCount; i++) {
//            			PointF midpoint = new PointF();
//						mFaces[i].getMidPoint(midpoint);
//						// 获得两眼之间的距离
//		                float eyesDistance = mFaces[i].eyesDistance();
//		                Rect rect = new Rect();
//		                rect.x = (int)(midpoint.x - eyesDistance);
//		                rect.width = (int)(2 * eyesDistance);
//		                rect.y = (int)(midpoint.y - 1.25* eyesDistance);
//		                rect.height = (int)(3 * eyesDistance);
//		                facesArray[i] = rect;
//		                Log.e("=========",rect.x + "|" + rect.width + "|" + rect.y + "|" +rect.height);
//            		}
//            	}
//            }
//        }*/
//
//        /*if(!isSaveAndRequest) {
//			if( facesArray != null && facesArray.length > 0){
//	    		isSaveAndRequest = true;
//	    		smallRect = new Rect();
//	    		Mat drawable = mRgba;
//	    		for (int i = 0; i < facesArray.length; i++) {
//	                if(i == 0) {
//	                	smallRect = facesArray[i];
//	                } else {
//	                	if(smallRect.x + smallRect.width < facesArray[i].x + facesArray[i].width) {
//	                		smallRect.width = facesArray[i].x + facesArray[i].width - smallRect.x;
//	                	}
//	                	if(smallRect.y + smallRect.height < facesArray[i].y + facesArray[i].height) {
//	                		smallRect.height = facesArray[i].y + facesArray[i].height - smallRect.y;
//	                	}
//	                	if(smallRect.x > facesArray[i].x) {
//	                		smallRect.x = facesArray[i].x;
//	                	}
//	                	if(smallRect.y > facesArray[i].y) {
//	                		smallRect.y = facesArray[i].y;
//	                	}
//	                }
//	        	}
//
//	        	mSmallBgr = new Mat();
//	       		Size size = new Size(smallRect.width/2, smallRect.height/2);
//	       		Imgproc.resize(drawable.submat(smallRect).clone(),mSmallBgr,size);
//	       		Imgcodecs.imwrite(file,mSmallBgr);
//	       		Imgproc.resize(drawable,mLargeBgr,mSubSize);
//	       		Imgcodecs.imwrite(largeFile,mLargeBgr);
//
//		        Observable.create(new ObservableOnSubscribe<Mat>() {
//		        	@Override
//		        	public void subscribe(ObservableEmitter<Mat> emitter) throws Exception {
//		           		emitter.onNext(mSmallBgr);
//		        		emitter.onComplete();
//	        		}
//
//		        }).subscribeOn( Schedulers.newThread() )
//		        .subscribe(new Consumer<Mat>() {
//		            @Override
//		            public void accept(Mat drawable) throws Exception {
//		            	drawable = MatUtil.adjustBrightness(drawable);
//		        		if( !MatUtil.isBlur(drawable) ) {
//		               		recognitionUserCount = 0;
//		               		isSaveAndRequest = false;
//		               		ApiHttp.uploadFaceFile(file, new Callback() {
//								@Override
//								public void onResponse(Call arg0, Response arg1) throws IOException {
//									// TODO Auto-generated method stub
//									isSaveAndRequest = false;
//									String response  = arg1.body().string();
//									ResponseBean mResponseResult = new ResponseBean();
//		   							if(!TextUtils.isEmpty(response)){
//		   								users = JSONUtil.parseList(response, mResponseResult, UserBean.class);
//		   								if (mResponseResult.getCode() == ResponseBean.SUCCESS) {
//		   									ApiHttp.getPictureLarge(largeFile, mResponseResult.getFile_name(), new Callback() {
//
//												@Override
//												public void onResponse(Call arg0, Response resp) throws IOException {
//													// TODO Auto-generated method stub
//												}
//
//												@Override
//												public void onFailure(Call arg0, IOException ioe) {
//													// TODO Auto-generated method stub
//												}
//											});
//		   									if (users != null) {
//		   										errorCount = 0;
//		   										recognitionUserCount = users.size();
//		   									} else {
//		   										errorCount++;
//		   									}
//		   									Message message = new Message();
//											message.what = SHOW_RESULT_PANEL;
//											mHandle.sendMessage(message);
//
//		   								} else {
//		   									errorCount++;
//		   									Message message = new Message();
//											message.what = SHOW_RESULT_PANEL;
//											mHandle.sendMessage(message);
//		   								}
//		   							}
//								}
//
//								@Override
//								public void onFailure(Call arg0, IOException arg1) {
//									// TODO Auto-generated method stub
//									isSaveAndRequest = false;
//									Message message = new Message();
//									message.what = SHOW_RESULT_PANEL;
//									mHandle.sendMessage(message);
//								}
//							});
//		        		} else {
//		        			isSaveAndRequest = false;
//		        		}
//		            }
//		        });
//			}
//    	}*/
//
//        //每个设备不一样，有些可能要先转成rgb
//        //Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_BGR2RGB, 3);
//
//        for (int i = 0; i < facesArray.length; i++) {
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 1);
//        }
//        return mRgba;
//    }
//
//    /**
//     * ��ʼ��������
//     */
//    private InitListener mTtsInitListener = new InitListener() {
//        @Override
//        public void onInit(int code) {
//            Log.e(TAG, "InitListener init() code = " + code);
//            if (code != ErrorCode.SUCCESS) {
//            } else {
//                // ��ʼ���ɹ���֮����Ե���startSpeaking����
//                // ע���еĿ�������onCreate�����д�����ϳɶ���֮�����Ͼ͵���startSpeaking���кϳɣ�
//                // ��ȷ�������ǽ�onCreate�е�startSpeaking������������
//            }
//        }
//    };
//
//    private void speech(String text) {
//        if (mTts.isSpeaking()) {
//            mTts.stopSpeaking();
//        }
//        setParam();
//        int code = mTts.startSpeaking(text, mTtsListener);
//        if (code != ErrorCode.SUCCESS) {
//            Toast.makeText(mContext, "�����ϳ�ʧ��,������: " + code, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * �ϳɻص�������
//     */
//    private SynthesizerListener mTtsListener = new SynthesizerListener() {
//
//        @Override
//        public void onSpeakBegin() {
//        }
//
//        @Override
//        public void onSpeakPaused() {
//        }
//
//        @Override
//        public void onSpeakResumed() {
//        }
//
//        @Override
//        public void onBufferProgress(int percent, int beginPos, int endPos,
//                                     String info) {
//            // �ϳɽ���
//            mPercentForBuffering = percent;
//        }
//
//        @Override
//        public void onSpeakProgress(int percent, int beginPos, int endPos) {
//            // ���Ž���
//            mPercentForPlaying = percent;
//        }
//
//        @Override
//        public void onCompleted(SpeechError error) {
//            if (error == null) {
//            } else if (error != null) {
//            }
//        }
//
//        @Override
//        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
//            // ���´������ڻ�ȡ���ƶ˵ĻỰid����ҵ�����ʱ���Ựid�ṩ������֧����Ա�������ڲ�ѯ�Ự��־����λ����ԭ��
//            // ��ʹ�ñ����������ỰidΪnull
//            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//            //		Log.d(TAG, "session id =" + sid);
//            //	}
//        }
//    };
//
//    /*
//     * ��������
//     * @param param
//     * @return
//     */
//    private void setParam() {
//        // ��ղ���
//        mTts.setParameter(SpeechConstant.PARAMS, null);
//        // ���ݺϳ�����������Ӧ����
//        //����ʹ�ñ�������
//        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//        //���÷�������Դ·��
//        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
//        //���÷�����
//        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);
//        //���úϳ�����
//        mTts.setParameter(SpeechConstant.SPEED, "50");
//        //���úϳ�����
//        mTts.setParameter(SpeechConstant.PITCH, "50");
//        //���úϳ�����
//        mTts.setParameter(SpeechConstant.VOLUME, "50");
//        //���ò�������Ƶ������
//        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
//        // ���ò��źϳ���Ƶ������ֲ��ţ�Ĭ��Ϊtrue
//        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
//
//        // ������Ƶ����·����������Ƶ��ʽ֧��pcm��wav������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
//        // ע��AUDIO_FORMAT���������Ҫ���°汾������Ч
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, FileUtil.DEFAULT_SAVE_PATH + "tts.wav");
//    }
//
//    //��ȡ��������Դ·��
//    private String getResourcePath() {
//        StringBuffer tempBuffer = new StringBuffer();
//        //�ϳ�ͨ����Դ
//        tempBuffer.append(ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, "tts/common.jet"));
//        tempBuffer.append(";");
//        //��������Դ
//        tempBuffer.append(ResourceUtil.generateResourcePath(this, RESOURCE_TYPE.assets, "tts/" + voicerLocal + ".jet"));
//        return tempBuffer.toString();
//    }
//
//
//    private void setMinFaceSize(float faceSize) {
//        mRelativeFaceSize = faceSize;
//        mAbsoluteFaceSize = 0;
//    }
//
//    private void setDetectorType(int type) {
//        if (mDetectorType != type) {
//            mDetectorType = type;
//
//            if (type == NATIVE_DETECTOR) {
//                Log.i(TAG, "Detection Based Tracker enabled");
//                mNativeDetector.start();
//            } else {
//                Log.i(TAG, "Cascade detector enabled");
//                mNativeDetector.stop();
//            }
//        }
//    }
//
//    public void showResultPanel(final int userCount) {
//        mHandle.removeMessages(DISMISS_RESULT_PANEL);
//        if (userCount == 0) {
//            if (errorCount >= MAX_ERROR_COUNT) {
//                mResultOneLayout.setVisibility(View.GONE);
//                mResultTwoLayout.setVisibility(View.GONE);
//                mResultErrorLayout.setVisibility(View.VISIBLE);
//                TextView mErrorText = (TextView) mResultErrorLayout.findViewById(R.id.error_text);
//                mErrorText.setText("sorry,识别失败！");
//                mResultLayout.setVisibility(View.VISIBLE);
//                mHandle.sendEmptyMessageDelayed(DISMISS_RESULT_PANEL, CLOSE_PANNEL_TIME * 1000);
//            } else {
//                mResultOneLayout.setVisibility(View.GONE);
//                mResultTwoLayout.setVisibility(View.GONE);
//                mResultErrorLayout.setVisibility(View.GONE);
//                mResultLayout.setVisibility(View.GONE);
//                mHandle.sendEmptyMessage(DISMISS_RESULT_PANEL);
//            }
//        } else if (userCount == 1) {
//            mResultOneLayout.setVisibility(View.VISIBLE);
//            mResultTwoLayout.setVisibility(View.GONE);
//            mResultErrorLayout.setVisibility(View.GONE);
//            mResultLayout.setVisibility(View.VISIBLE);
//            if (users != null && users.size() > 0) {
//                final UserBean user = users.get(0);
//                if (Util.isOnMainThread()) {
//                    GlideUtil.getInstance().glideLoad(mContext, user.getUrl(), coverView);
//                }
//                valueNameView.setText(user.getRealName());
//                if (user.getGender() == 0) {
//                    valueGenderView.setText(R.string.secret);
//                } else if (user.getGender() == 1) {
//                    valueGenderView.setText(R.string.male);
//                } else if (user.getGender() == 2) {
//                    valueGenderView.setText(R.string.female);
//                }
//                valueUnitView.setText(user.getUnit());
//                valueDepartmentView.setText(user.getDepartment());
//                valuePositionView.setText(user.getPosition());
//            }
//            mHandle.sendEmptyMessageDelayed(DISMISS_RESULT_PANEL, CLOSE_PANNEL_TIME * 1000);
//        } else if (userCount == 2) {
//            mResultOneLayout.setVisibility(View.GONE);
//            mResultTwoLayout.setVisibility(View.VISIBLE);
//            mResultErrorLayout.setVisibility(View.GONE);
//            mResultLayout.setVisibility(View.VISIBLE);
//            if (users != null && users.size() > 0) {
//                if (Util.isOnMainThread()) {
//                    GlideUtil.getInstance().glideLoad(mContext, users.get(0).getUrl(), coverViewOne);
//                }
//                valueNameViewOne.setText(users.get(0).getRealName());
//                if (users.get(0).getGender() == 0) {
//                    valueGenderViewOne.setText(R.string.secret);
//                } else if (users.get(0).getGender() == 1) {
//                    valueGenderViewOne.setText(R.string.male);
//                } else if (users.get(0).getGender() == 2) {
//                    valueGenderViewOne.setText(R.string.female);
//                }
//                valueUnitViewOne.setText(users.get(0).getUnit());
//                valueDepartmentViewOne.setText(users.get(0).getDepartment());
//                valuePositionViewOne.setText(users.get(0).getPosition());
//                if (Util.isOnMainThread()) {
//                    // TODO Auto-generated method stub
//                    GlideUtil.getInstance().glideLoad(mContext, users.get(1).getUrl(), coverViewTwo);
//                }
//
//                valueNameViewTwo.setText(users.get(1).getRealName());
//                if (users.get(1).getGender() == 0) {
//                    valueGenderViewTwo.setText(R.string.secret);
//                } else if (users.get(1).getGender() == 1) {
//                    valueGenderViewTwo.setText(R.string.male);
//                } else if (users.get(1).getGender() == 2) {
//                    valueGenderViewTwo.setText(R.string.female);
//                }
//                valueUnitViewTwo.setText(users.get(1).getUnit());
//                valueDepartmentViewTwo.setText(users.get(1).getDepartment());
//                valuePositionViewTwo.setText(users.get(1).getPosition());
//            }
//            mHandle.sendEmptyMessageDelayed(DISMISS_RESULT_PANEL, CLOSE_PANNEL_TIME * 1000);
//        }
//
//    }
//
//    /**
//     * 监听返回键--是否退出程序
//     */
////    @Override
////    public boolean onKeyDown(int keyCode, KeyEvent event) {
////        if (keyCode == KeyEvent.KEYCODE_BACK) {
////        	// 杀死该应用进程
////            android.os.Process.killProcess(android.os.Process.myPid());
////            System.exit(0);
////        }
////        return super.onKeyDown(keyCode, event);
////    }
//    public void hideResultPanel() {
//        mResultOneLayout.setVisibility(View.GONE);
//        mResultTwoLayout.setVisibility(View.GONE);
//        mResultErrorLayout.setVisibility(View.GONE);
//        mResultLayout.setVisibility(View.GONE);
//    }
//
//    public void showHttpErrorPanel() {
//        mHandle.removeMessages(DISMISS_LOADABLE_PANEL);
//        mLoadableLayout.setType(LoadableLayout.NETWORK_ERROR);
//        mHandle.sendEmptyMessageDelayed(DISMISS_LOADABLE_PANEL, CLOSE_PANNEL_TIME * 1000);
//    }
//}
