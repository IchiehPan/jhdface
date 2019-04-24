package cn.jhd.face.client.widget.view;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

@SuppressLint("NewApi")
public class ProcessDataTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = ProcessDataTask.class.getSimpleName();
    private Camera mCamera;
    private byte[] mData;
    private Delegate mDelegate;
    private int orientation;

    public ProcessDataTask(Camera camera, byte[] data, Delegate delegate, int orientation) {
        mCamera = camera;
        mData = data;
        mDelegate = delegate;
        this.orientation = orientation;
    }

    public ProcessDataTask perform() {
        if (Build.VERSION.SDK_INT >= 11) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            execute();
        }
        return this;
    }

    public void cancelTask() {
        if (getStatus() != Status.FINISHED) {
            cancel(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mDelegate = null;
    }

    @Override
    protected String doInBackground(Void... params) {
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        int width = size.width;
        int height = size.height;

        try {
            if (mDelegate == null) {
                return null;
            }
            return mDelegate.processData(mData, width, height, false);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            try {
                return mDelegate.processData(mData, width, height, true);
            } catch (Exception e2) {
                Log.e(TAG, "doInBackground: ", e2);
                return null;
            }
        }
    }

    public interface Delegate {
        String processData(byte[] data, int width, int height, boolean isRetry);
    }
}
