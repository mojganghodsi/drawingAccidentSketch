package com.example.mypc.paint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.content.Context;

/**
 * Created by My PC on 9/2/2017.
 */

public class KrookiPreview extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kroo_preview);
        Log.e("create", "here");
        ImageView im = findViewById(R.id.pre);
        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");
        Log.e("chizi dakhelesh hast?",bitmap.toString());
        im.setImageBitmap(bitmap);

    }
}
