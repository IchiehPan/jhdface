package cn.jhd.face.client.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cn.jhd.face.client.R;
import cn.jhd.face.client.utils.CommonUtil;
import cn.jhd.face.client.utils.InfoUtil;


public class ScanBoxView extends View {
    private Context mContext;
    private int mMoveStepDistance;
    private int mAnimDelayTime;

    private Rect mFramingRect;
    private float mScanLineTop;
    private float mScanLineLeft;
    private Paint mPaint;
    private TextPaint mTipPaint;

    private int mMaskColor;
    private int mCornerColor;
    private int mCornerLength;
    private int mCornerSize;
    private int mRectWidth;
    private int mRectHeight;

    private int mTopOffset;
    private int mScanLineSize;
    private int mScanLineColor;
    private int mScanLineMargin;
    private boolean mIsShowDefaultScanLineDrawable;
    private Drawable mCustomScanLineDrawable;
    private Bitmap mScanLineBitmap;
    private int mBorderSize;
    private int mBorderColor;
    private int mAnimTime;
    private boolean mIsCenterVertical;
    private int mToolbarHeight;
    private boolean mIsBarcode;
    private String mTipText;
    private int mTipTextSize;
    private int mTipTextColor;
    private boolean mIsTipTextBelowRect;
    private int mTipTextMargin;
    private boolean mIsShowTipTextAsSingleLine;
    private int mTipBackgroundColor;
    private boolean mIsShowTipBackground;
    private boolean mIsScanLineReverse;
    private boolean mIsShowDefaultGridScanLineDrawable;
    private Drawable mCustomGridScanLineDrawable;
    private Bitmap mGridScanLineBitmap;
    private float mGridScanLineBottom;
    private float mGridScanLineRight;
    private boolean mIsMoveScanLine;

    private Bitmap mOriginScanLineBitmap;
    private Bitmap mOriginGridScanLineBitmap;


    private float mHalfCornerSize;
    private StaticLayout mTipTextSl;
    private int mTipBackgroundRadius;

    public ScanBoxView(Context context) {
        super(context);
        mContext = context;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMaskColor = Color.parseColor("#33FFFFFF");
        mCornerColor = Color.WHITE;
        mCornerLength = CommonUtil.dp2px(context, 20);
        mCornerSize = CommonUtil.dp2px(context, 3);
        mScanLineSize = CommonUtil.dp2px(context, 1);
        mScanLineColor = Color.WHITE;
        mTopOffset = CommonUtil.dp2px(context, 90);
        mRectHeight = CommonUtil.dp2px(context, 300);
        mScanLineMargin = 0;
        mIsShowDefaultScanLineDrawable = false;
        mCustomScanLineDrawable = null;
        mScanLineBitmap = null;
        mBorderSize = CommonUtil.dp2px(context, 1);
        mBorderColor = Color.WHITE;
        mAnimTime = 1000;
        mIsCenterVertical = false;
        mToolbarHeight = 0;
        mIsBarcode = false;
        mMoveStepDistance = CommonUtil.dp2px(context, 2);
        mTipText = null;
        mTipTextSize = CommonUtil.dp2px(context, 14);
        mTipTextColor = Color.WHITE;
        mIsTipTextBelowRect = false;
        mTipTextMargin = CommonUtil.dp2px(context, 20);
        mIsShowTipTextAsSingleLine = false;
        mTipBackgroundColor = Color.parseColor("#22000000");
        mIsShowTipBackground = false;
        mIsScanLineReverse = false;
        mIsShowDefaultGridScanLineDrawable = false;
        mIsMoveScanLine = true;

        float screenWidth = CommonUtil.getScreenWidth(mContext);
        mRectWidth = (int) (screenWidth - mHalfCornerSize - 0.5f);

        mTipPaint = new TextPaint();
        mTipPaint.setAntiAlias(true);

        mTipBackgroundRadius = CommonUtil.dp2px(context, 4);
    }

    public void initCustomAttrs(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JHDView);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();

