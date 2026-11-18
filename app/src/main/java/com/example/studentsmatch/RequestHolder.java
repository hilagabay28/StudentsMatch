package com.example.studentsmatch;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequestHolder extends RecyclerView.ViewHolder{
    public TextView tvName;
    public TextView tvsubject;
    public ImageView ivPicture;

    public RequestHolder(@NonNull View itemView) {
        super(itemView);
        tvName=itemView.findViewById(R.id.tvName);
        ivPicture=itemView.findViewById(R.id.ivPicture);
        tvsubject=itemView.findViewById(R.id.tvsubject);

        itemView.setClickable(true);

    }

}
