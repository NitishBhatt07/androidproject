package com.bhatt.nitish.speechapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener {


    ///for flotinf button.............
    private static final int CODE_PERMISSION = 2084;
    /////////////////end.................

    private ContentResolver cResolver;
    //for flash light...................
    private Camera camera;
    private Camera.Parameters parameters;
    boolean isFlashLightOn = false;
    //flag to detect flash is on or off.................

    int i = 0;

    AnalogClock simpleAnalogClock;

    TextToSpeech textToSpeech;
    Button speakbutton;

    VideoView videoview;

    ////audio manager for increase and ecrease volume.........................
    AudioManager audioManager;
    ////////////////code ends.............................

    /////for chat mess background relativelayout........
    RelativeLayout leftchatbg;
    LinearLayout rightchatbg;
    ///...............end...........

    ///for sliding.........................................
    private GestureDetectorCompat gestureDetectorCompat;
//end sliding.............................................

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private TextView returnedText, Chatbottext;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /////edit text go up when keyboard popup.............
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        /////code end...........................................
        ////for hiding mobiles notification bar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ////code ends
        setContentView(R.layout.activity_main);


        /////for chat mess background relativelayout........
        rightchatbg = (LinearLayout) findViewById(R.id.rightmessbg);
        leftchatbg = (RelativeLayout) findViewById(R.id.leftmessbg);

        rightchatbg.setVisibility(View.INVISIBLE);
        leftchatbg.setVisibility(View.INVISIBLE);
        ///...............end...........


        ////audio manager for increase and ecrease volume.........................
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        ////////////////code ends.............................


//////////////////////////for flash light...........................
        if (isFlashSupported()) {
            camera = Camera.open();
            parameters = camera.getParameters();
        } else {
            showNoFlashAlert();
        }
/////////////////////////////for flash light..............................

        Chatbottext = (TextView) findViewById(R.id.textView2);

        /////for displaying clock...........................
        simpleAnalogClock = (AnalogClock) findViewById(R.id.simpleAnalogClock); // inititate a analog clock
        simpleAnalogClock.setBackgroundColor(Color.BLACK);
        simpleAnalogClock.setVisibility(View.INVISIBLE);
        // simpleAnalogClock.setBackgroundColor(Color.GREEN); // green color for the background of the analog clock


        videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.jarvis);
        videoview.setVideoURI(uri);
        videoview.start();

        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0, 0);
            }
        });


        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.getDefault());

                } else {
                    Toast.makeText(MainActivity.this, "not supported", Toast.LENGTH_LONG).show();
                }
            }
        });

        //for sliding........................................
        gestureDetectorCompat = new GestureDetectorCompat(this, new MainActivity.My2ndGestureListener());
        //end sliding.........................................

        returnedText = (TextView) findViewById(R.id.textView1);
        speakbutton = (Button) findViewById(R.id.speakbutton);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.ENGLISH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        speakbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                i++;
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        i = 0;
                    }
                };
                if (i == 1) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION);
                    textToSpeech.stop();
                    rightchatbg.setVisibility(View.INVISIBLE);
                    leftchatbg.setVisibility(View.INVISIBLE);
                    simpleAnalogClock.setVisibility(View.INVISIBLE);
                    handler.postDelayed(r, 250);

                } else if (i == 2) {
                    //Double click
                    Intent main = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(main);
                }
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
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onBackPressed() {
        Intent out = new Intent(Intent.ACTION_MAIN);
        out.addCategory(Intent.CATEGORY_HOME);
        out.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(out);
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
        textToSpeech.speak(errorMessage, TextToSpeech.QUEUE_ADD, null);
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
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        returnedText.setText(matches.get(0));
        String input = returnedText.getText().toString();
        rightchatbg.setVisibility(View.VISIBLE);
        returnedText.setMovementMethod(new ScrollingMovementMethod());
        RetrieveFeedTask task = new RetrieveFeedTask();
        task.execute(input);

        switch (input) {

            case "gallery":
            case "Gallery":
            case "open gallery":
            case "jarvis open gallery":
            case "Jarvis open gallery":
            case "jarvis gallery":
            case "Jarvis gallery":
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(gallery);
                break;

            case "facebook":
            case "Facebook":
            case "open facebook":
            case "open Facebook":
            case "jarvis open facebook":
            case "jarvis facebook":
            case "Jarvis open facebook":
            case "Jarvis open Facebook":
            case "Jarvis facebook":
            case "Jarvis Facebook":
                Intent fb = getPackageManager().getLaunchIntentForPackage("com.facebook.lite");
                startActivity(fb);
                break;

            case "camera":
            case "Camera":
            case "open camera":
            case "jarvis open camera":
            case "jarvis camera":
            case "Jarvis open camera":
            case "Jarvis camera":
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(cam);
                break;

            case "open whatsapp":
            case "whatsapp":
            case "Whatsapp":
            case "WhatsApp":
            case "jarvis open whatsapp":
            case "jarvis open WhatsApp":
            case "Jarvis open WhatsApp":
            case "open WhatsApp":
            case "jarvis whatsapp":
            case "Jarvis open whatsapp":
            case "Jarvis open Whatsapp":
            case "Jarvis whatsapp":
            case "Jarvis Whatsapp":
                Intent wtsapp = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                startActivity(wtsapp);
                break;

            case "open what's up":
            case "what's up":
            case "jarvis open what's up":
            case "jarvis what's up":
            case "Jarvis open what's up":
            case "Jarvis what's up":
                Intent wtsaup = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                startActivity(wtsaup);
                break;

            case "open YouTube":
            case "YouTube":
            case "Jarvis open YouTube":
            case "Jarvis YouTube":
            case "jarvis open YouTube":
            case "jarvis YouTube":
                Intent youtube = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                startActivity(youtube);
                break;

            case "text recognition":
            case "open text recognition":
            case "text recognition activity":
            case "image to text":
            case "text activity":
            case "open text activity":
            case "jarvis text recognition":
            case "jarvis open text recognition":
            case "jarvis open text activity":
            case "jarvis open text window":
            case "Jarvis text recognition":
            case "Jarvis open text recognition":
            case "Jarvis open text activity":
            case "Jarvis open text window":
            case "Jarvis text window":
                Intent TR = new Intent(MainActivity.this, Textrecoginition.class);
                startActivity(TR);
                break;

            case "Google activity":
            case "Google":
            case "google":
            case "open google activity":
            case "open Google activity":
            case "jarvis open google window":
            case "jarvis open Google window":
            case "jarvis google window":
            case "jarvis Google window":
            case "jarvis google":
            case "Jarvis google activity":
            case "Jarvis open google activity":
            case "Jarvis open Google activity":
            case "Jarvis open google window":
            case "Jarvis open Google window":
            case "Jarvis google window":
            case "Jarvis Google window":
            case "Jarvis google":
                Intent googleActivity = new Intent(MainActivity.this, GoogleActivity.class);
                startActivity(googleActivity);
                break;

            case "Call activity":
            case "call activity":
            case "open call activity":
            case "Call":
            case "call":
            case "jarvis open call activity":
            case "jarvis open call window":
            case "jarvis call window":
            case "jarvis open call":
            case "Jarvis open call activity":
            case "Jarvis open call window":
            case "Jarvis call window":
            case "Jarvis open call":
                Intent callactivity = new Intent(MainActivity.this, ContactList.class);
                startActivity(callactivity);
                break;


            case "file manager":
            case "open file manager":
            case "jarvis open file manager":
            case "jarvis file manager":
            case "Jarvis open file manager":
            case "Jarvis file manager":
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 7);
                break;

            case "what is time":
            case "time":
            case "Time":
            case "date":
            case "what is date":
            case "jarvis what is time":
            case "Jarvis what is time":
            case "Jarvis what is date":
            case "jarvis what is date":
                /////for displaying clock...........................
                simpleAnalogClock.setVisibility(View.VISIBLE);
                // green color for the background of the analog clock
                Calendar calander = Calendar.getInstance();
                SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                String Date = date.format(calander.getTime());
                String Time = time.format(calander.getTime());
                returnedText.setText("Time = " + Time + "\n" + "Date = " + Date);
                textToSpeech.speak("sir the time is " + Time + "\n\n" + "and date is\n " + Date, TextToSpeech.QUEUE_FLUSH, null);
                break;


            case "message":
            case "Message":
            case "message window":
            case "open message window":
            case "jarvis message window":
            case "jarvis open message window":
            case "Jarvis message window":
            case "Jarvis open message window":
                Intent mess = new Intent(MainActivity.this, Readsms.class);
                startActivity(mess);
                break;

            case "alarm":
            case "alarm activity":
            case "open alarm activity":
            case "Jarvis open alarm activity":
            case "Jarvis alarm activity":
                Intent alrm = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(alrm);
                break;

            case "notification":
            case "enable notification":
            case "jarvis enable notification":
            case "Jarvis enable notification":
                //for enable notification.....
                sendNotification();
                ///////////////////////...
                /////for exiting after notification on............
                Intent exit = new Intent(Intent.ACTION_MAIN);
                exit.addCategory(Intent.CATEGORY_HOME);
                exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(exit);
                //////////for exiting.......................
                break;

            case "music":
            case "music activity":
            case "Jarvis open music activity":
            case "open music activity":
                Intent music = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(music);
                break;

            case "mobile number":

                break;

            case "turn off flash":
            case "open flash":
            case "Jarvis open flash light":
            case"open flash light":
            case "Jarvis turn off flash":
            case "Jarvis turn off flash light":
            case "jarvis turn off flash":
            case"flash off":
            case"Jarvis flash off":
                if (isFlashLightOn) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    isFlashLightOn = false;
                }
                break;

            case "turn on flash":
            case "close flash":
            case "Jarvis close flash light":
            case"close flash light":
            case "Jarvis turn on flash":
            case "Jarvis turn on flash light":
            case "jarvis turn on flash":
            case"flash on":
            case"Jarvis flash on":
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();
                isFlashLightOn = true;
                break;

            case "open text to speech":
            case"text to speech":
            case"text to speech activity":
            case "Jarvis open text to speech activity":
            case"Jarvis open text to speech":
                Intent tts = new Intent(MainActivity.this,TTs.class);
                startActivity(tts);
                break;

            case"chat head":
            case"make chat head":
            case"create chat head":
            case"jarvis create chat head":
            case"open chat head":
            case"Jarvis open chathead":
            case"Jarvis make chat head":
            case"jarvis make chat head":
               startService(new Intent(MainActivity.this, FloatingService.class));
                finish();
                Intent thenexit = new Intent(Intent.ACTION_MAIN);
                thenexit.addCategory(Intent.CATEGORY_HOME);
                thenexit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(thenexit);
                break;

            case "exit":
            case "close":
            case "close application":
            case "jarvis close application":
            case "Jarvis exit application":
            case "Jarvis close application":
                Intent close = new Intent(Intent.ACTION_MAIN);
                close.addCategory(Intent.CATEGORY_HOME);
                close.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(close);
                break;

            case "Jarvis show me commands":
            case "show me commands":
            case "Jarvis show me command":
            case "commands":
            case "what can you do":
            case "Jarvis what can you do":
            case "which work can you do":
            case "Jarvis command list":
            case "command list":
            case "command":
            case "Jarvis show me command list":
            case "jarvis commands":
            case "jarvis command":
            case "Jarvis commands":
            case "Jarvis command":
            case "help":
            case "what you can do for me":
            case "What is commands" :
            case "help me jarvis":
            case "help me Jarvis":
                String command = getString(R.string.commands);
                returnedText.setText(command);
                break;

        }

    }

    //////////////////////this is for flash light..............................
    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
    private void showNoFlashAlert() {
        new AlertDialog.Builder(this)
                .setMessage("Your device hardware does not support flashlight!")
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle("Error")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }
///////////////////////flash light code.........................

    ///////////////for opening file manager................
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {

            case 7:

                if (resultCode == RESULT_OK) {

                    String PathHolder = data.getData().getPath();

                    Toast.makeText(MainActivity.this, PathHolder, Toast.LENGTH_LONG).show();

                }
                break;

        }
    }
    ///////////////for opening file manager................


    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    //////////////this is for enabling notification.................
    public void sendNotification() {

        final Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
        builder.setContentTitle("Tap to Open");
        builder.setContentText("Mainactivity");
        builder.setSmallIcon(R.drawable.mike);
        builder.setTicker("JARVIS IS OPENING");
        builder.setContentIntent(pIntent);
        builder.setAutoCancel(false);
        android.app.Notification n = builder.build();
        NotificationManager manager = (NotificationManager) MainActivity.this.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, n);

    }
