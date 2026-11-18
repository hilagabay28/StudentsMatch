package com.example.studentsmatch;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.SupportActionModeWrapper;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GethelpFragment extends Fragment {
    ImageView uploadImageBtn;
    Uri capturedImageUri;
    Spinner subjectsSpinner;
    FloatingActionButton saveRequest;
    EditText getHelpEditText;
    HelperDB helperDB;
    SQLiteDatabase db;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("StudentsMatch", "Selected URI: " + uri);
                        capturedImageUri = uri;

                        uploadImageBtn.setImageURI(uri);
                    } else {
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gethelp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helperDB=new HelperDB(getContext());
        saveRequest = view.findViewById(R.id.saveRequest1);
        subjectsSpinner = view.findViewById(R.id.subjectsSpinner);
        uploadImageBtn = view.findViewById(R.id.uploadImageBtn);
        getHelpEditText = view.findViewById(R.id.getHelpEditText);

        String[] arrSubjects;
        arrSubjects = new String[]{"Math", "Literature", "Bible", "English", "Physics", "Computer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, arrSubjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectsSpinner.setAdapter(adapter);

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

                uploadImageBtn.setImageURI(capturedImageUri);
            }
        });

        saveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = getHelpEditText.getText().toString().trim();
                if (description.isEmpty()) {
                    getHelpEditText.setError("Please describe the problem");
                    return;
                }
                // The image is optional.
                String image = "";
                if (capturedImageUri != null) {
                    int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContext().getContentResolver().takePersistableUriPermission(capturedImageUri, flag);
                    image = capturedImageUri.toString();
                }

                Request request = new Request(MainActivity.user.getUserName(), subjectsSpinner.getSelectedItem().toString(), description, image);
                ContentValues cv = new ContentValues();
                cv.put(HelperDB.REQUEST_NAME, request.getRequestname());
                cv.put(HelperDB.REQUEST_SUBJECT, request.getRequestsubject());
                cv.put(HelperDB.REQUEST_DESCRIPTION, request.getRequestdescription());
                cv.put(HelperDB.REQUEST_IMAGE, request.getRequestimage());
                db = helperDB.getWritableDatabase();
                db.insert(HelperDB.REQUEST_TABLE, null, cv);
                db.close();

                Toast.makeText(getContext(),"Your form has been successfully submitted ",Toast.LENGTH_LONG).show();
            }
        });
    }

}
