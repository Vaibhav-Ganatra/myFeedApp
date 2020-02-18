package com.example.myfeed;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class addPost extends Fragment {

    private View v;
    private ArrayList<post_image_layout> postArrayList = new ArrayList<>();
    private ArrayList<Uri> uriList= new ArrayList<>();


    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db= FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fresco.initialize(getContext());
        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.fragment_add_post, container, false);
        SimpleDraweeView profilepic = v.findViewById(R.id.addPostProfilePic);
        profilepic.setImageURI(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());

        final FirebaseStorage storage= FirebaseStorage.getInstance();

        TextView email = v.findViewById(R.id.addPost_email);
        TextView name = v.findViewById(R.id.addPost_displayName);

        email.setText(mAuth.getCurrentUser().getEmail());
        name.setText(mAuth.getCurrentUser().getDisplayName());

        final EditText postText = v.findViewById(R.id.postText);
        Button addImage;

        addImage = v.findViewById(R.id.add_image);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickImage, 1);
                }
            }
        });

        v.findViewById(R.id.addPost_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String postTexts= postText.getText().toString();
                final String postID = String.valueOf(Math.random()).substring(3);
                final StorageReference storageReference = storage.getReference().child("Posts").child(postID);

                final DatabaseReference myRef = db.getReference().child("Posts").child(postID);

                if(postTexts.length()==0){
                    final AlertDialog.Builder alertDialog= new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Are you sure you don't want to add text to your post?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(uriList.size()==0)
                                Toast.makeText(getContext(), "Cannot make empty post", Toast.LENGTH_SHORT).show();
                            else {
                                DatabaseReference saveID=db.getReference().child("PostID").child("Images");
                                String sid=saveID.push().getKey();
                                saveID.child(sid).setValue(postID);


                                for (int j = 0; j < uriList.size(); j++) {
                                    String s = String.valueOf(Math.random()).substring(3);
                                    storageReference.child(s)
                                            .putFile(uriList.get(j));
                                    String pictureID=saveID.getParent().child(postID).push().getKey();
                                    saveID.getParent().child(postID).child(pictureID).setValue(s);
                                    myRef.child(s).setValue(String.valueOf(storageReference.child(s).getDownloadUrl()));
                                    if (j == uriList.size() - 1) {
                                        updateUI();
                                    }
                                }
                            }


                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });



                    alertDialog.create();
                    alertDialog.show();

                }
                else {
                    if (uriList.size() == 0) {
                        DatabaseReference saveID = db.getReference().child("PostID").child("Text");
                        String sid = saveID.push().getKey();
                        saveID.child(sid).setValue(postID);
                        myRef.setValue(postTexts).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateUI();
                            }
                        });
                    } else {

                        StorageReference storageReference2 = storage.getReference().child("Posts")
                                .child(postID);
                        DatabaseReference saveID = db.getReference().child("PostID").child("Images+Text");
                        String sid = saveID.push().getKey();
                        saveID.child(sid).setValue(postID);
                        DatabaseReference myRef2 = db.getReference().child("Posts").child(postID);
                        myRef2.child("PostText").setValue(postTexts);
                        for (int j = 0; j < uriList.size(); j++) {
                            String s = String.valueOf(Math.random()).substring(3);
                            storageReference2.child(s)
                                    .putFile(uriList.get(j));
                            String pictureID = saveID.getParent().child(postID).push().getKey();
                            saveID.getParent().child(postID).child(pictureID).setValue(s);
                            myRef2.child(s).setValue(String.valueOf(storageReference.child(s).getDownloadUrl()));


                            if (j == uriList.size() - 1) {
                                updateUI();
                            }
                        }


                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Gallery permission granted", Toast.LENGTH_LONG).show();
                Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImage, 1);
            } else {
                Toast.makeText(getContext(), "Gallery permission denied", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED && requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                Uri filePath = data.getData();
               // initialising arrayList

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (filePath != null) {
                    Cursor cursor = getContext().getContentResolver().query(filePath,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        postArrayList.add(new post_image_layout(picturePath));
                        uriList.add(filePath);


                        RecyclerView image_rv = v.findViewById(R.id.images_rv_addPost);
                        image_rv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
                        if(postArrayList.size()>1)
                        Toast.makeText(getContext(), "Swipe right to see other images", Toast.LENGTH_SHORT).show();
                        image_rv_adapter adapter = new image_rv_adapter(getContext(), postArrayList);
                        image_rv.setAdapter(adapter);


                    }
                }
            }

        }
    }

    private void updateUI() {
        Toast.makeText(getContext(), "Post has been added successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getContext(), mainApp_activity.class));

    }

}

