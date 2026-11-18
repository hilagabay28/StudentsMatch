package com.example.studentsmatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    public MaterialToolbar topAppBar;
    public CircleImageView photoView;
    public BottomNavigationView bottomNavigationView;

    public static User user;

    public void init() {
        topAppBar = findViewById(R.id.topAppBar);
        photoView=findViewById(R.id.photoView);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
    }

    public void getPicFromFile() throws FileNotFoundException {
        Log.d("StudentsMatch","get pic: "+user.getPic());
        Bitmap image = ProfilePic.load(user.getPic());
        if (image != null) {
            photoView.setImageBitmap(image);
        } else {
            Log.d("StudentsMatch","no profile pic to show for "+user.getUserName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        user = (User) getIntent().getSerializableExtra("user");
        try {
            getPicFromFile();
        } catch (FileNotFoundException e) {
            Log.d("StudentsMatch","failed to load "+user.getPic()+"\n "+e.toString());
        }

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.editBtn) {
                    Log.d("StudentsMatch","its ok"+user.toString());
                    Intent intent = new Intent(MainActivity.this, EditUserActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                }
                return true;
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.helpBtn){
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, new GethelpFragment(), null)
                            .setReorderingAllowed(true)
                            .commit();
                    topAppBar.setTitle("Get help");
                }
               else if (itemId == R.id.rateBtn){
                 Intent intent =new Intent(MainActivity.this, RateActivity.class);
                 intent.putExtra("user", user);
                 startActivity(intent);
                }
                else if (itemId == R.id.assistBtn){
                    fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, GivehelpFragment.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // Name can be null
                            .commit();
                    topAppBar.setTitle("Give help");
                }
                return true;
            }
        });

    }
}
