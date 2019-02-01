package com.bhatt.nitish.speechapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static com.bhatt.nitish.speechapplication.MainActivity.getErrorText;

public class Textspeak extends AppCompatActivity {

    EditText editText;
    Button googlebtn,speakbtn,backbtn,exitbtn;

    TextToSpeech toSpeech;
    int result;
    String speaktext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////for hiding mobiles notification bar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ////code ends
        setContentView(R.layout.activity_textspeak);

        speakbtn = (Button)findViewById(R.id.speakbtn);
        googlebtn = (Button)findViewById(R.id.googlebtn);
        backbtn = (Button)findViewById(R.id.backbtn);
        exitbtn = (Button)findViewById(R.id.exitbtn);

        ///for text to speach.......
        toSpeech = new TextToSpeech(Textspeak.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = toSpeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(Textspeak.this, "feature not support", Toast.LENGTH_LONG).show();
                }
            }
        });
        //text to speach end.......


        editText = (EditText)findViewById(R.id.etspeak);
        editText.setMovementMethod(new ScrollingMovementMethod());

        ////for google search for text.......
        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech.speak("sir ,searching on google",TextToSpeech.QUEUE_ADD,null);
                String option = editText.getText().toString();
                Intent find = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/search?q=" + option));
                startActivity(find);

            }
        });
        //end google search.......

        ///this is for getting text from previous class/........
        String value1 = super.getIntent().getExtras().getString("value2");
        editText.setText(value1);
        speaktext = editText.getText().toString();
        ////code end

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech.speak("going back to main activity",TextToSpeech.QUEUE_ADD,null);
                Intent back = new Intent(Textspeak.this,Textrecoginition.class);
                startActivity(back);
            }
        });

        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech.stop();
                toSpeech.speak("going back to main activity",TextToSpeech.QUEUE_ADD,null);
                Intent pre = new Intent(Textspeak.this,MainActivity.class);
                startActivity(pre);
            }
        });


        ///for speacking text
        speakbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSpeech.speak(speaktext, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        /////end for speaking text

    }

    public void customsearch(View view){
        toSpeech.speak("sir,searching on google",TextToSpeech.QUEUE_ADD,null);
        Intent search = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/search?q=" + speaktext));
        startActivity(search);

    }

}
