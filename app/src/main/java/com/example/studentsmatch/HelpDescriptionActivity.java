package com.example.studentsmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public Button whatsappBtn;
    public TextView whatsappNote;

    public void init(){
        nameEt=findViewById(R.id.nameEt);
        subjectEt=findViewById(R.id.subjectEt);
        getHelpEditText=findViewById(R.id.getHelpEditText);
        getImage1=findViewById(R.id.getImage1);
        whatsappBtn=findViewById(R.id.whatsappBtn);
        whatsappNote=findViewById(R.id.whatsappNote);
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
        setUpWhatsApp(request);
    }

    // Lets the helper message the student who asked for help, using the phone number from their profile.
    private void setUpWhatsApp(Request request) {
        String name = request.getRequestname();
        boolean ownRequest = MainActivity.user != null && name.equals(MainActivity.user.getUserName());
        if (ownRequest) {
            whatsappNote.setText("This is your own request");
            whatsappNote.setVisibility(View.VISIBLE);
            return;
        }

        String phone = findPhone(name);
        String international = toInternational(phone);
        if (international == null) {
            whatsappNote.setText("No phone number for " + name);
            whatsappNote.setVisibility(View.VISIBLE);
            return;
        }

        whatsappBtn.setText("Message " + name + " on WhatsApp");
        whatsappBtn.setVisibility(View.VISIBLE);
        whatsappBtn.setOnClickListener(v -> {
            String message = "Hi " + name + ", I saw your request for help in "
                    + request.getRequestsubject() + " on StudentsMatch and I can help.";
            Uri uri = Uri.parse("https://wa.me/" + international + "?text=" + Uri.encode(message));
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String findPhone(String userName) {
        SQLiteDatabase db = new HelperDB(this).getReadableDatabase();
        try (Cursor cursor = db.query(HelperDB.USER_TABLE, new String[]{HelperDB.USER_PHONE},
                HelperDB.USER_NAME + " = ?", new String[]{userName}, null, null, null)) {
            return cursor.moveToFirst() ? cursor.getString(0) : null;
        }
    }

    // Israeli mobile number (0501234567) to WhatsApp format (972501234567).
    static String toInternational(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("972")) return digits.length() == 12 ? digits : null;
        if (digits.startsWith("0") && digits.length() == 10) return "972" + digits.substring(1);
        return null;
    }

}
