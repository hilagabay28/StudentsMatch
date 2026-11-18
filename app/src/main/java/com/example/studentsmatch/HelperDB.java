package com.example.studentsmatch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HelperDB extends SQLiteOpenHelper {
    public static final String DB_FILE = "account_info.db";
    public static final String USER_TABLE = "user_table";

    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_YEAR = "user_year";
    public static final String USER_PHONE = "user_phone";
    public static final String USER_PASS = "user_pass";
    public static final String USER_PIC = "user_pic";

    public static final String SELECTIONS_TABLE = "selections_table";
    public static final String SEL_EMAIL = "sel_email";
    public static final String SEL_SUBJECT = "sel_subject";

    public static final String SEL_CHOICE = "sel_choice";

    public static final String REQUEST_TABLE = "request_table";

    public static final String REQUEST_NAME = "request_name";

    public static final String REQUEST_SUBJECT = "request_subject";

    public static final String REQUEST_DESCRIPTION = "request_description";

    public static final String REQUEST_IMAGE = "request_image";

    public static final String REQUEST_FILE = "request_file";

    public HelperDB(@Nullable Context context) {
        super(context, DB_FILE, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String st = "CREATE TABLE IF NOT EXISTS " + USER_TABLE;
        st += " ( " + USER_NAME + " TEXT, ";
        st += USER_EMAIL + " TEXT, ";
        st += USER_YEAR + " TEXT, ";
        st += USER_PHONE + " TEXT, ";
        st += USER_PASS + " TEXT, ";
        st +=  USER_PIC+ " TEXT); ";

        sqLiteDatabase.execSQL(st);

        String st2 = "CREATE TABLE IF NOT EXISTS " + SELECTIONS_TABLE;
        st2 += " ( " + SEL_EMAIL + " TEXT, ";
        st2 += SEL_SUBJECT+ " TEXT, ";
        st2 +=  SEL_CHOICE+ " TEXT); ";

        sqLiteDatabase.execSQL(st2);

        String st3 = "CREATE TABLE IF NOT EXISTS " + REQUEST_TABLE;
        st3 += " ( " + REQUEST_NAME + " TEXT, ";
        st3 += REQUEST_SUBJECT + " TEXT, ";
        st3 += REQUEST_DESCRIPTION + " TEXT, ";
        st3 += REQUEST_IMAGE + " TEXT, ";
        st3 +=  REQUEST_FILE+ " TEXT); ";

        sqLiteDatabase.execSQL(st3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
