
package com.myphotostudio;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;

public class CropSelectionView extends View {

    private Bitmap bitmap;

    private RectF imageRect = new RectF();
    private RectF cropRect = new RectF();

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float downX, downY;
    private boolean resizing = false;
    private boolean moving = false;
    private int activeCorner = -1;

    private final float minCropSize = 50f;

    public CropSelectionView(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h,
                                 int oldW, int oldH) {

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

        float insetX = bw * 0.08f;
        float insetY = bh * 0.08f;

        cropRect.set(
            imageRect.left + insetX,
            imageRect.top + insetY,
            imageRect.right - insetX,
            imageRect.bottom - insetY
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
            float x = cropRect.left +
                cropRect.width() * i / 3;

            float y = cropRect.top +
                cropRect.height() * i / 3;

            canvas.drawLine(
                x, cropRect.top,
                x, cropRect.bottom, paint
            );

            canvas.drawLine(
                cropRect.left, y,
                cropRect.right, y, paint
            );
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        float handle = 18;

        canvas.drawCircle(cropRect.left,
            cropRect.top, handle, paint);

        canvas.drawCircle(cropRect.right,
            cropRect.top, handle, paint);

        canvas.drawCircle(cropRect.left,
            cropRect.bottom, handle, paint);

        canvas.drawCircle(cropRect.right,
            cropRect.bottom, handle, paint);
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

                float handle = 55;

                if (Math.abs(x - cropRect.left) < handle &&
                    Math.abs(y - cropRect.top) < handle) {
                    activeCorner = 0;

                } else if (
                    Math.abs(x - cropRect.right) < handle &&
                    Math.abs(y - cropRect.top) < handle) {
                    activeCorner = 1;

                } else if (
                    Math.abs(x - cropRect.left) < handle &&
                    Math.abs(y - cropRect.bottom) < handle) {
                    activeCorner = 2;

                } else if (
                    Math.abs(x - cropRect.right) < handle &&
                    Math.abs(y - cropRect.bottom) < handle) {
                    activeCorner = 3;
                }

                resizing = activeCorner != -1;
                moving = !resizing && cropRect.contains(x, y);

                return true;

            case MotionEvent.ACTION_MOVE:

                if (resizing) {

                    float minW = Math.min(
                        minCropSize, imageRect.width() / 3
                    );

                    float minH = Math.min(
                        minCropSize, imageRect.height() / 3
                    );

                    switch (activeCorner) {

                        case 0:
                            cropRect.left = Math.max(
                                imageRect.left,
                                Math.min(x, cropRect.right - minW)
                            );
                            cropRect.top = Math.max(
                                imageRect.top,
                                Math.min(y, cropRect.bottom - minH)
                            );
                            break;

                        case 1:
                            cropRect.right = Math.min(
                                imageRect.right,
                                Math.max(x, cropRect.left + minW)
                            );
                            cropRect.top = Math.max(
                                imageRect.top,
                                Math.min(y, cropRect.bottom - minH)
                            );
                            break;

                        case 2:
                            cropRect.left = Math.max(
                                imageRect.left,
                                Math.min(x, cropRect.right - minW)
                            );
                            cropRect.bottom = Math.min(
                                imageRect.bottom,
                                Math.max(y, cropRect.top + minH)
                            );
                            break;

                        case 3:
                            cropRect.right = Math.min(
                                imageRect.right,
                                Math.max(x, cropRect.left + minW)
                            );
                            cropRect.bottom = Math.min(
                                imageRect.bottom,
                                Math.max(y, cropRect.top + minH)
                            );
                            break;
                    }

                } else if (moving) {

                    float dx = x - downX;
                    float dy = y - downY;

                    float left = Math.max(
                        imageRect.left,
                        Math.min(
                            imageRect.right - cropRect.width(),
                            cropRect.left + dx
                        )
                    );

                    float top = Math.max(
                        imageRect.top,
                        Math.min(
                            imageRect.bottom - cropRect.height(),
                            cropRect.top + dy
                        )
                    );

                    cropRect.offsetTo(left, top);
                }

                downX = x;
                downY = y;

                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                resizing = false;
                moving = false;
                activeCorner = -1;

                invalidate();
                return true;
        }

        return true;
    }

    public Rect getBitmapCropRect() {

        float sx = bitmap.getWidth() /
            imageRect.width();

        float sy = bitmap.getHeight() /
            imageRect.height();

        int left = Math.max(0,
            (int)((cropRect.left - imageRect.left) * sx));

        int top = Math.max(0,
            (int)((cropRect.top - imageRect.top) * sy));

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
