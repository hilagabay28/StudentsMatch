package com.example.studentsmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Typeface;

import android.content.res.Resources;

public class RateActivity extends AppCompatActivity {
    public LinearLayout llSubjects;
    public static String[] arrSubjects;
    public Button btnSave;
     public User user;
    public HelperDB helperDB;
    public SQLiteDatabase db;

    public void init() {
        llSubjects = findViewById(R.id.llSubjects);
        arrSubjects = new String[]{"Math", "Literature", "Bible", "English", "Physics", "Computer"};
        btnSave=findViewById(R.id.btnSave);
        helperDB=new HelperDB(this);

        db = helperDB.getReadableDatabase();

    }

    public void creatList(){
        LinearLayout.LayoutParams lpVertical = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams lpHorizontal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpHorizontal.setMargins(15, 10, 10, 10);

        for (int i = 0; i < arrSubjects.length; i++) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setLayoutParams(lpHorizontal);

            TextView tv = new TextView(this);
            tv.setText(arrSubjects[i]);
            tv.setWidth(400);
            tv.setTextSize(25);
          tv.setTypeface(Typeface.create("suezoneregular", Typeface.BOLD));

            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llSubjects.getLayoutParams();
            params.gravity = Gravity.CENTER;
            llSubjects.setLayoutParams(params);

            ll.addView(tv, lpHorizontal);
            Spinner sp = new Spinner(RateActivity.this);

            sp.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            sp.setBackgroundColor(Color.WHITE);
            String[] options={"good at this sub","not so good at this sub"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, options);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter);
            ll.addView(sp);
            llSubjects.addView(ll, lpVertical);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        init();
        user = (User) getIntent().getSerializableExtra("user");
        Log.d("StudentsMatch", "onCreadtRateActivity"+user.getUserEmail());
        creatList();
        Log.d("StudentsMatch", "aftercreatList"+user.getUserEmail());
        if(isalreadyreated())
        {
            Log.d("StudentsMatch", "afterAlreadyreated"+user.getUserEmail());
            loadselection();
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();

                Intent intent = new Intent(RateActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }
    public void loadselection(){
        String[] projection = {
                helperDB.SEL_EMAIL,
                helperDB.SEL_SUBJECT,
                helperDB.SEL_CHOICE

        };
        String selection = helperDB.SEL_EMAIL + " = ?";
        String[] selectionArgs =  new String [] {user.getUserEmail()};
        SQLiteDatabase db = helperDB.getReadableDatabase();
        Cursor cursor = db.query(
                helperDB.SELECTIONS_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
        Log.d("StudentsMatch","load count:"+cursor.getCount());
        int len=cursor.getCount();
        cursor.moveToFirst();
        for(int i=0; i<len; i++) {

            String column = helperDB.SEL_SUBJECT;
            int index = cursor.getColumnIndexOrThrow(column);
            String value = cursor.getString(index);

            Log.d("StudentsMatch", "subject:" + value);

            column = helperDB.SEL_CHOICE;
            index = cursor.getColumnIndexOrThrow(column);
          String  value2 = cursor.getString(index);

            Log.d("StudentsMatch", "choice:" + value2);

            Log.d("StudentsMatch", "update :" + value+"->"+value2);

            updateSpinners(value, value2);
            cursor.moveToNext();
        }

}
public void updateSpinners(String subject, String choice){
    for (int i = 0; i < arrSubjects.length; i++) {
        LinearLayout ll = (LinearLayout) llSubjects.getChildAt(i);

        // Get the subject name
        TextView tvSubject = (TextView) ll.getChildAt(0);
        String subjectName = tvSubject.getText().toString();
        Spinner sp = (Spinner) ll.getChildAt(1);
        String selected = sp.getSelectedItem().toString();
        Log.d("StudentsMatch", subjectName + "," + choice);
        if(subjectName.equals(subject))
        {
            if(choice.equals("not so good at this sub")){
                sp.setSelection(1);
            }
            else {
                Log.d("StudentsMatch","dont touch"+subject);
            }
            return;
        }

    }
}
    public void insert(){
        for (int i = 0; i < arrSubjects.length; i++) {
            LinearLayout ll = (LinearLayout) llSubjects.getChildAt(i);

            // Get the subject name
            TextView tvSubject = (TextView) ll.getChildAt(0);
            String subjectName = tvSubject.getText().toString();
            Spinner sp=(Spinner) ll.getChildAt(1);
            String selected=sp.getSelectedItem().toString();
            Log.d("StudentsMatch",subjectName+","+selected );

            ContentValues cv= new ContentValues();
            cv.put(helperDB.SEL_EMAIL,user.getUserEmail());
            cv.put(helperDB.SEL_SUBJECT,subjectName);
            cv.put(helperDB.SEL_CHOICE,selected);
            Log.d("StudentsMatch", "cv:"+cv.toString());
            db=helperDB.getWritableDatabase();
            db.insert(helperDB.SELECTIONS_TABLE,null,cv);

            db.close();

        }
    }
    public void update(){
        for (int i = 0; i < arrSubjects.length; i++) {
            LinearLayout ll = (LinearLayout) llSubjects.getChildAt(i);

            // Get the subject name
            TextView tvSubject = (TextView) ll.getChildAt(0);
            String subjectName = tvSubject.getText().toString();
            Spinner sp=(Spinner) ll.getChildAt(1);
            String selected=sp.getSelectedItem().toString();
            Log.d("StudentsMatch",subjectName+","+selected );

            SQLiteDatabase db=helperDB.getWritableDatabase();

            String [] oldData={user.getUserEmail(), subjectName};

            String infield=helperDB.SEL_EMAIL+"=? AND "+helperDB.SEL_SUBJECT+"=? ";

            ContentValues cv=new ContentValues();

            cv.put(helperDB.SEL_CHOICE,selected);
            Log.d("StudentsMatch", "cv:"+cv.toString());
            int rowsChanged= db.update(helperDB.SELECTIONS_TABLE, cv, infield,

                    oldData);
            if (rowsChanged>0)
            {
            }

        }
    }

    public boolean isalreadyreated(){
        String[] projection = {
                helperDB.SEL_EMAIL

        };

        String selection = helperDB.SEL_EMAIL + "=?";
        Log.d("StudentsMatch", "isalreadyreated2"+selection);

        String[] selectionArgs =  new String [] {user.getUserEmail()};
        Log.d("StudentsMatch", "isalreadyreated3"+user.getUserEmail());
        db=helperDB.getReadableDatabase();

        Cursor cursor = db.query(
                helperDB.SELECTIONS_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.getCount()==0)
        {
            db.close();
            return false;
        }
        db.close();
        return true;

    }

    public void save(){
        Log.d("StudentsMatch", user.getUserName());
        if(isalreadyreated()){
            update();
        }
        else{
            insert();
        }

}
}