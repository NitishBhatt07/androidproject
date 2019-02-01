package com.bhatt.nitish.speechapplication;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import static com.bhatt.nitish.speechapplication.MainActivity.getErrorText;

public class LoginActivity extends AppCompatActivity {

    TextToSpeech textToSpeech;

    Button send;
    int n=0;
    private EditText returnedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textToSpeech = new TextToSpeech(LoginActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.getDefault());

                } else {
                    Toast.makeText(LoginActivity.this, "not supported", Toast.LENGTH_LONG).show();
                }
            }
        });

        returnedText = (EditText) findViewById(R.id.edittext);
        send = (Button) findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             if (returnedText.getText().toString().equals("Nitish")){
                 textToSpeech.speak("welcome sir",TextToSpeech.QUEUE_ADD,null);
                 Intent main = new Intent(LoginActivity.this,MainActivity.class);
                 startActivity(main);
             }else{
                 textToSpeech.speak("try again",TextToSpeech.QUEUE_ADD,null);
                 returnedText.setVisibility(View.VISIBLE);
                 returnedText.setBackgroundColor(Color.RED);
                 n --;
                 returnedText.setText(Integer.toString(n));

                 if (n == 0){
                     textToSpeech.speak("Limit end,try after some time",TextToSpeech.QUEUE_ADD,null);
                     send.setEnabled(false);
                 }
             }


            }
        });

    }
}