package com.myphotostudio;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.*;
import android.widget.*;
import android.view.Gravity;

public class MainActivity extends Activity {

    ImageView photo;
    SeekBar brightness, contrast;
    TextView status;

    Bitmap originalBitmap;
    Bitmap rotatedBitmap;
    Bitmap pendingSaveBitmap;

    int rotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 12, 16, 12);
        layout.setBackgroundColor(Color.rgb(245,245,250));

        TextView title = new TextView(this);
        title.setText("MyPhotoStudio");
        title.setTextSize(27);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        photo = new ImageView(this);
        photo.setBackgroundColor(Color.LTGRAY);
        photo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        layout.addView(photo, new LinearLayout.LayoutParams(
            -1, 0, 1
        ));

        Button choose = new Button(this);
        choose.setText("Choose Photo");
        layout.addView(choose);

        choose.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 100);
        });

        Button cropButton = new Button(this);
        cropButton.setText("CROP PHOTO");
        layout.addView(cropButton);

        cropButton.setOnClickListener(v -> showCropOptions());

        TextView rotateLabel = new TextView(this);
        rotateLabel.setText("Photo Rotation");
        rotateLabel.setTextColor(Color.BLACK);
        rotateLabel.setTextSize(17);
        layout.addView(rotateLabel);

        LinearLayout rotateRow = new LinearLayout(this);
        rotateRow.setOrientation(LinearLayout.HORIZONTAL);

        addRotationButton(rotateRow, "90°", 90);
        addRotationButton(rotateRow, "180°", 180);
        addRotationButton(rotateRow, "270°", 270);

        Button reset = new Button(this);
        reset.setText("Reset");
        rotateRow.addView(reset,
            new LinearLayout.LayoutParams(0, -2, 1));

        reset.setOnClickListener(v -> {
            rotation = 0;
            updatePhoto();
        });

        layout.addView(rotateRow);

        TextView brightnessLabel = new TextView(this);
        brightnessLabel.setText("Brightness");
        brightnessLabel.setTextColor(Color.BLACK);
        layout.addView(brightnessLabel);

        brightness = new SeekBar(this);
        brightness.setMax(200);
        brightness.setProgress(100);
        layout.addView(brightness);

        TextView contrastLabel = new TextView(this);
        contrastLabel.setText("Contrast");
        contrastLabel.setTextColor(Color.BLACK);
        layout.addView(contrastLabel);

        contrast = new SeekBar(this);
        contrast.setMax(200);
        contrast.setProgress(100);
        layout.addView(contrast);

        brightness.setOnSeekBarChangeListener(listener());
        contrast.setOnSeekBarChangeListener(listener());

        status = new TextView(this);
        status.setText("Choose a photo to start editing");
        status.setTextColor(Color.DKGRAY);
        status.setGravity(Gravity.CENTER);
        layout.addView(status);

        Button save = new Button(this);
        save.setText("Save Edited Photo");
        layout.addView(save);
        save.setOnClickListener(v -> savePhoto());

        setContentView(layout);
    }

    void showCropOptions() {
        if (originalBitmap == null) {
            Toast.makeText(this, "Choose a photo first",
                Toast.LENGTH_SHORT).show();
            return;
        }

        String[] choices = {
            "Square (1:1)",
            "Portrait (4:5)",
            "Landscape (16:9)",
            "Story (9:16)",
            "Wide (4:3)"
        };

        float[] ratios = {
            1f, 4f / 5f, 16f / 9f, 9f / 16f, 4f / 3f
        };

        new AlertDialog.Builder(this)
            .setTitle("Choose Crop Size")
            .setItems(choices, (dialog, which) -> {
                applyCrop(ratios[which]);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    void applyCrop(float targetRatio) {
        Bitmap edited = createEditedBitmap();

        if (edited == null) return;

        int width = edited.getWidth();
        int height = edited.getHeight();

        float currentRatio = (float) width / height;

        int cropWidth = width;
        int cropHeight = height;

        if (currentRatio > targetRatio) {
            cropWidth = Math.round(height * targetRatio);
        } else {
            cropHeight = Math.round(width / targetRatio);
        }

        int left = (width - cropWidth) / 2;
        int top = (height - cropHeight) / 2;

        Bitmap cropped = Bitmap.createBitmap(
            edited, left, top, cropWidth, cropHeight
        );

        edited.recycle();

        if (rotatedBitmap != null &&
            rotatedBitmap != originalBitmap) {
            rotatedBitmap.recycle();
        }

        if (originalBitmap != null) {
            originalBitmap.recycle();
        }

        originalBitmap = cropped;
        rotatedBitmap = null;
        rotation = 0;

        brightness.setProgress(100);
        contrast.setProgress(100);

        updatePhoto();

        status.setText("Crop applied successfully!");

        Toast.makeText(this, "Photo cropped successfully!",
            Toast.LENGTH_SHORT).show();
    }

    void addRotationButton(
        LinearLayout row, String text, int degrees) {

        Button button = new Button(this);
        button.setText(text);

        row.addView(button,
            new LinearLayout.LayoutParams(0, -2, 1));

        button.setOnClickListener(v -> {
            rotation = (rotation + degrees) % 360;
            updatePhoto();
        });
    }

    SeekBar.OnSeekBarChangeListener listener() {
        return new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(
                SeekBar s, int progress, boolean fromUser) {
                updatePhoto();
            }

            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        };
    }

    Bitmap rotateBitmap(Bitmap source, int degrees) {
        if (source == null || degrees == 0) return source;

        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        return Bitmap.createBitmap(
            source, 0, 0,
            source.getWidth(), source.getHeight(),
            matrix, true
        );
    }

    ColorMatrix getColorMatrix() {
        float b = brightness.getProgress() - 100;
        float c = contrast.getProgress() / 100f;
        float offset = 128 * (1 - c) + b;

        return new ColorMatrix(new float[] {
            c,0,0,0,offset,
            0,c,0,0,offset,
            0,0,c,0,offset,
            0,0,0,1,0
        });
    }

    void updatePhoto() {
        if (originalBitmap == null) return;

        if (rotatedBitmap != null &&
            rotatedBitmap != originalBitmap) {
            rotatedBitmap.recycle();
        }

        rotatedBitmap = rotateBitmap(originalBitmap, rotation);
        photo.setImageBitmap(rotatedBitmap);

        photo.setColorFilter(
            new ColorMatrixColorFilter(getColorMatrix())
        );

        status.setText("Editing photo...");
    }

    Bitmap createEditedBitmap() {
        if (originalBitmap == null) return null;

        Bitmap rotated = rotateBitmap(originalBitmap, rotation);

        Bitmap result = Bitmap.createBitmap(
            rotated.getWidth(),
            rotated.getHeight(),
            Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        paint.setColorFilter(
            new ColorMatrixColorFilter(getColorMatrix())
        );

        canvas.drawBitmap(rotated, 0, 0, paint);

        if (rotated != originalBitmap) {
            rotated.recycle();
        }

        return result;
    }

    void savePhoto() {
        if (originalBitmap == null) {
            Toast.makeText(this, "Choose a photo first",
                Toast.LENGTH_SHORT).show();
            return;
        }

        pendingSaveBitmap = createEditedBitmap();

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_TITLE, "MyPhotoStudio_Edit.jpg");

        startActivityForResult(intent, 101);
    }

    Bitmap loadImage(Uri uri) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        java.io.InputStream stream =
            getContentResolver().openInputStream(uri);

        BitmapFactory.decodeStream(stream, null, options);
        stream.close();

        int sample = 1;

        while (options.outWidth / sample > 2048 ||
               options.outHeight / sample > 2048) {
            sample *= 2;
        }

        BitmapFactory.Options finalOptions =
            new BitmapFactory.Options();
        finalOptions.inSampleSize = sample;

        stream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(
            stream, null, finalOptions);
        stream.close();

        return bitmap;
    }

    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            if (requestCode == 101 && pendingSaveBitmap != null) {
                pendingSaveBitmap.recycle();
                pendingSaveBitmap = null;
            }
            return;
        }

        if (requestCode == 100) {
            try {
                Bitmap loaded = loadImage(data.getData());

                if (loaded == null) {
                    throw new Exception("Unable to decode image");
                }

                if (rotatedBitmap != null &&
                    rotatedBitmap != originalBitmap) {
                    rotatedBitmap.recycle();
                }

                if (originalBitmap != null) {
                    originalBitmap.recycle();
                }

                originalBitmap = loaded;
                rotatedBitmap = null;
                rotation = 0;

                brightness.setProgress(100);
                contrast.setProgress(100);

                updatePhoto();
                status.setText("Photo loaded successfully!");

            } catch (Exception e) {
                status.setText("Unable to load photo");
                Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == 101 && pendingSaveBitmap != null) {
            try {
                java.io.OutputStream output =
                    getContentResolver().openOutputStream(
                        data.getData());

                if (output == null) {
                    throw new Exception("Cannot open save location");
                }

                pendingSaveBitmap.compress(
                    Bitmap.CompressFormat.JPEG, 95, output);

                output.close();

                Toast.makeText(this, "Photo saved successfully!",
                    Toast.LENGTH_LONG).show();

                status.setText("Edited photo saved!");

            } catch (Exception e) {
                Toast.makeText(this, "Save failed: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            } finally {
                pendingSaveBitmap.recycle();
                pendingSaveBitmap = null;
            }
        }
    }
}
