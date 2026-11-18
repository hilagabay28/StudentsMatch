package com.example.studentsmatch;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserActivity extends AppCompatActivity {
    public EditText username, useremail, useryear, userphone, userpassword;
    public ExtendedFloatingActionButton saveBtn, deleteBtn;
    public ActivityResultLauncher<Uri> arlBig;
    public HelperDB helperDB;
    public SQLiteDatabase db;
    public CircleImageView photoView;
    public Uri imageUri;
    public String currPath;      // picture that will be saved
    public String pendingPath;   // file the camera is writing to right now
    public User user;
    public void init() {
        saveBtn = findViewById(R.id.editBtn);
        username = findViewById(R.id.username);
        useremail = findViewById(R.id.useremail);
        useryear = findViewById(R.id.useryear);
        userphone = findViewById(R.id.userphone);
        userpassword = findViewById(R.id.userpassword);
        helperDB=new HelperDB(EditUserActivity.this);
        photoView = findViewById(R.id.photoView);
        deleteBtn=findViewById(R.id.deleteBtn);
    }

    public void getPicFromFile(String filename) {
        Log.d("StudentsMatch", "get pic: " + filename);
        // Keep the current picture unless the user takes a new one.
        currPath = filename;
        Bitmap image = ProfilePic.load(filename);
        if (image != null) {
            photoView.setImageBitmap(image);
        } else {
            Log.d("StudentsMatch", "no profile pic to show: " + filename);
        }
    }

    public boolean isAllOk(User user) {
      Log.d("StudentsMatch","user is:"+user);
        String name = user.getUserName();
        String email = user.getUserEmail();
        String year = user.getUserYear();
        String phone = user.getUserPhone();
        String password = user.getUserPass();
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
        // An empty password here means "keep my current password".

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
        if (!password.isEmpty() && password.length() < 6) {
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
        ActivityCompat.requestPermissions(EditUserActivity.this, permissions, 100);
    }

    public void listenerForPic() {
        arlBig = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        try {
                            Bitmap image = result ? ProfilePic.load(pendingPath) : null;
                            if (image != null) {
                                currPath = pendingPath;//this picture will be saved
                                photoView.setImageBitmap(image);//display pic taken
                            } else {
                                ProfilePic.deleteIfEmpty(pendingPath);//camera cancelled
                            }
                        } catch (Exception e) {
                            Toast.makeText(EditUserActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }//onActivityResult
                }//registerForActivityResult
        );//registerForActivityResult
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edituser);
        init();
        ask4Permissions();
        listenerForPic();

        user = (User) getIntent().getSerializableExtra("user");
        loadUserData(user.getUserName());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User test=new User();
                test.setUserName(username.getText().toString());
                test.setUserEmail(useremail.getText().toString());
                test.setUserYear(useryear.getText().toString());
                test.setUserPhone(userphone.getText().toString());
                test.setUserPass(userpassword.getText().toString());
                Log.d("StudentsMatch","current path:"+currPath);
                test.setPic(currPath);
                if (isAllOk(test)) {
                    Log.d("StudentsMatch", "Save: "+user.getUserName());
                    String oldName = user.getUserName();//the row to update is found by the old name
                    user.setUserName(test.getUserName());
                    user.setUserEmail(test.getUserEmail());
                    user.setUserYear(test.getUserYear());
                    user.setUserPhone(test.getUserPhone());
                    if (!test.getUserPass().isEmpty()) {
                        user.setUserPass(PasswordHasher.hash(test.getUserPass()));//new password
                    }
                    user.setPic(currPath);//save the (new) profile picture too
                    updateUser(oldName, user);

                    Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                    intent.putExtra("user", user)  ;
                    startActivity(intent);
                }

            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=user.getUserName();
                deleteUser(username);
                Intent it=new Intent(EditUserActivity.this, StartActivity.class);
                startActivity(it);
            }
        });

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageUri = createUri();
                    // Note: imageUri.getPath() is NOT a real file path (it starts with
                    // /picker_get_content/...), so the real path is kept in pendingPath.
                    Log.d("StudentsMatch", "path:" + pendingPath);
                    arlBig.launch(imageUri);

                } catch (IOException e) {
                    Toast.makeText(EditUserActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Uri createUri() throws IOException {
        String fileName = getFileName();
        Log.d("StudentsMatch", "fileName" + fileName);
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            File img = new File(storageDir, fileName + ".jpg");
            img.createNewFile();
            pendingPath = img.getAbsolutePath();
            Log.d("StudentsMatch", "Save current path " + pendingPath);
            return FileProvider.getUriForFile(getApplicationContext(),
                    "com.example.studentsmatch.fileProvider",
                    img);
        } catch (IOException e) {
            Log.d("StudentsMatch", "could not create file " + fileName);
            Toast.makeText(EditUserActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
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

    public void deleteUser(String username)
    {
        Log.d("StudentsMatch","Delete "+username);
        SQLiteDatabase db=helperDB.getWritableDatabase();
        String where=helperDB.USER_NAME+"=?";
        String [] args=new String [] {username};
        int rows=db.delete(helperDB.USER_TABLE,where, args);
        db.close();
        if (rows==0)
        {
            Log.d("StudentsMatch","Could not find user "+username+" to be deleted");
        }
        else if (rows==1)
        {
            Log.d("StudentsMatch","Deleted user "+username);
        }
        else {
            Log.d("StudentsMatch","WARNING! More than one user "+username+" deleted");
        }
    }

    public void updateUser(String oldName, User user) {
        Log.d("StudentsMatch","update user "+user.getUserName());
        db = helperDB.getWritableDatabase();

        String[] oldData = {oldName};
        String infield = helperDB.USER_NAME + "=?";
        ContentValues cv = new ContentValues();

        cv.put(helperDB.USER_NAME, user.getUserName());
        cv.put(helperDB.USER_EMAIL, user.getUserEmail());
        cv.put(helperDB.USER_YEAR, user.getUserYear());
        cv.put(helperDB.USER_PASS, user.getUserPass());
        cv.put(helperDB.USER_PHONE, user.getUserPhone());
        cv.put(helperDB.USER_PIC, user.getPic());

        int rowsChanged = db.update(helperDB.USER_TABLE, cv, infield, oldData);
        if (rowsChanged > 0) {
            Toast.makeText(EditUserActivity.this, "changes saved", Toast.LENGTH_SHORT).show();
            loadUserData(user.getUserName());
        }
        else {
        }
    }

    public void loadUserData(String myuser)
    {
        Log.d("StudentsMatch","load:"+myuser);
        String[] projection = {
                helperDB.USER_NAME,
                helperDB.USER_EMAIL,
                helperDB.USER_YEAR,
                helperDB.USER_PASS,
                helperDB.USER_PHONE,
                helperDB.USER_PIC

        };
        String selection = helperDB.USER_NAME + " = ?";
        String[] selectionArgs =  new String [] {myuser};
        SQLiteDatabase db = helperDB.getReadableDatabase();
        Cursor cursor = db.query(
                helperDB.USER_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
        Log.d("StudentsMatch","load count:"+cursor.getCount());
        cursor.moveToFirst();

        String column=helperDB.USER_NAME;
        int index = cursor.getColumnIndexOrThrow(column);
        String value= cursor.getString(index);
        Log.d("StudentsMatch","user name:"+value);
        username.setText(value);

        column=helperDB.USER_EMAIL;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
        useremail.setText(value);

        column=helperDB.USER_YEAR;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
        useryear.setText(value);

        column=helperDB.USER_PHONE;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
        userphone.setText(value);

        column=helperDB.USER_PASS;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
        // The stored value is a hash, so never show it. Leave empty = keep current password.
        userpassword.setText("");
        userpassword.setHint("new password (optional)");

        column=helperDB.USER_PIC;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
        getPicFromFile(value);
    }

}