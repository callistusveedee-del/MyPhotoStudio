
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

                resizing =
                    Math.abs(x - cropRect.right) < 45 &&
                    Math.abs(y - cropRect.bottom) < 45;

                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = x - downX;
                float dy = y - downY;

                if (resizing) {
                    float newWidth = Math.max(
                        80, cropRect.width() + dx
                    );

                    float newHeight = newWidth / ratio;

                    if (cropRect.left + newWidth > imageRect.right) {
                        newWidth = imageRect.right - cropRect.left;
                        newHeight = newWidth / ratio;
                    }

                    if (cropRect.top + newHeight > imageRect.bottom) {
                        newHeight = imageRect.bottom - cropRect.top;
                        newWidth = newHeight * ratio;
                    }

                    cropRect.right = cropRect.left + newWidth;
                    cropRect.bottom = cropRect.top + newHeight;

                } else if (cropRect.contains(downX, downY)) {
                    float maxX = imageRect.right - cropRect.width();
                    float maxY = imageRect.bottom - cropRect.height();

                    float left = Math.max(
                        imageRect.left,
                        Math.min(maxX, cropRect.left + dx)
                    );

                    float top = Math.max(
                        imageRect.top,
                        Math.min(maxY, cropRect.top + dy)
                    );

                    cropRect.offsetTo(left, top);
                }

                downX = x;
                downY = y;
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
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
