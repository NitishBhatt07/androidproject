package com.bhatt.nitish.speechapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;

public class Readsms extends AppCompatActivity {

    private ToggleButton toggle;
    private CompoundButton.OnCheckedChangeListener toggleListener;

    private TextView smsText;
    private TextView smsSender;

    TextToSpeech toSpeech;

    private BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readsms);


        ///for text to speach.......
        toSpeech = new TextToSpeech(Readsms.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    status = toSpeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(Readsms.this, "feature not support", Toast.LENGTH_LONG).show();
                }
            }
        });
        //text to speach end.......

        toggle = (ToggleButton) findViewById(R.id.speechToggle);
        smsText = (TextView) findViewById(R.id.sms_text);
        smsSender = (TextView) findViewById(R.id.sms_sender);

        toggleListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                if (isChecked) {
                    toSpeech.speak("Okay! Sar, I will read your messages out loud for you now",
                            TextToSpeech.QUEUE_ADD,null);
                } else {
                    toSpeech.speak("Okay! Sar, I will stay silent now",TextToSpeech.QUEUE_ADD,null);
                }
            }
        };
        toggle.setOnCheckedChangeListener(toggleListener);

        initializeSMSReceiver();
        registerSMSReceiver();

    }


    private void initializeSMSReceiver(){
        smsReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    Object[] pdus = (Object[])bundle.get("pdus");
                    for(int i=0;i<pdus.length;i++){
                        byte[] pdu = (byte[])pdus[i];
                        SmsMessage message = SmsMessage.createFromPdu(pdu);
                        String text = message.getDisplayMessageBody();
                        String sender = getContactName(message.getOriginatingAddress());
                        toSpeech.speak("Sir,You have a new message from" + sender,TextToSpeech.QUEUE_ADD,null);
                        toSpeech.speak(text,TextToSpeech.QUEUE_ADD,null);
                        smsSender.setText("Message from " + sender);
                        smsText.setText(text);


                    }
                }

            }
        };
    }

    private String getContactName(String phone){

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        String projection[] = new String[]{ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor.moveToFirst()){
            return cursor.getString(0);

        }else {
            return "unknown number";
        }
    }

    private void registerSMSReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
        toSpeech.stop();
    }

}
