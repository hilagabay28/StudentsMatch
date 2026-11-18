package com.example.studentsmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    public Button gotoReg;
    public Button loginbutton;
    public EditText etUsername;
    public EditText etPassword;
    HelperDB helperDB;

    SQLiteDatabase db;
    public void init(){

        gotoReg= findViewById(R.id.gotoReg);
        loginbutton=findViewById(R.id.loginbutton);
        helperDB=new HelperDB(LoginActivity.this);
        etUsername= findViewById(R.id.etUsername);
        etPassword= findViewById(R.id.etPassword);
        helperDB = new HelperDB(LoginActivity.this);
        db = helperDB.getReadableDatabase();
    }

    public void loadUser()
    {
        SharedPreferences sp=
                getSharedPreferences("StudentsMatch",MODE_PRIVATE);
        // Only the user name is remembered; the password is never saved on the device.
        String username = sp.getString("username", "");
        etUsername.setText(username);
    }
    public void saveUser(){
        SharedPreferences sp=
                getSharedPreferences("StudentsMatch",MODE_PRIVATE);

        SharedPreferences.Editor editor= sp.edit();

        editor.putString("username",etUsername.getText().toString());
        editor.remove("userpass");//older versions saved the password here

        editor.commit();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
loadUser();
        gotoReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(etUsername.getText().toString(), "", "", "",etPassword.getText().toString(), "");
                if (isAllOk(user)) {
                    int result = verifyCredentials(etUsername.getText().toString(), etPassword.getText().toString());
                    if (result == 1) {
                        ///להודיע למשתמש
                    } else if (result == 2) {
                        //להודיע שגיאת סיסמא
                    } else {
                     saveUser();
                       loadUserData(etUsername.getText().toString(),user);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        Log.d("StudentsMatch", "intentMainActivity"+user.toString());
                        startActivity(intent);
                    }
                }
            }
        });
    }

    public void loadUserData(String myuser,User user)
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
        user.setUserName(value);

        column=helperDB.USER_EMAIL;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
       user.setUserEmail(value);

        column=helperDB.USER_YEAR;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
        user.setUserYear(value);

        column=helperDB.USER_PHONE;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
        user.setUserPhone(value);

        column=helperDB.USER_PASS;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
       user.setUserPass(value);

        column=helperDB.USER_PIC;
        index = cursor.getColumnIndexOrThrow(column);
        value= cursor.getString(index);
       user.setPic(value);

    }

    public boolean isAllOk(User user) {

        String name=user.getUserName();
        String password=user.getUserPass();
        if (name.isEmpty()) {
            etUsername.setError("Missing user name");
            return false; // At least one field is empty
        }

        if (password.isEmpty()) {
            etPassword.setError("Missing password");
            return false; // At least one field is empty
        }

        // Validate email format using a simple regex pattern
        // Validate phone number format (you can customize the format validation as needed)
        // Add more password validation rules as needed
        if (password.length() < 6) {
            etPassword.setError("Password is too short (at least 6 characters)");
            return false; // Password is too short
        }
        // If all validations pass, return true to indicate that user properties are valid
        return true;
    }
    private int verifyCredentials(String currusername, String currpassword) {
        SQLiteDatabase db = helperDB.getWritableDatabase();//writable: may upgrade an old password

        String[] projection = {helperDB.USER_NAME, helperDB.USER_PASS};
        String selection = helperDB.USER_NAME + " = ?";
        String[] selectionArgs = new String [] {currusername};
        Log.d("StudentsMatch", "user name is "+ currusername);
        Cursor cursor = db.query(helperDB.USER_TABLE, projection, selection, selectionArgs, null, null, null);

        boolean result = cursor.moveToFirst();
        if (result==false)// אין משתמש בשם זה

        {
etUsername.setError("User does not exist");
            cursor.close();
            db.close();
            return 1;
        }
        String column=helperDB.USER_PASS;
        int index = cursor.getColumnIndex(column);
        String password= cursor.getString(index);

        cursor.close();

        if (PasswordHasher.verify(currpassword, password))// יש משתמש וסיסמה תקינה
        {
            if (!PasswordHasher.isHashed(password)) {
                // Old account with a plain-text password: replace it with a hash now.
                ContentValues cv = new ContentValues();
                cv.put(helperDB.USER_PASS, PasswordHasher.hash(currpassword));
                db.update(helperDB.USER_TABLE, cv, selection, selectionArgs);
            }
            db.close();
            return 0;
        }
        db.close();
        etPassword.setError("Wrong password");

        return 2;// יש משתמש אבל סיסמה שגויה
    }

}