package com.example.studentsmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelpDescriptionActivity extends AppCompatActivity {
    public EditText nameEt;
    public EditText subjectEt, getHelpEditText;
    public ImageView getImage1;

    public void init(){
        nameEt=findViewById(R.id.nameEt);
        subjectEt=findViewById(R.id.subjectEt);
        getHelpEditText=findViewById(R.id.getHelpEditText);
        getImage1=findViewById(R.id.getImage1);
    }

//
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_description);

        Request request=(Request) getIntent().getSerializableExtra("request");
        init();

        nameEt.setText(request.getRequestname());
        subjectEt.setText(request.getRequestsubject());
        getHelpEditText.setText(request.getRequestdescription());
        String convertedStringFromUri = request.getRequestimage();
        if (convertedStringFromUri != null && !convertedStringFromUri.isEmpty()) {//image is optional
            Uri toUri = Uri.parse(convertedStringFromUri);
            try {
                int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(toUri, flag);
                getImage1.setImageURI(toUri);
            } catch (SecurityException e) {
                Log.d("StudentsMatch", "no access to request image: " + e);
            }
        }
    }

}
