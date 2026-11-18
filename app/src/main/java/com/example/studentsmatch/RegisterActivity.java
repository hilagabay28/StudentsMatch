package com.example.studentsmatch;

import static com.google.android.material.datepicker.MaterialDatePicker.Builder;
import static com.google.android.material.datepicker.MaterialDatePicker.todayInUtcMilliseconds;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    public EditText username, useremail, useryear, userphone, userpassword;
    public CircleImageView imageView;
    public ExtendedFloatingActionButton regbuttom;
    public ActivityResultLauncher<Uri> arlBig;
    public Uri imageUri;
    public String currPath;      // picture that will be saved
    public String pendingPath;   // file the camera is writing to right now
    HelperDB helperDB;
    SQLiteDatabase db;

    public boolean isAllOk(User user) {
        String name=user.getUserName();
        String email=user.getUserEmail();
        String year=user.getUserYear();
        String phone=user.getUserPhone();
        String password=user.getUserPass();
        if (name.isEmpty()) {
            username.setError("Missing user name");
            return false; // At least one field is empty
        }
        if (email.isEmpty()) {
            useremail.setError("Missing email");
            return false; // At least one field is empty
        }
        if (phone.isEmpty()) {
            userphone.setError("Missing phone number");
            return false; // At least one field is empty
        }
        if (year.isEmpty()) {
            useryear.setError("Missing birth date");
            return false; // At least one field is empty
        }
        if (password.isEmpty()) {
            userpassword.setError("Missing password");
            return false; // At least one field is empty
        }

        // Validate email format using a simple regex pattern
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        if (!email.matches(emailPattern)) {

            useremail.setError("Wrong email format");
            return false; // Invalid email format
        }
        // Validate phone number format (you can customize the format validation as needed)
        String phonePattern = "^[0-9]{10}$";
        if (!phone.matches(phonePattern)) {
            userphone.setError("Wrong phone number, please enter 10 digits");
            return false; // Invalid phone number format (assuming a 10-digit format)
        }
        // Add more password validation rules as needed
        if (password.length() < 6) {
            userpassword.setError("Password is too short (at least 6 characters)");
            return false; // Password is too short
        }
        // If all validations pass, return true to indicate that user properties are valid
        return true;
    }

    public void ask4Permissions() {
        String[] permissions = new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(RegisterActivity.this, permissions, 100);
    }

    public void init() {
        regbuttom = findViewById(R.id.regbuttom);
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.useremail);
        useryear = findViewById(R.id.useryear);
        userphone = findViewById(R.id.userphone);
        userpassword = findViewById(R.id.userpassword);
        imageView = findViewById(R.id.imageView);
        currPath="";
    }

    public void listenerForPic() {
        arlBig = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        try {
                            android.graphics.Bitmap image = result ? ProfilePic.load(pendingPath) : null;
                            if (image != null) {
                                currPath = pendingPath;//this picture will be saved
                                imageView.setImageBitmap(image);//display pic taken
                            } else {
                                ProfilePic.deleteIfEmpty(pendingPath);//camera cancelled
                            }
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }//onActivityResult
                }//registerForActivityResult
        );//registerForActivityResult
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        helperDB = new HelperDB(RegisterActivity.this);
        init();
        ask4Permissions();
        listenerForPic();

        useryear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Pick a date")
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build();

                    materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date(selection));
                        useryear.setText(date);
                    });

                    materialDatePicker.show(getSupportFragmentManager(), "data-picker");
                }
            }
        });

        regbuttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(username.getText().toString(), useremail.getText().toString(), useryear.getText().toString(), userphone.getText().toString(), userpassword.getText().toString(), currPath);
                if (isAllOk(user) && !verifyExists(username.getText().toString())) {
                    ContentValues cv = new ContentValues();
                    cv.put(HelperDB.USER_NAME, user.getUserName());
                    cv.put(HelperDB.USER_EMAIL, user.getUserEmail());
                    cv.put(HelperDB.USER_YEAR, user.getUserYear());
                    cv.put(HelperDB.USER_PHONE, user.getUserPhone());
                    user.setUserPass(PasswordHasher.hash(user.getUserPass()));//never store the plain password
                    cv.put(HelperDB.USER_PASS, user.getUserPass());
                    cv.put(HelperDB.USER_PIC, user.getPic());
                    db = helperDB.getWritableDatabase();
                    db.insert(HelperDB.USER_TABLE, null, cv);
                    db.close();

                    Intent intent = new Intent(RegisterActivity.this, RateActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageUri = createUri();
                    String path = imageUri.getPath();//go and save in dbs!
                    Log.d("StudentsMatch", "path:" + path);
                    arlBig.launch(imageUri);

                } catch (IOException e) {
                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public String getFileName() {
        int sel_year, sel_month, sel_day, sel_hour, sel_minute, sel_sec;

        Calendar calendar = Calendar.getInstance();
        sel_year = calendar.get(Calendar.YEAR);
        sel_month = calendar.get(Calendar.MONTH) + 1;
        sel_day = calendar.get(Calendar.DAY_OF_MONTH);
        sel_hour = calendar.get(Calendar.HOUR);
        sel_minute = calendar.get(Calendar.MINUTE);
        sel_sec = calendar.get(Calendar.SECOND);

        String fileName = "Mypic_" + sel_year + sel_month + sel_day +
                "-" + sel_hour + "-" + sel_minute + "-" + sel_sec;
        Log.d("StudentsMatch", "filename: " + fileName);
        return fileName;

    }

    private Uri createUri() throws IOException {
        String fileName = getFileName();
        Log.d("StudentsMatch", "fileName" + fileName);
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            File img = new File(storageDir, fileName + ".jpg");
            img.createNewFile();
            pendingPath = img.getAbsolutePath();

            Log.d("StudentsMatch", "Save " + pendingPath);
            return FileProvider.getUriForFile(getApplicationContext(),
                    "com.example.studentsmatch.fileProvider",
                    img);
        } catch (IOException e) {
            Log.d("StudentsMatch", "could not create file " + fileName);
            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public boolean validateUserProperties(String firstName, String lastName, String email, String phone, String password) {
        useremail.setTextColor(Color.parseColor("#FFFFFF"));
        userphone.setTextColor(Color.parseColor("#FFFFFF"));
        userpassword.setTextColor(Color.parseColor("#FFFFFF"));
        return false;
    }

    private boolean verifyExists(String currusername) {
        SQLiteDatabase db = helperDB.getReadableDatabase();

        String[] projection = {HelperDB.USER_NAME};
        String selection = HelperDB.USER_NAME + " = ?";
        String[] selectionArgs = new String[]{currusername};

        Cursor cursor = db.query(HelperDB.USER_TABLE, projection, selection, selectionArgs, null, null, null);

        boolean result = cursor.moveToFirst();

        if(!result){
        }
        else{
            username.setError("user by that name is already exist ");
        }
        cursor.close();
        db.close();

        return result;
    }

}

