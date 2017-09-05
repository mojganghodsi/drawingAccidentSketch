package com.example.mypc.paint;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

import static android.R.attr.bitmap;

public class MainActivity extends Activity {

    private DrawableView drawableView;
    private DrawableViewConfig config = new DrawableViewConfig();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();


    }

    private void initUi() {
        drawableView = (DrawableView) findViewById(R.id.paintView);
        Button strokeWidthMinusButton = (Button) findViewById(R.id.strokeWidthMinusButton);
        Button strokeWidthPlusButton = (Button) findViewById(R.id.strokeWidthPlusButton);
        Button changeColorButton = (Button) findViewById(R.id.changeColorButton);
        Button undoButton = (Button) findViewById(R.id.undoButton);
        final Button save = (Button) findViewById(R.id.save);

        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1080);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);

        strokeWidthPlusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() + 10);
            }
        });
        strokeWidthMinusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() - 10);
            }
        });
        changeColorButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                Random random = new Random();
                config.setStrokeColor(
                        Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                drawableView.undo();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                final Bitmap bm = drawableView.obtainBitmap();

                // constant factors
                final double GS_RED = 0.299;
                final double GS_GREEN = 0.587;
                final double GS_BLUE = 0.114;

                // create output bitmap
                Bitmap bmOut = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), bm.getConfig());
                // pixel information
                int A, R = 0, G, B;
                int pixel;

                // get image size
                int width = bm.getWidth();
                int height = bm.getHeight();

                // scan through every single pixel
                for(int x = 0; x < width; ++x) {
                    for(int y = 0; y < height; ++y) {
                        // get one pixel color
                        pixel = bm.getPixel(x, y);
                        // retrieve color of all channels
                        A = Color.alpha(pixel);
                        R = Color.red(pixel);
                        G = Color.green(pixel);
                        B = Color.blue(pixel);
                        // take conversion up to one single value
                        R = G = B = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                        // set new pixel color to output bitmap
                        bmOut.setPixel(x, y, Color.argb(A, R, G, B));
                    }
                }


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmOut.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();



                /* Get the image as string */
                // Normal
                ByteArrayOutputStream full_stream = new ByteArrayOutputStream();
                bmOut.compress(Bitmap.CompressFormat.PNG, 100, full_stream);
                byte[] full_bytes = full_stream.toByteArray();
                String img_full = Base64.encodeToString(full_bytes, Base64.DEFAULT);



                Context context = getApplicationContext();
                CharSequence text = byteArray.toString();
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                System.out.println("########");
                System.out.println(bmOut);
                System.out.println("########");
                System.out.println(byteArray);


                try {
                    String url = "http://thingtalk.ir/images?key=TYHG1EZEOUOFSYKO&field1="+img_full;
                    String result = new HttpPostRequest().execute(url).get();

                    System.out.println("$$$$$$$$$$");
                    Toast.makeText(getApplicationContext() , "OK" , Toast.LENGTH_LONG).show();
                    System.out.println("$$$$$$$$$$");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }



                //converting bitmap object to show in pre
/*
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent i = new Intent(getApplicationContext() , KrooPreview.class);
                        startActivity(i);
                        i.putExtra("bitmap", bm);
                        finish();

                    }
                });
*/

            }
        });
    }



    private class HttpPostRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result;
            String inputLine;

            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);

                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();

                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();

                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());

                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();

                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }

            return result;
        }

        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }

        //checking connectivity



    }
/*
    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "myImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }
*/
}
