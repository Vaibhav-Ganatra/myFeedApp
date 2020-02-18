package com.example.myfeed;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class post_image_vh extends RecyclerView.ViewHolder {

   ImageView myImage;
   TextView imageNo;
    public post_image_vh(@NonNull View itemView) {
        super(itemView);

      myImage= itemView.findViewById(R.id.image_rv);
      imageNo= itemView.findViewById(R.id.imageNumber);

    }
}
