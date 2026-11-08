
package com.myphotostudio;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

public class CropSelectionView extends View {

    private Bitmap bitmap;
    private float ratio;
    private RectF imageRect = new RectF();
    private RectF cropRect = new RectF();

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float downX, downY;
private boolean resizing = false;
private int activeCorner = -1;
    public CropSelectionView(Context context, Bitmap bitmap, float ratio) {
        super(context);
        this.bitmap = bitmap;
        this.ratio = ratio;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        float scale = Math.min(
            (float) w / bitmap.getWidth(),
            (float) h / bitmap.getHeight()
        );

        float bw = bitmap.getWidth() * scale;
        float bh = bitmap.getHeight() * scale;

        imageRect.set(
            (w - bw) / 2,
            (h - bh) / 2,
            (w + bw) / 2,
            (h + bh) / 2
        );

        float cw = bw;
        float ch = cw / ratio;

        if (ch > bh) {
            ch = bh;
            cw = ch * ratio;
        }

        cropRect.set(
            imageRect.centerX() - cw / 2,
            imageRect.centerY() - ch / 2,
            imageRect.centerX() + cw / 2,
            imageRect.centerY() + ch / 2
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawBitmap(bitmap, null, imageRect, paint);

        paint.setColor(0x99000000);

        canvas.drawRect(
            imageRect.left, imageRect.top,
            imageRect.right, cropRect.top, paint
        );

        canvas.drawRect(
            imageRect.left, cropRect.bottom,
            imageRect.right, imageRect.bottom, paint
        );

        canvas.drawRect(
            imageRect.left, cropRect.top,
            cropRect.left, cropRect.bottom, paint
        );

        canvas.drawRect(
            cropRect.right, cropRect.top,
            imageRect.right, cropRect.bottom, paint
        );

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(cropRect, paint);

        paint.setStrokeWidth(1);

        for (int i = 1; i < 3; i++) {
            float x = cropRect.left + cropRect.width() * i / 3;
            float y = cropRect.top + cropRect.height() * i / 3;

            canvas.drawLine(x, cropRect.top, x, cropRect.bottom, paint);
            canvas.drawLine(cropRect.left, y, cropRect.right, y, paint);
        }

        paint.setStyle(Paint.Style.FILL);

        float handle = 18;

        canvas.drawCircle(cropRect.left, cropRect.top, handle, paint);
        canvas.drawCircle(cropRect.right, cropRect.top, handle, paint);
        canvas.drawCircle(cropRect.left, cropRect.bottom, handle, paint);
        canvas.drawCircle(cropRect.right, cropRect.bottom, handle, paint);
    }

  @Override
public boolean onTouchEvent(MotionEvent event) {
    float x = event.getX();
    float y = event.getY();

    switch (event.getAction()) {

        case MotionEvent.ACTION_DOWN:
            downX = x;
            downY = y;
            activeCorner = -1;

            float handle = 45;

            if (Math.abs(x - cropRect.left) < handle &&
                Math.abs(y - cropRect.top) < handle) {
                activeCorner = 0;
            } else if (Math.abs(x - cropRect.right) < handle &&
                       Math.abs(y - cropRect.top) < handle) {
                activeCorner = 1;
            } else if (Math.abs(x - cropRect.left) < handle &&
                       Math.abs(y - cropRect.bottom) < handle) {
                activeCorner = 2;
            } else if (Math.abs(x - cropRect.right) < handle &&
                       Math.abs(y - cropRect.bottom) < handle) {
                activeCorner = 3;
            }

            resizing = activeCorner != -1;
            return true;

        case MotionEvent.ACTION_MOVE:

            if (resizing) {

                float anchorX =
                    (activeCorner == 0 || activeCorner == 2)
                    ? cropRect.right : cropRect.left;

                float anchorY =
                    (activeCorner == 0 || activeCorner == 1)
                    ? cropRect.bottom : cropRect.top;

                float sx =
                    (activeCorner == 0 || activeCorner == 2)
                    ? -1 : 1;

                float sy =
                    (activeCorner == 0 || activeCorner == 1)
                    ? -1 : 1;

                float maxW = sx < 0
                    ? anchorX - imageRect.left
                    : imageRect.right - anchorX;

                float maxH = sy < 0
                    ? anchorY - imageRect.top
                    : imageRect.bottom - anchorY;

                float maxWidth = Math.min(maxW, maxH * ratio);

                float width = Math.min(
                    maxWidth,
                    Math.max(40,
                        Math.max(
                            Math.abs(x - anchorX),
                            Math.abs(y - anchorY) * ratio
                        )
                    )
                );

                float height = width / ratio;

                cropRect.set(
                    sx < 0 ? anchorX - width : anchorX,
                    sy < 0 ? anchorY - height : anchorY,
                    sx < 0 ? anchorX : anchorX + width,
                    sy < 0 ? anchorY : anchorY + height
                );

            } else if (cropRect.contains(downX, downY)) {

                float left = Math.max(
                    imageRect.left,
                    Math.min(
                        imageRect.right - cropRect.width(),
                        cropRect.left + x - downX
                    )
                );

                float top = Math.max(
                    imageRect.top,
                    Math.min(
                        imageRect.bottom - cropRect.height(),
                        cropRect.top + y - downY
                    )
                );

                cropRect.offsetTo(left, top);
            }

            downX = x;
            downY = y;
            invalidate();
            return true;

        case MotionEvent.ACTION_UP:
            resizing = false;
            activeCorner = -1;
            return true;
    }

    return true;
}
    public Rect getBitmapCropRect() {
        float sx = bitmap.getWidth() / imageRect.width();
        float sy = bitmap.getHeight() / imageRect.height();

        int left = Math.max(0, (int)((cropRect.left - imageRect.left) * sx));
        int top = Math.max(0, (int)((cropRect.top - imageRect.top) * sy));

        int right = Math.min(
            bitmap.getWidth(),
            (int)((cropRect.right - imageRect.left) * sx)
        );

        int bottom = Math.min(
            bitmap.getHeight(),
            (int)((cropRect.bottom - imageRect.top) * sy)
        );

        return new Rect(left, top, right, bottom);
    }
}
