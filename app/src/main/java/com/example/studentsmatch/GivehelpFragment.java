package com.example.studentsmatch;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class GivehelpFragment extends Fragment {
    public RecyclerView rvList;
    public RecyclerView.Adapter adapter;
    public ArrayList<Request> arrayRequest;

    public Context context;
    public HelperDB helperDB;
    public SQLiteDatabase db;

    public GivehelpFragment() {
        // Required empty public constructor
    }

    public void initList(View v) {

        rvList = v.findViewById(R.id.rvList);
        context = getContext();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);

        rvList.setLayoutManager(linearLayoutManager);

        arrayRequest = new ArrayList<>();

        getMePeople();
        Log.d("StudentsMatch","getMePeople recived:"+arrayRequest.toString());

        adapter = new RequestAdapter(context, arrayRequest);

        rvList.setAdapter(adapter);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_givehelp, container, false);
        initList(v);

        rvList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Do something when the user touches an item
                Log.d("StudentsMatch",  " onInterceptTouchEvent" );
                return true;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Do something when the user touches and releases an item
// Do something when the user touches and releases an item

                View row = rv.findChildViewUnder(e.getX(), e.getY());
                int position = row == null ? RecyclerView.NO_POSITION : rv.getChildAdapterPosition(row);

                if (e.getAction() == MotionEvent.ACTION_UP && position != RecyclerView.NO_POSITION) {

                    //pressed
                    Intent it=new Intent(getContext(),HelpDescriptionActivity.class);
                    it.putExtra("request",arrayRequest.get(position));
                    it.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    startActivity(it);
                }
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                // Do something when the parent view disallows intercepting touch events
                Log.d("StudentsMatch",  " onRequestDisallowInterceptTouchEvent" );
            }
        });

        return v;

    }

    public void getMePeople()   //מבקשת מהדאטא בייס להציג את הבקשות של האנשים שמבקשים עזרה במקצועות
    {
        helperDB = new HelperDB(context);

        String[] projection = {
                helperDB.SEL_CHOICE,
                helperDB.SEL_SUBJECT
        };

        String selection = helperDB.SEL_EMAIL + " = ? AND " + helperDB.SEL_CHOICE + "=?";
        String[] selectionArgs = new String[]{MainActivity.user.getUserEmail(), "good at this sub"};

        db = helperDB.getReadableDatabase();

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

        int i = cursor.getCount();
        cursor.moveToFirst();
        Log.d("StudentsMatch", "getMePeople found"+i+" were i can help");

        ArrayList<String> GoodAt = new ArrayList<String>();
        for (int j = 0; j < i; j++) {

            String column = HelperDB.SEL_SUBJECT;
            int index = cursor.getColumnIndexOrThrow(column);
            String subject = cursor.getString(index);
            Log.d("StudentsMatch", "i am good at "+subject);

            GoodAt.add(subject);
            cursor.moveToNext();
        }

        cursor.close();;

        for (int j = 0; j < i; j++) {
            String subject = GoodAt.get(j);
            Log.d("StudentsMatch", "looking for peple that need help in "+subject);

            String[] projection1 = {helperDB.REQUEST_NAME,
                    helperDB.REQUEST_SUBJECT,
                    helperDB.REQUEST_DESCRIPTION,
                    helperDB.REQUEST_IMAGE

            };

            String selection1 = helperDB.REQUEST_SUBJECT + " = ? ";
            String[] selectionArgs1 = new String[]{subject};

            db = helperDB.getReadableDatabase();

            Cursor cursor1 = db.query(
                    helperDB.REQUEST_TABLE,
                    projection1,
                    selection1,
                    selectionArgs1,
                    null,
                    null,
                    null,
                    null
            );

            int len = cursor1.getCount();
            Log.d("StudentsMatch", " found"+len+"requests for"+subject);
            cursor1.moveToFirst();

            for (int reqnum = 1; reqnum <= len; reqnum++) {

                String column = HelperDB.REQUEST_NAME;
                int index = cursor1.getColumnIndexOrThrow(column);
                String value = cursor1.getString(index);

                String username = value;

                column = HelperDB.REQUEST_SUBJECT;
                index = cursor1.getColumnIndexOrThrow(column);
                value = cursor1.getString(index);
                Log.d("StudentsMatch", "pass:" + value);
                String requestsub = value;

                column = HelperDB.REQUEST_DESCRIPTION;
                index = cursor1.getColumnIndexOrThrow(column);
                value = cursor1.getString(index);
                Log.d("StudentsMatch", "year:" + value);
                String description = value;

                column = HelperDB.REQUEST_IMAGE;
                index = cursor1.getColumnIndexOrThrow(column);
                value = cursor1.getString(index);
                Log.d("StudentsMatch", "pic:" + value);
                String image = value;

                Request newRequest = new Request(username, requestsub, description, image);
                Log.d("StudentsMatch", "found request i can help "+newRequest.toString());

                arrayRequest.add(newRequest);
                cursor1.moveToNext();
            }
            cursor1.close();
        }

    }
}
