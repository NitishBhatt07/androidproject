package com.bhatt.nitish.speechapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Textrecoginition extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    Button gallerybtn,btnback,btnnext;
    TextToSpeech toSpeech;
    int result;
    String inputtext;


    private Bitmap bitmap,cambitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////for hiding mobiles notification bar
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ////code ends
        setContentView(R.layout.activity_textrecoginition);

        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        gallerybtn = (Button) findViewById(R.id.gallerybtn);
        btnback  =(Button)findViewById(R.id.btnback);
        btnnext  =(Button)findViewById(R.id.btnnext);



        ///text to speceh...........
        toSpeech = new TextToSpeech(Textrecoginition.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    result = toSpeech.setLanguage(Locale.getDefault());
                } else {
                    Toast.makeText(Textrecoginition.this, "feature not support", Toast.LENGTH_LONG).show();
                }
            }
        });
        ////text to speach ends...........

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(Textrecoginition.this,MainActivity.class);
                startActivity(back);
            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager pm = getPackageManager();
                    int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
                    if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                        final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                        android.support.v7.app.AlertDialog.Builder builder =
                                new android.support.v7.app.AlertDialog.Builder(Textrecoginition.this);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, PICK_IMAGE_CAMERA);
                                } else if (options[item].equals("Choose From Gallery")) {
                                    dialog.dismiss();
                                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                                } else if (options[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    } else
                        Toast.makeText(getApplicationContext(), "Camera Permission error", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Camera Permission error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inputStreamImg = null;
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                cambitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                cambitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                imageView.setImageBitmap(cambitmap);

                ////this is for recognize text from image by button click....

                btnnext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Textrecoginition.this,Textspeak.class);


                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                        if (textRecognizer.isOperational()) {

                            Frame frame = new Frame.Builder().setBitmap(cambitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i < items.size(); ++i) {
                                TextBlock myitems = items.valueAt(i);
                                sb.append(myitems.getValue());
                                sb.append("\n");
                            }
                            textView.setText(sb.toString());
                            textView.setMovementMethod(new ScrollingMovementMethod());
                            inputtext = textView.getText().toString();
                            textView.setVisibility(View.INVISIBLE);


                            intent.putExtra("value2",inputtext);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Textrecoginition.this, "not supported", Toast.LENGTH_LONG).show();
                        }
                    }

                });
                ////hear code end for recognize text..........
            } catch (Exception e) {
                e.printStackTrace();
            }
            /////////code end/..........


        } else if (requestCode == PICK_IMAGE_GALLERY) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                imageView.setImageBitmap(bitmap);

                ////this is for recognize text from image by button click
                btnnext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Textrecoginition.this,Textspeak.class);

                        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                        if (textRecognizer.isOperational()) {

                            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> items = textRecognizer.detect(frame);
                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i < items.size(); ++i) {
                                TextBlock myitems = items.valueAt(i);
                                sb.append(myitems.getValue());
                                sb.append("\n");
                            }
                            textView.setText(sb.toString());
                            textView.setMovementMethod(new ScrollingMovementMethod());
                            inputtext = textView.getText().toString();

                            intent.putExtra("value2",inputtext);
                            startActivity(intent);


                        } else {
                            Toast.makeText(Textrecoginition.this, "not supported", Toast.LENGTH_LONG).show();
                        }
                    }

                });
                ////hear code end for recognize text...........

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Textrecoginition.this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(Textrecoginition.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
