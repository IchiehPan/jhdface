package cn.jhd.face.client.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

import cn.jhd.face.client.R;
import cn.jhd.face.client.constant.Constants;

// 科大讯飞语音 - 主类
public class MSCUtil {
    private static final String TAG = MSCUtil.class.getSimpleName();
    // 语音合成对象
    private static SpeechSynthesizer mTts;
    // 默认云端发音人
    public static String voicerCloud = "xiaoyan";
    // 默认本地发音人
    public static String voicerLocal = "xiaoyan";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    String mEngineMode = SpeechConstant.MODE_MSC;

    // 语音缓存
    // 缓冲进度
    static int mPercentForBuffering = 0;
    // 播放进度
    static int mPercentForPlaying = 0;

    public static void initMSC(Context context) { // 初始化科大讯飞语音类
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        StringBuffer param = new StringBuffer();
        param.append(SpeechConstant.APPID + "=" + context.getString(R.string.msc_app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(context, param.toString());

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
    }

    /*
     * 锟斤拷锟斤拷锟斤拷锟斤拷
     * @param param
     * @return
     */
    public void setMSCParam(Context context) {

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mTts.setParameter(SpeechConstant.ENGINE_MODE, mEngineMode);

        if (SpeechConstant.TYPE_LOCAL.equals(mEngineType)
                && SpeechConstant.MODE_MSC.equals(mEngineMode)) {
            // 需下载使用对应的离线合成SDK
            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath(context));
        }

        mTts.setParameter(SpeechConstant.VOICE_NAME, voicerLocal);

        // 锟斤拷詹锟斤拷锟�
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 锟斤拷锟捷合筹拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷应锟斤拷锟斤拷
        //锟斤拷锟矫合筹拷锟斤拷锟斤拷
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //锟斤拷锟矫合筹拷锟斤拷锟斤拷
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //锟斤拷锟矫合筹拷锟斤拷锟斤拷
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //锟斤拷锟矫诧拷锟斤拷锟斤拷锟斤拷频锟斤拷锟斤拷锟斤拷
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 锟斤拷锟矫诧拷锟脚合筹拷锟斤拷频锟斤拷锟斤拷锟斤拷植锟斤拷牛锟侥拷锟轿猼rue
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 锟斤拷锟斤拷锟斤拷频锟斤拷锟斤拷路锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷频锟斤拷式支锟斤拷pcm锟斤拷wav锟斤拷锟斤拷锟斤拷路锟斤拷为sd锟斤拷锟斤拷注锟斤拷WRITE_EXTERNAL_STORAGE权锟斤拷
        // 注锟斤拷AUDIO_FORMAT锟斤拷锟斤拷锟斤拷锟斤拷锟揭拷锟斤拷掳姹撅拷锟斤拷锟斤拷锟叫�
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Constants.DEFAULT_SAVE_PATH + "tts.wav");
    }

    //锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷源路锟斤拷
    public String getResourcePath(Context context) {
        StringBuffer tempBuffer = new StringBuffer();
        //锟较筹拷通锟斤拷锟斤拷源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //锟斤拷锟斤拷锟斤拷锟斤拷源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voicerLocal + ".jet"));
        return tempBuffer.toString();
    }

    public static void speech(Context context, String text) {
        if (null == mTts) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            Log.i(TAG, "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }

        //        String path = ResourceUtil.TTS_RES_PATH + "=" + ResourceLoader.getResPath(this, mSharedPreferences, "tts") + "," + ResourceUtil.ENGINE_START + "=tts";
//        Boolean ret = SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START, path);

        final String strTextToSpeech = "科大讯飞，让世界聆听我们的声音";
        mTts.startSpeaking(strTextToSpeech, mSynListener);

        if (mTts.isSpeaking()) {
            mTts.stopSpeaking();
        }
        int code = mTts.startSpeaking(text, mSynListener);
        if (code != ErrorCode.SUCCESS) {
            Toast.makeText(context, "语音读取错误: " + code, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化监听。
     */
    private static InitListener mTtsInitListener = code -> {
        Log.d(TAG, "InitListener init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            Log.e(TAG, "onInit: " + "初始化失败,错误码：" + code);
        } else {
            // 初始化成功，之后可以调用startSpeaking方法
            // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
            // 正确的做法是将onCreate中的startSpeaking调用移至这里
        }
    };

    /**
     * 合成回调监听。
     */
    static SynthesizerListener mSynListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            // 合成进度
            mPercentForBuffering = percent;
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
            } else if (error != null) {
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}

            //实时音频流输出参考
			/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
				byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
				Log.e("MscSpeechLog", "buf is =" + buf);
			}*/
        }
    };
}
