package com.example.studentsmatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RequestAdapter extends RecyclerView.Adapter<RequestHolder>{
    private ArrayList<Request> arrayRequests;
    private Context context;
    public RequestAdapter(Context context, ArrayList<Request>arrayRequests)
    {
        this.context=context;
        this.arrayRequests=arrayRequests;
    }
    @Override
    public int getItemCount() {
        return arrayRequests.size();
    }

    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.helpfile,parent,false);

        RequestHolder requestHolder = new RequestHolder(inflate);

// Set the clickable property to true
        return requestHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        Log.d("StudentsMatch", "Adapter onBindViewHolder1" + arrayRequests.get(position).toString());
        holder.tvName.setText(arrayRequests.get(position).getRequestname());

        holder.tvsubject.setText(arrayRequests.get(position).getRequestsubject());

        String name = arrayRequests.get(position).getRequestimage();

        int resourceId = context.getResources().getIdentifier("_profileuser", "drawable", context.getPackageName());

        holder.ivPicture.setImageResource(resourceId);

        if (name != null && name.startsWith("content://")) {
            // Images picked from the gallery are stored as content URIs.
            holder.ivPicture.setImageURI(android.net.Uri.parse(name));
        } else if (name != null && !name.isEmpty()) {
            try {
                getPicFromFile(holder, name);
            } catch (FileNotFoundException e) {
                Log.d("StudentsMatch", "request image not found: " + name);
            }
        }
    }

    public void getPicFromFile(RequestHolder holder, String file) throws FileNotFoundException {
        Bitmap image;
        Log.d("StudentsMatch","get pic: "+file);
        try {
            InputStream stream = Files.newInputStream(Paths.get(file));
            image = BitmapFactory.decodeStream(stream);
            holder.ivPicture.setImageBitmap(image);
        }
        catch (IOException e)
        {
            int resourceId = context.getResources().getIdentifier("_profileuser", "drawable",context.getPackageName());

            holder.ivPicture.setImageResource(resourceId);

            Log.d("StudentsMatch","failed to load "+file+"\n "+e.toString());
        }
    }

}

