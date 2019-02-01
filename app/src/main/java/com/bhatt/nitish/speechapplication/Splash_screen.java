package com.bhatt.nitish.speechapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.VideoView;

public class Splash_screen extends AppCompatActivity {
    Button skipbtn;

    ///for sliding.........................................
    private GestureDetectorCompat gestureDetectorCompat;
//end sliding.............................................


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////for hiding mobiles notification bar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ////code ends
        setContentView(R.layout.activity_splash_screen);

        //for sliding.....................................................................................
        gestureDetectorCompat = new GestureDetectorCompat(this, new Splash_screen.MyGestureListener());
        //end sliding....................................................................................

        VideoView videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.splesh);
        videoview.setVideoURI(uri);
        videoview.start();
        skipbtn = (Button)findViewById(R.id.skip);

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent skip = new Intent(Splash_screen.this,MainActivity.class);
                startActivity(skip);
            }
        });

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(20000);   // set the duration of splash screen
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(Splash_screen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timer.start();

    }

    ///for sliding......................................................................

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

            if(event2.getX() < event1.getX()){
               /* Toast.makeText(getBaseContext(),
                        "Swipe left - startActivity()",
                        Toast.LENGTH_SHORT).show(); */

                //switch another activity
                Intent intent = new Intent(
                        Splash_screen.this, MainActivity.class);
                startActivity(intent);
            }

            return true;
        }
    }

    //end sliding.................................................................
}


