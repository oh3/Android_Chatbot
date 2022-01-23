package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class ImageActivity extends AppCompatActivity {

    private SubsamplingScaleImageView zoomImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // 비트맵으로 전달할 시...
        Intent intent = getIntent();
        byte[] arr = getIntent().getByteArrayExtra("image");
        final Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        zoomImage = (SubsamplingScaleImageView)findViewById(R.id.zoomImage);
        zoomImage.setImage(ImageSource.bitmap(image));

        zoomImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                return false;
            }
        });
    }
}