        afterInitCustomAttrs();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.JHDView_orc_topOffset) {
            mTopOffset = typedArray.getDimensionPixelSize(attr, mTopOffset);
        } else if (attr == R.styleable.JHDView_orc_cornerSize) {
            mCornerSize = typedArray.getDimensionPixelSize(attr, mCornerSize);
        } else if (attr == R.styleable.JHDView_orc_cornerLength) {
            mCornerLength = typedArray.getDimensionPixelSize(attr, mCornerLength);
        } else if (attr == R.styleable.JHDView_orc_scanLineSize) {
            mScanLineSize = typedArray.getDimensionPixelSize(attr, mScanLineSize);
        } else if (attr == R.styleable.JHDView_orc_rectWidth) {
            mRectWidth = typedArray.getDimensionPixelSize(attr, mRectWidth);
        } else if (attr == R.styleable.JHDView_orc_maskColor) {
            mMaskColor = typedArray.getColor(attr, mMaskColor);
        } else if (attr == R.styleable.JHDView_orc_cornerColor) {
            mCornerColor = typedArray.getColor(attr, mCornerColor);
        } else if (attr == R.styleable.JHDView_orc_scanLineColor) {
            mScanLineColor = typedArray.getColor(attr, mScanLineColor);
        } else if (attr == R.styleable.JHDView_orc_scanLineMargin) {
            mScanLineMargin = typedArray.getDimensionPixelSize(attr, mScanLineMargin);
        } else if (attr == R.styleable.JHDView_orc_isShowDefaultScanLineDrawable) {
            mIsShowDefaultScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultScanLineDrawable);
        } else if (attr == R.styleable.JHDView_orc_customScanLineDrawable) {
            mCustomScanLineDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.JHDView_orc_borderSize) {
            mBorderSize = typedArray.getDimensionPixelSize(attr, mBorderSize);
        } else if (attr == R.styleable.JHDView_orc_borderColor) {
            mBorderColor = typedArray.getColor(attr, mBorderColor);
        } else if (attr == R.styleable.JHDView_orc_animTime) {
            mAnimTime = typedArray.getInteger(attr, mAnimTime);
        } else if (attr == R.styleable.JHDView_orc_isCenterVertical) {
            mIsCenterVertical = typedArray.getBoolean(attr, mIsCenterVertical);
        } else if (attr == R.styleable.JHDView_orc_toolbarHeight) {
            mToolbarHeight = typedArray.getDimensionPixelSize(attr, mToolbarHeight);
        } else if (attr == R.styleable.JHDView_orc_rectHeight) {
            mRectHeight = typedArray.getDimensionPixelSize(attr, mRectHeight);
        } else if (attr == R.styleable.JHDView_orc_isBarcode) {
            mIsBarcode = typedArray.getBoolean(attr, mIsBarcode);
        } else if (attr == R.styleable.JHDView_orc_tipText) {
            mTipText = typedArray.getString(attr);
        } else if (attr == R.styleable.JHDView_orc_tipTextSize) {
            mTipTextSize = typedArray.getDimensionPixelSize(attr, mTipTextSize);
        } else if (attr == R.styleable.JHDView_orc_tipTextColor) {
            mTipTextColor = typedArray.getColor(attr, mTipTextColor);
        } else if (attr == R.styleable.JHDView_orc_isTipTextBelowRect) {
            mIsTipTextBelowRect = typedArray.getBoolean(attr, mIsTipTextBelowRect);
        } else if (attr == R.styleable.JHDView_orc_tipTextMargin) {
            mTipTextMargin = typedArray.getDimensionPixelSize(attr, mTipTextMargin);
        } else if (attr == R.styleable.JHDView_orc_isShowTipTextAsSingleLine) {
            mIsShowTipTextAsSingleLine = typedArray.getBoolean(attr, mIsShowTipTextAsSingleLine);
        } else if (attr == R.styleable.JHDView_orc_isShowTipBackground) {
            mIsShowTipBackground = typedArray.getBoolean(attr, mIsShowTipBackground);
        } else if (attr == R.styleable.JHDView_orc_tipBackgroundColor) {
            mTipBackgroundColor = typedArray.getColor(attr, mTipBackgroundColor);
        } else if (attr == R.styleable.JHDView_orc_isScanLineReverse) {
            mIsScanLineReverse = typedArray.getBoolean(attr, mIsScanLineReverse);
        } else if (attr == R.styleable.JHDView_orc_isShowDefaultGridScanLineDrawable) {
            mIsShowDefaultGridScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultGridScanLineDrawable);
        } else if (attr == R.styleable.JHDView_orc_customGridScanLineDrawable) {
            mCustomGridScanLineDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.JHDView_orc_isMoveScanLine) {
            mIsMoveScanLine = typedArray.getBoolean(attr, mIsMoveScanLine);
        }
    }

    private void afterInitCustomAttrs() {
        if (mIsShowDefaultGridScanLineDrawable) {
            mOriginGridScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jhdview_default_grid_scan_line);
            mOriginGridScanLineBitmap = CommonUtil.makeTintBitmap(mOriginGridScanLineBitmap, mScanLineColor);
        }
        if (mCustomGridScanLineDrawable != null) {
            mOriginGridScanLineBitmap = ((BitmapDrawable) mCustomGridScanLineDrawable).getBitmap();
        }
        if (mOriginGridScanLineBitmap != null) {
            mOriginGridScanLineBitmap = CommonUtil.adjustPhotoRotation(mOriginGridScanLineBitmap, 90);
            mOriginGridScanLineBitmap = CommonUtil.adjustPhotoRotation(mOriginGridScanLineBitmap, 90);
            mOriginGridScanLineBitmap = CommonUtil.adjustPhotoRotation(mOriginGridScanLineBitmap, 90);
        }

        if (mIsShowDefaultScanLineDrawable) {
            mOriginScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jhdview_default_scan_line);
            mOriginScanLineBitmap = CommonUtil.makeTintBitmap(mOriginScanLineBitmap, mScanLineColor);
        }
        if (mCustomScanLineDrawable != null) {
            mOriginScanLineBitmap = ((BitmapDrawable) mCustomScanLineDrawable).getBitmap();
        }
        if (mOriginScanLineBitmap != null) {
            mOriginScanLineBitmap = CommonUtil.adjustPhotoRotation(mOriginScanLineBitmap, 90);
        }

        mTopOffset += mToolbarHeight;
        mHalfCornerSize = 1.0f * mCornerSize / 2;

        mTipPaint.setTextSize(mTipTextSize);
        mTipPaint.setColor(mTipTextColor);

        setIsBarcode(mIsBarcode);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mFramingRect == null) {
            return;
        }

        // 画遮罩层
        drawMask(canvas);


        // 画边框线
        drawBorderLine(canvas);

        // 画四个直角的线
        drawCornerLine(canvas);
        //drawCircle(canvas);

        // 画提示文本
        drawTipText(canvas);

        if (mIsMoveScanLine) {
            // 画扫描线
            drawScanLine(canvas);
            // 移动扫描线的位置
            moveScanLine();
        }

    }

    /**
     * 画遮罩层
     *
     * @param canvas
     */
    private void drawMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mMaskColor != Color.TRANSPARENT) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mMaskColor);
            canvas.drawRect(0, 0, width, mFramingRect.top, mPaint);
            canvas.drawRect(0, mFramingRect.top, mFramingRect.left, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(mFramingRect.right + 1, mFramingRect.top, width, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(0, mFramingRect.bottom + 1, width, height, mPaint);
        }
    }

    /**
     * 画边框线
     *
     * @param canvas
     */
    private void drawBorderLine(Canvas canvas) {
        if (mBorderSize > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(mBorderSize);
            canvas.drawRect(mFramingRect, mPaint);
        }
    }

    /**
     * 画圆
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {

        if (mMaskColor != Color.TRANSPARENT) {
            canvas.save();
            int circleMiddlePointX = mFramingRect.left + (mFramingRect.right - mFramingRect.left) / 2;
            int circleMiddlePointY = mFramingRect.top + (mFramingRect.bottom - mFramingRect.top) / 2;
            int radial = circleMiddlePointX - mFramingRect.left - 4;

            Path mPath = new Path();
            mPath.addCircle(circleMiddlePointX, circleMiddlePointY, radial, Direction.CCW);
            canvas.clipPath(mPath, Region.Op.DIFFERENCE);
            canvas.drawColor(mMaskColor);
            canvas.restore();

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(4);
            canvas.drawCircle(circleMiddlePointX, circleMiddlePointY, radial, mPaint);
        }
    }

    /**
     * 画人脸提示框
     *
     * @param canvas
     */
    private void drawHuman(Canvas canvas) {

    }

    /**
     * 画四个直角的线
     *
     * @param canvas
     */
    private void drawCornerLine(Canvas canvas) {
        if (mHalfCornerSize > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mCornerColor);
            mPaint.setStrokeWidth(mCornerSize);
            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.top, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.top - mHalfCornerSize, mFramingRect.left, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.top, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.top - mHalfCornerSize, mFramingRect.right, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);

            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.bottom, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.bottom + mHalfCornerSize, mFramingRect.left, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.bottom, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.bottom + mHalfCornerSize, mFramingRect.right, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
        }
    }

    /**
     * 画扫描线
     *
     * @param canvas
     */
    private void drawScanLine(Canvas canvas) {
        if (mIsBarcode) {
            if (mGridScanLineBitmap != null) {
                RectF dstGridRectF = new RectF(mFramingRect.left + mHalfCornerSize + 0.5f, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mGridScanLineRight, mFramingRect.bottom - mHalfCornerSize - mScanLineMargin);

                Rect srcGridRect = new Rect((int) (mGridScanLineBitmap.getWidth() - dstGridRectF.width()), 0, mGridScanLineBitmap.getWidth(), mGridScanLineBitmap.getHeight());

                if (srcGridRect.left < 0) {
                    srcGridRect.left = 0;
                    dstGridRectF.left = dstGridRectF.right - srcGridRect.width();
                }

                canvas.drawBitmap(mGridScanLineBitmap, srcGridRect, dstGridRectF, mPaint);
            } else if (mScanLineBitmap != null) {
                RectF lineRect = new RectF(mScanLineLeft, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineBitmap.getWidth(), mFramingRect.bottom - mHalfCornerSize - mScanLineMargin);
                canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mScanLineColor);
                canvas.drawRect(mScanLineLeft, mFramingRect.top + mHalfCornerSize + mScanLineMargin, mScanLineLeft + mScanLineSize, mFramingRect.bottom - mHalfCornerSize - mScanLineMargin, mPaint);
            }
        } else {
            if (mGridScanLineBitmap != null) {
                RectF dstGridRectF = new RectF(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mFramingRect.top + mHalfCornerSize + 0.5f, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mGridScanLineBottom);

                Rect srcRect = new Rect(0, (int) (mGridScanLineBitmap.getHeight() - dstGridRectF.height()), mGridScanLineBitmap.getWidth(), mGridScanLineBitmap.getHeight());

                if (srcRect.top < 0) {
                    srcRect.top = 0;
                    dstGridRectF.top = dstGridRectF.bottom - srcRect.height();
                }

                canvas.drawBitmap(mGridScanLineBitmap, srcRect, dstGridRectF, mPaint);
            } else if (mScanLineBitmap != null) {
                RectF lineRect = new RectF(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineBitmap.getHeight());
                canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mScanLineColor);
                canvas.drawRect(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineSize, mPaint);
            }
        }
    }

    public Rect getFramingRect() {
        return mFramingRect;
    }

    /**
     * 画提示文本
     *
     * @param canvas
     */
    private void drawTipText(Canvas canvas) {
        if (TextUtils.isEmpty(mTipText) || mTipTextSl == null) {
            return;
        }

        if (mIsTipTextBelowRect) {
            if (mIsShowTipBackground) {
                mPaint.setColor(mTipBackgroundColor);
                mPaint.setStyle(Paint.Style.FILL);
                if (mIsShowTipTextAsSingleLine) {
                    Rect tipRect = new Rect();
                    mTipPaint.getTextBounds(mTipText, 0, mTipText.length(), tipRect);
                    float left = (canvas.getWidth() - tipRect.width()) / 2 - mTipBackgroundRadius;
                    canvas.drawRoundRect(new RectF(left, mFramingRect.bottom + mTipTextMargin - mTipBackgroundRadius, left + tipRect.width() + 2 * mTipBackgroundRadius, mFramingRect.bottom + mTipTextMargin + mTipTextSl.getHeight() + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                } else {
                    canvas.drawRoundRect(new RectF(mFramingRect.left, mFramingRect.bottom + mTipTextMargin - mTipBackgroundRadius, mFramingRect.right, mFramingRect.bottom + mTipTextMargin + mTipTextSl.getHeight() + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                }
            }

            canvas.save();
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0, mFramingRect.bottom + mTipTextMargin);
            } else {
                canvas.translate(mFramingRect.left + mTipBackgroundRadius, mFramingRect.bottom + mTipTextMargin);
            }
            mTipTextSl.draw(canvas);
            canvas.restore();
        } else {
            if (mIsShowTipBackground) {
                mPaint.setColor(mTipBackgroundColor);
                mPaint.setStyle(Paint.Style.FILL);

                if (mIsShowTipTextAsSingleLine) {
                    Rect tipRect = new Rect();
                    mTipPaint.getTextBounds(mTipText, 0, mTipText.length(), tipRect);
                    float left = (canvas.getWidth() - tipRect.width()) / 2 - mTipBackgroundRadius;
                    canvas.drawRoundRect(new RectF(left, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight() - mTipBackgroundRadius, left + tipRect.width() + 2 * mTipBackgroundRadius, mFramingRect.top - mTipTextMargin + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                } else {
                    canvas.drawRoundRect(new RectF(mFramingRect.left, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight() - mTipBackgroundRadius, mFramingRect.right, mFramingRect.top - mTipTextMargin + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                }
            }

            canvas.save();
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight());
            } else {
                canvas.translate(mFramingRect.left + mTipBackgroundRadius, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight());
            }
            mTipTextSl.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 移动扫描线的位置
     */
    private void moveScanLine() {
        if (mIsBarcode) {
            if (mGridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                mScanLineLeft += mMoveStepDistance;
                int scanLineSize = mScanLineSize;
                if (mScanLineBitmap != null) {
                    scanLineSize = mScanLineBitmap.getWidth();
                }

                if (mIsScanLineReverse) {
                    if (mScanLineLeft + scanLineSize > mFramingRect.right - mHalfCornerSize || mScanLineLeft < mFramingRect.left + mHalfCornerSize) {
                        mMoveStepDistance = -mMoveStepDistance;
                    }
                } else {
                    if (mScanLineLeft + scanLineSize > mFramingRect.right - mHalfCornerSize) {
                        mScanLineLeft = mFramingRect.left + mHalfCornerSize + 0.5f;
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                mGridScanLineRight += mMoveStepDistance;
                if (mGridScanLineRight > mFramingRect.right - mHalfCornerSize) {
                    mGridScanLineRight = mFramingRect.left + mHalfCornerSize + 0.5f;
                }
            }
        } else {
            if (mGridScanLineBitmap == null) {
                // 处理非网格扫描图片的情况
                mScanLineTop += mMoveStepDistance;
                int scanLineSize = mScanLineSize;
                if (mScanLineBitmap != null) {
                    scanLineSize = mScanLineBitmap.getHeight();
                }

                if (mIsScanLineReverse) {
                    if (mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize || mScanLineTop < mFramingRect.top + mHalfCornerSize) {
                        mMoveStepDistance = -mMoveStepDistance;
                    }
                } else {
                    if (mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize) {
                        mScanLineTop = mFramingRect.top + mHalfCornerSize + 0.5f;
                    }
                }
            } else {
                // 处理网格扫描图片的情况
                mGridScanLineBottom += mMoveStepDistance;
                if (mGridScanLineBottom > mFramingRect.bottom - mHalfCornerSize) {
                    mGridScanLineBottom = mFramingRect.top + mHalfCornerSize + 0.5f;
                }
            }

        }
        postInvalidateDelayed(mAnimDelayTime, mFramingRect.left, mFramingRect.top, mFramingRect.right, mFramingRect.bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calFramingRect();
    }

    private void calFramingRect() {
        int leftOffset = (getWidth() - mRectWidth) / 2;
        mFramingRect = new Rect(leftOffset, mTopOffset, leftOffset + mRectWidth, mTopOffset + mRectHeight);

        if (mIsBarcode) {
            mGridScanLineRight = mScanLineLeft = mFramingRect.left + mHalfCornerSize + 0.5f;
        } else {
            mGridScanLineBottom = mScanLineTop = mFramingRect.top + mHalfCornerSize + 0.5f;
        }
    }

    public Rect getScanBoxAreaRect(int previewHeight) {
        Rect rect = new Rect(mFramingRect);
        float ratio = 1.0f * previewHeight / getMeasuredHeight();
        rect.left = (int) (rect.left * ratio);
        rect.right = (int) (rect.right * ratio);
        rect.top = (int) (rect.top * ratio);
        rect.bottom = (int) (rect.bottom * ratio);
        return rect;
    }

    public Rect getScanBoxAreaRect() {
        Rect rect = new Rect(mFramingRect);
//        rect.left = (int) (rect.left);
//        rect.right = (int) (rect.right);
//        rect.top = (int) (rect.top);
//        rect.bottom = (int) (rect.bottom);
        return rect;
    }

    public void setIsBarcode(boolean isBarcode) {
        mIsBarcode = isBarcode;

        if (mCustomGridScanLineDrawable != null || mIsShowDefaultGridScanLineDrawable) {
            mGridScanLineBitmap = mOriginGridScanLineBitmap;
        } else if (mCustomScanLineDrawable != null || mIsShowDefaultScanLineDrawable) {
            mScanLineBitmap = mOriginScanLineBitmap;
        }

        if (mIsBarcode) {
            mAnimDelayTime = (int) ((1.0f * mAnimTime * mMoveStepDistance) / mRectWidth);
        } else {
            mRectHeight = mRectHeight != 0 ? mRectHeight : mRectWidth;
            mAnimDelayTime = (int) ((1.0f * mAnimTime * mMoveStepDistance) / mRectHeight);
        }

        if (!TextUtils.isEmpty(mTipText)) {
            if (mIsShowTipTextAsSingleLine) {
                mTipTextSl = new StaticLayout(mTipText, mTipPaint, InfoUtil.getScreenResolution(getContext()).x, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, true);
            } else {
                mTipTextSl = new StaticLayout(mTipText, mTipPaint, mRectWidth - 2 * mTipBackgroundRadius, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, true);
            }
        }

        if (mIsCenterVertical) {
            int screenHeight = InfoUtil.getScreenResolution(getContext()).y;
            if (mToolbarHeight == 0) {
                mTopOffset = (screenHeight - mRectHeight) / 2;
            } else {
                mTopOffset = (screenHeight - mRectHeight) / 2 + mToolbarHeight / 2;
            }
        }

        calFramingRect();

        postInvalidate();
    }

    public boolean getIsBarcode() {
        return mIsBarcode;
    }

    public int getMaskColor() {
        return mMaskColor;
    }

    public void setMaskColor(int maskColor) {
        mMaskColor = maskColor;
    }

    public int getCornerColor() {
        return mCornerColor;
    }

    public void setCornerColor(int cornerColor) {
        mCornerColor = cornerColor;
    }

    public int getCornerLength() {
        return mCornerLength;
    }

    public void setCornerLength(int cornerLength) {
        mCornerLength = cornerLength;
    }

    public int getCornerSize() {
        return mCornerSize;
    }

    public void setCornerSize(int cornerSize) {
        mCornerSize = cornerSize;
    }

    public int getRectWidth() {
        return mRectWidth;
    }

    public void setRectWidth(int rectWidth) {
        mRectWidth = rectWidth;
    }

    public int getRectHeight() {
        return mRectHeight;
    }

    public void setRectHeight(int rectHeight) {
        mRectHeight = rectHeight;
    }


    public int getTopOffset() {
        return mTopOffset;
    }

    public void setTopOffset(int topOffset) {
        mTopOffset = topOffset;
    }

    public int getScanLineSize() {
        return mScanLineSize;
    }

    public void setScanLineSize(int scanLineSize) {
        mScanLineSize = scanLineSize;
    }

    public int getScanLineColor() {
        return mScanLineColor;
    }

    public void setScanLineColor(int scanLineColor) {
        mScanLineColor = scanLineColor;
    }

    public int getScanLineMargin() {
        return mScanLineMargin;
    }

    public void setScanLineMargin(int scanLineMargin) {
        mScanLineMargin = scanLineMargin;
    }

    public boolean isShowDefaultScanLineDrawable() {
        return mIsShowDefaultScanLineDrawable;
    }

    public void setShowDefaultScanLineDrawable(boolean showDefaultScanLineDrawable) {
        mIsShowDefaultScanLineDrawable = showDefaultScanLineDrawable;
    }

    public Drawable getCustomScanLineDrawable() {
        return mCustomScanLineDrawable;
    }

    public void setCustomScanLineDrawable(Drawable customScanLineDrawable) {
        mCustomScanLineDrawable = customScanLineDrawable;
    }

    public Bitmap getScanLineBitmap() {
        return mScanLineBitmap;
    }

    public void setScanLineBitmap(Bitmap scanLineBitmap) {
        mScanLineBitmap = scanLineBitmap;
    }

    public int getBorderSize() {
        return mBorderSize;
    }

    public void setBorderSize(int borderSize) {
        mBorderSize = borderSize;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public int getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(int animTime) {
        mAnimTime = animTime;
    }

    public boolean isCenterVertical() {
        return mIsCenterVertical;
    }

    public void setCenterVertical(boolean centerVertical) {
        mIsCenterVertical = centerVertical;
    }

    public int getToolbarHeight() {
        return mToolbarHeight;
    }

    public void setToolbarHeight(int toolbarHeight) {
        mToolbarHeight = toolbarHeight;
    }


    public String getTipText() {
        return mTipText;
    }

    public void setTipText(String tipText) {
        mTipText = tipText;
    }

    public int getTipTextColor() {
        return mTipTextColor;
    }

    public void setTipTextColor(int tipTextColor) {
        mTipTextColor = tipTextColor;
    }

    public int getTipTextSize() {
        return mTipTextSize;
    }

    public void setTipTextSize(int tipTextSize) {
        mTipTextSize = tipTextSize;
    }

    public boolean isTipTextBelowRect() {
        return mIsTipTextBelowRect;
    }

    public void setTipTextBelowRect(boolean tipTextBelowRect) {
        mIsTipTextBelowRect = tipTextBelowRect;
    }

    public int getTipTextMargin() {
        return mTipTextMargin;
    }

    public void setTipTextMargin(int tipTextMargin) {
        mTipTextMargin = tipTextMargin;
    }

    public boolean isShowTipTextAsSingleLine() {
        return mIsShowTipTextAsSingleLine;
    }

    public void setShowTipTextAsSingleLine(boolean showTipTextAsSingleLine) {
        mIsShowTipTextAsSingleLine = showTipTextAsSingleLine;
    }

    public boolean isShowTipBackground() {
        return mIsShowTipBackground;
    }

    public void setShowTipBackground(boolean showTipBackground) {
        mIsShowTipBackground = showTipBackground;
    }

    public int getTipBackgroundColor() {
        return mTipBackgroundColor;
    }

    public void setTipBackgroundColor(int tipBackgroundColor) {
        mTipBackgroundColor = tipBackgroundColor;
    }

    public boolean isScanLineReverse() {
        return mIsScanLineReverse;
    }

    public void setScanLineReverse(boolean scanLineReverse) {
        mIsScanLineReverse = scanLineReverse;
    }

    public boolean isShowDefaultGridScanLineDrawable() {
        return mIsShowDefaultGridScanLineDrawable;
    }

    public void setShowDefaultGridScanLineDrawable(boolean showDefaultGridScanLineDrawable) {
        mIsShowDefaultGridScanLineDrawable = showDefaultGridScanLineDrawable;
    }

    public float getHalfCornerSize() {
        return mHalfCornerSize;
    }

    public void setHalfCornerSize(float halfCornerSize) {
        mHalfCornerSize = halfCornerSize;
    }

    public StaticLayout getTipTextSl() {
        return mTipTextSl;
    }

    public void setTipTextSl(StaticLayout tipTextSl) {
        mTipTextSl = tipTextSl;
    }

    public int getTipBackgroundRadius() {
        return mTipBackgroundRadius;
    }

    public void setTipBackgroundRadius(int tipBackgroundRadius) {
        mTipBackgroundRadius = tipBackgroundRadius;
    }

    public boolean isMoveScanLine() {
        return mIsMoveScanLine;
    }

    public void setMoveScanLine(boolean moveScanLine) {
        mIsMoveScanLine = moveScanLine;
    }

}