package cn.jhd.face.client.widget.view;

import android.graphics.Rect;

public class FaceDetectorResult {
    private int usableFaceCount;
    private Rect minRect;

    public int getUsableFaceCount() {
        return usableFaceCount;
    }

    public void setUsableFaceCount(int usableFaceCount) {
        this.usableFaceCount = usableFaceCount;
    }

    public Rect getMinRect() {
        return minRect;
    }

    public void setMinRect(Rect minRect) {
        this.minRect = minRect;
    }
}
