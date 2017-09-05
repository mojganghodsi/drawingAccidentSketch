package com.example.mypc.paint;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class KrooPreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kroo_preview);
        ImageView im = (ImageView) findViewById(R.id.pre);
        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
        im.setImageBitmap(bitmap);


    }
}
