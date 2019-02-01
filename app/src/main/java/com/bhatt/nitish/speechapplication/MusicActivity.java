package com.bhatt.nitish.speechapplication;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.bhatt.nitish.speechapplication.MainActivity.getErrorText;

public class MusicActivity extends AppCompatActivity implements RecognitionListener {

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private TextView returnedText;
    Button btnspeak,volumeupbtn,volumedownbtn,musicbtn,infobtn;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    TextToSpeech textToSpeech;

    MediaPlayer mp;

    VideoView videoview;

    ////audio manager for increase and ecrease volume.........................
    AudioManager audioManager;
    ////////////////code ends............................


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////for hiding mobiles notification bar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ////code ends
        setContentView(R.layout.activity_music);

        ////audio manager for increase and ecrease volume.........................
       audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        ////////////////code ends.............................


        videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.musicbg);
        videoview.setVideoURI(uri);

        videoview.start();

        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer vid) {
                vid.setLooping(true);
                vid.setVolume(0,0);
            }
        });


        textToSpeech = new TextToSpeech(MusicActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.getDefault());

                } else {
                    Toast.makeText(MusicActivity.this, "not supported", Toast.LENGTH_LONG).show();
                }
            }
        });


        musicbtn = (Button)findViewById(R.id.musicbtn);
        infobtn = (Button)findViewById(R.id.infobtn);
        volumedownbtn = (Button)findViewById(R.id.volumedownbtn);
        volumeupbtn = (Button)findViewById(R.id.volumeupbtn);


        musicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()){
                    ////to set new image............
                    musicbtn.setBackgroundResource(R.drawable.musicpausebtn);
                    ///end..........................
                    mp.pause();
                }else {
                    ////to set new image............
                    musicbtn.setBackgroundResource(R.drawable.musicresumebtn);
                    ///end..........................
                    mp.start();
                }
            }
        });

        volumeupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
            }
        });

        volumedownbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            }
        });

        infobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ////for showing commands ...........
                textToSpeech.speak("Sir,these are instraction to operate me",TextToSpeech.QUEUE_ADD,null);
                String musiccommand = getString(R.string.MusicActivityInstraction);
                returnedText.setMovementMethod(new ScrollingMovementMethod());
                returnedText.setText(musiccommand);
                //////end command.............................
            }
        });



        returnedText = (TextView) findViewById(R.id.textView1);
        btnspeak = (Button) findViewById(R.id.speakbutton);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);


        btnspeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions
                        (MusicActivity.this,
                                new String[]{android.Manifest.permission.RECORD_AUDIO},
                                REQUEST_RECORD_PERMISSION);

            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MusicActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        Intent musicactivity = new Intent(MusicActivity.this,MusicActivity.class);
        startActivity(musicactivity);
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }


    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }


    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }


    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        //toggleButton.setChecked(false);
    }

    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }


    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }


    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        returnedText.setText(matches.get(0));
        String input = returnedText.getText().toString();

        switch(input) {

            case "back":
            case "back to main activity":
            case "main window":
                textToSpeech.speak("welcome back to main activity", TextToSpeech.QUEUE_ADD, null);
                Intent back = new Intent(MusicActivity.this, MainActivity.class);
                startActivity(back);
                break;


        }

        if (input.equals(input)){

            mp = new MediaPlayer();
            try {
                mp.setDataSource("sdcard/Music/"+input+".mp3");

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();

        }

    }
}
