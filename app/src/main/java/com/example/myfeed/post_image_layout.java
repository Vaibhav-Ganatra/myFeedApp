package com.example.myfeed;

import android.net.Uri;

public class post_image_layout {

    String picturePath;

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public post_image_layout(String picturePath) {
        this.picturePath = picturePath;
    }
}
