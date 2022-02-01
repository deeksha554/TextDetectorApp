package com.example.textdetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


import java.io.IOException;
import java.util.List;

public class CaptureImage extends AppCompatActivity {
    TextView textView;
    AppCompatButton button1;//button2;
    Bitmap bitmap,uri;
    ImageView imageView;
    String[] strings ={"Camera","Gallery","Cancel"};
    AlertDialog.Builder dialog;
    InputImage inputImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        button1 = findViewById(R.id.button1);
        //button2 = findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView2);
        textView = findViewById(R.id.textView2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new AlertDialog.Builder(CaptureImage.this);
                dialog.setTitle("Select Image");
                dialog.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(strings[which].equals("Camera")){
                            if(ActivityCompat.checkSelfPermission(CaptureImage.this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent,0);
                            }else{
                                ActivityCompat.requestPermissions(CaptureImage.this,new String[]{Manifest.permission.CAMERA},200);
                            }
                        }else if(strings[which].equals("Gallery")){
                            if(ActivityCompat.checkSelfPermission(CaptureImage.this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent,1);
                            }else{
                                ActivityCompat.requestPermissions(CaptureImage.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);
                            }
                        }else{
                            dialog.dismiss();
                        }
                    }
                });
                 dialog.show();
              }
        });
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // if()
//                //detectText();
//                //else
//                 detectText1();
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(CaptureImage.this, "Permission Granted....", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(CaptureImage.this, "Permissions Denied....", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode==RESULT_OK && data!=null){
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);
            detectText();
        }else if(requestCode==1 && resultCode==RESULT_OK){
                  if(data!=null){
                  Uri uri1 = data.getData();
                  imageView.setImageURI(uri1);
                      try {
                          uri = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri1);
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
                  detectText1();
        }
    }

    public void detectText1(){
        inputImage = InputImage.fromBitmap(uri,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                List<Text.TextBlock> blocks1 = text.getTextBlocks();
                if(blocks1.size()==0){
//                    Toast.makeText(CaptureImage.this, "No Text....", Toast.LENGTH_SHORT).show();
                    textView.setText("No Text Detected....");
                    return;
                }
                for(Text.TextBlock blocks:text.getTextBlocks()) {
                            String block = blocks.getText();
                            textView.setText(block);
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CaptureImage.this, "Fail to detect text from image....", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void detectText() {
        inputImage = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                List<Text.TextBlock> blocks1 = text.getTextBlocks();
                if(blocks1.size()==0){
                    //Toast.makeText(CaptureImage.this, "No Text....", Toast.LENGTH_SHORT).show();
                    textView.setText("No Text Detected....");
                    return;
                }
                for (Text.TextBlock blocks : text.getTextBlocks()) {
                    String block = blocks.getText();
                    textView.setText(block);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CaptureImage.this, "Fail to detect text from image....", Toast.LENGTH_SHORT).show();
            }
        });
    }
}