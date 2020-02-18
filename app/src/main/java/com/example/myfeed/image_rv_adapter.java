package com.example.myfeed;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class image_rv_adapter extends RecyclerView.Adapter<post_image_vh> {

    private Context context;
    private ArrayList<post_image_layout> imageList;

    public image_rv_adapter(Context context, ArrayList<post_image_layout> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public post_image_vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new post_image_vh(LayoutInflater.from(context).inflate(R.layout.images_rv_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull post_image_vh holder, int position) {
        holder.myImage.setImageBitmap(BitmapFactory.decodeFile(imageList.get(position).getPicturePath()));
        holder.imageNo.setText(String.valueOf(position+1).concat(" of ").concat(String.valueOf(imageList.size())));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