//////////for enabling notification...................


    /////for sliding.......................................................................
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class My2ndGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe right' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

         /*
         Toast.makeText(getBaseContext(),
          event1.toString() + "\n\n" +event2.toString(),
          Toast.LENGTH_SHORT).show();
         */

            if (event2.getX() > event1.getX()) {
                /*Toast.makeText(getBaseContext(),
                        "Swipe right - finish()",
                        Toast.LENGTH_SHORT).show();*/

                //switch another activity
                Intent intent = new Intent(
                        MainActivity.this, Splash_screen.class);
                startActivity(intent);
            }

            return true;
        }
    }
        ///end sliding.....................................................................

        ////////////diglog box on exiting code..........
        public void Exit(View view) {
            android.app.AlertDialog.Builder exit = new android.app.AlertDialog.Builder(this);
            exit.setTitle("Exit");
            exit.setMessage("SURE ! YOU WANT TO EXIT ");
            exit.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            exit.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            exit.show();
        }
//////dilog box on exiting code................


        /////////////this is what i want this is dilogflow api.ai......................

    // Create GetText Metod
    public String GetText(String query) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://api.dialogflow.com/v1/query?v=20150910");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            /////these are some imp information............
            conn.setRequestProperty("Authorization", "Bearer f95d6a21cdea4399bb6a0579f93a60a4");
            conn.setRequestProperty("Content-Type", "application/json");
            //with my accesss token.........................

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);
//            jsonParam.put("name", "order a medium pizza");
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonParam.toString());
            wr.flush();
            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();


            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
//            if (object.has("fulfillment")) {
            fulfillment = object.getJSONObject("fulfillment");
//                if (fulfillment.has("speech")) {
            speech = fulfillment.optString("speech");
//                }
//            }
            return speech;

        } catch (Exception ex) {
            Log.d("karma", "exception at last " + ex);
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }

        return null;
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try {

                s = GetText(voids[0]);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Chatbottext.setText(s);
            leftchatbg.setVisibility(View.VISIBLE);
            String outputvoice = Chatbottext.getText().toString();
            Chatbottext.setMovementMethod(new ScrollingMovementMethod());
            textToSpeech.speak(outputvoice,TextToSpeech.QUEUE_ADD,null);

        }
    }

    ////////////this is api.ai from dilogflow
}