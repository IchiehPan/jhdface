package cn.jhd.face.client.widget.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TrackFaceRectView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private int mWidth;
    private int mHeight;
    private Paint mPaint = new Paint();
    private float mCornerSize = 3.0f;
    private float mCornerLength = 30.0f;
    private float mHalfCornerSize;

    private List<Rect> drawRectList;

    private Paint clipPaint;
    private Paint paint;

    private boolean canDraw = true;

    private Canvas canvas;

    public TrackFaceRectView(Context context) {
        this(context, null);
    }

    public TrackFaceRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.TRANSPARENT);
        this.setZOrderOnTop(true);

        drawRectList = new ArrayList<>();

        mHalfCornerSize = mCornerSize / 2;

        clipPaint = new Paint();
        clipPaint.setAntiAlias(true);
        clipPaint.setStyle(Paint.Style.STROKE);
        clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(5.0f);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mCornerSize);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
        canDraw = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        canDraw = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        canDraw = false;
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        // TODO Auto-generated method stub
        super.onWindowVisibilityChanged(visibility);
    }

    public FaceDetectorResult drawRect(FaceDetector.Face[] mFaces, int numberOfFaceDetected, Rect rect, int srcBitmapWidth, int srcBitmapHeight, float scaleX, float scaleY) {
        int usableFaceCount = 0;
        drawRectList.clear();
        FaceDetectorResult mFaceDetectorResult = new FaceDetectorResult();
        Rect minRect = new Rect();
        if (canDraw) {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                for (int i = 0; i < numberOfFaceDetected; i++) {
                    try {
                        PointF midpoint = new PointF();
                        mFaces[i].getMidPoint(midpoint);
                        // 获得两眼之间的距离
                        float eyesDistance = mFaces[i].eyesDistance();

                        Rect faceRect = new Rect();
                        // 因为拍摄的相片跟实际显示的图像是镜像关系，所以在图片上获取的两眼中间点跟手机上显示的是相反方向
                        faceRect.left = (int) (getWidth() - (midpoint.x + eyesDistance) * scaleX);
                        faceRect.top = (int) ((midpoint.y - eyesDistance) * scaleY);
                        faceRect.right = (int) (getWidth() - (midpoint.x - eyesDistance) * scaleX);
                        faceRect.bottom = (int) ((midpoint.y + 1.7 * eyesDistance) * scaleY);


                        if (minRect.left == 0) {
                            minRect.left = (int) (midpoint.x - eyesDistance);
                        } else if (minRect.left > (midpoint.x + eyesDistance)) {
                            minRect.left = (int) (midpoint.x - eyesDistance);
                        }


                        if (minRect.top == 0) {
                            minRect.top = (int) (midpoint.y - eyesDistance);
                        } else if (minRect.top > midpoint.y - eyesDistance) {
                            minRect.top = (int) (midpoint.y - eyesDistance);
                        }


                        if (minRect.right == 0) {
                            minRect.right = (int) (midpoint.x + eyesDistance);
                        } else if (minRect.right < (midpoint.x + eyesDistance)) {
                            minRect.right = (int) (midpoint.x + eyesDistance);
                        }


                        if (minRect.bottom == 0) {
                            minRect.bottom = (int) (midpoint.y + 1.7 * eyesDistance);
                        } else if (minRect.bottom < midpoint.y + 1.7 * eyesDistance) {
                            minRect.bottom = (int) (midpoint.y + 1.7 * eyesDistance);
                        }

                        if (
                                faceRect.left >= rect.left - 10
                                        && faceRect.right <= rect.right + 10
                                        && faceRect.top >= rect.top
                                        && faceRect.bottom <= rect.bottom) {
                            drawRectList.add(faceRect);
                            usableFaceCount++;
                        }
                    } catch (Exception e) {
                    }
                }

                if (usableFaceCount > 0) {
                    mFaceDetectorResult.setUsableFaceCount(usableFaceCount);
                    mFaceDetectorResult.setMinRect(minRect);
                    drawCornerLine();
                }
            }
            holder.unlockCanvasAndPost(canvas);
        }
        return mFaceDetectorResult;
    }

    /**
     * 画四个直角的线
     */
    private void drawCornerLine() {
        for (Rect faceRect : drawRectList) {
            canvas.drawLine(faceRect.left - mHalfCornerSize, faceRect.top, faceRect.left - mHalfCornerSize + mCornerLength, faceRect.top, mPaint);
            canvas.drawLine(faceRect.left, faceRect.top - mHalfCornerSize, faceRect.left, faceRect.top - mHalfCornerSize + mCornerLength, mPaint);

            canvas.drawLine(faceRect.right + mHalfCornerSize, faceRect.top, faceRect.right + mCornerSize - mCornerLength, faceRect.top, mPaint);
            canvas.drawLine(faceRect.right, faceRect.top - mHalfCornerSize, faceRect.right, faceRect.top - mHalfCornerSize + mCornerLength, mPaint);

            canvas.drawLine(faceRect.left - mHalfCornerSize, faceRect.bottom, faceRect.left - mHalfCornerSize + mCornerLength, faceRect.bottom, mPaint);
            canvas.drawLine(faceRect.left, faceRect.bottom + mHalfCornerSize, faceRect.left, faceRect.bottom + mHalfCornerSize - mCornerLength, mPaint);

            canvas.drawLine(faceRect.right + mHalfCornerSize, faceRect.bottom, faceRect.right + mHalfCornerSize - mCornerLength, faceRect.bottom, mPaint);
            canvas.drawLine(faceRect.right, faceRect.bottom + mHalfCornerSize, faceRect.right, faceRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
        }
    }

    public void clean() {
        if (canDraw) {
            Paint paint = new Paint();
            canvas = holder.lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//	    	paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
//	    	canvas.drawPaint(paint);
//	    	paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
