package com.myphotostudio;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.*;
import android.view.View;
import android.view.Gravity;

public class MainActivity extends Activity {

    ImageView photo;
    SeekBar brightness;
    TextView status;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        layout.setBackgroundColor(Color.rgb(245,245,250));

        TextView title = new TextView(this);
        title.setText("MyPhotoStudio");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        photo = new ImageView(this);
        photo.setBackgroundColor(Color.LTGRAY);
        photo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams imageParams =
            new LinearLayout.LayoutParams(
                -1, 0, 1
            );

        imageParams.setMargins(0, 25, 0, 20);
        layout.addView(photo, imageParams);

        Button choose = new Button(this);
        choose.setText("Choose Photo");
        layout.addView(choose);

        choose.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 100);
        });

        TextView label = new TextView(this);
        label.setText("Brightness");
        label.setTextSize(18);
        label.setTextColor(Color.BLACK);
        layout.addView(label);

        brightness = new SeekBar(this);
        brightness.setMax(200);
        brightness.setProgress(100);
        layout.addView(brightness);

        brightness.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(
                    SeekBar seekBar, int progress, boolean fromUser) {

                    float value = progress - 100;

                    ColorMatrix matrix = new ColorMatrix(
                        new float[] {
                            1,0,0,0,value,
                            0,1,0,0,value,
                            0,0,1,0,value,
                            0,0,0,1,0
                        }
                    );

                    photo.setColorFilter(
                        new ColorMatrixColorFilter(matrix)
                    );
                }

                public void onStartTrackingTouch(SeekBar s) {}
                public void onStopTrackingTouch(SeekBar s) {}
            }
        );

        status = new TextView(this);
        status.setText("Choose a photo to start editing");
        status.setTextColor(Color.DKGRAY);
        status.setGravity(Gravity.CENTER);
        layout.addView(status);

        setContentView(layout);
    }

    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 &&
            resultCode == RESULT_OK && data != null) {

            selectedImage = data.getData();
            photo.setImageURI(selectedImage);
            brightness.setProgress(100);
            photo.clearColorFilter();
            status.setText("Photo loaded successfully!");
        }
    }
}
