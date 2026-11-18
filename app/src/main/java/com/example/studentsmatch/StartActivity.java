package com.example.studentsmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    public CountDownTimer cdt;
    public final int MAX_TIME=3000;
    public final int SECOND=1000;
    public long missing ;
    public  static boolean paused;

    public void init(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        init();
        startTimer();
    }
    public void startTimer()
    {
        if (cdt!=null)
        {
            cdt.cancel();
        }
        missing=MAX_TIME;
        cdt=new CountDownTimer(missing,SECOND) {
            @Override
            public void onTick(long l) {

                    missing = missing - SECOND;
                Log.d("StudentsMatch", "onTik"+missing);

            }
            @Override
            public void onFinish() {
                Log.d("StudentsMatch", "onTik"+missing);
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;
                }
                Intent intent=new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();//close the splash so "back" from login doesn't return to it

            }
        }.start();

    }

    }
