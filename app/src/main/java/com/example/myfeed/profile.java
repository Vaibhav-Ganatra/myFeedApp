package com.example.myfeed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class profile extends Fragment {
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
    private String initialName= user.getDisplayName();
    private String initialPhoneNumber="";
    SimpleDraweeView profilePic;

    private EditText name;
    private EditText phoneNumber;
    private Boolean isPhoneNumberVerified=false;
    private Button submit;
    private Button verify;
    private String latestVerifiedPhoneNumber;
    private Uri currentProfilePic;
    private String apiKey = "032e28a0-4b77-11ea-9fa5-0200cd936042", details= "";
    private String currentPhoneNumber="";
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        Fresco.initialize(getContext());
         View v= inflater.inflate(R.layout.fragment_profile, container, false);

         name=v.findViewById(R.id.name_profileFragment_xml);
        EditText email = v.findViewById(R.id.email_profileFragment_xml);
         phoneNumber=v.findViewById(R.id.phone_profileFragment_xml);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(getContext(), gso);

       profilePic= v.findViewById(R.id.profilePicture_profileFragment_xml);
       currentProfilePic=user.getPhotoUrl();
        profilePic.setImageURI(currentProfilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
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

         submit=v.findViewById(R.id.submit_profileFragment_xml);
        Button save = v.findViewById(R.id.save_profileFragment_xml);
        Button logout = v.findViewById(R.id.logOut_profileFragment_xml);
         verify=v.findViewById(R.id.verify_profileFragment_xml);

         name.setText(initialName);
        email.setText(user.getEmail());

        db.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                { initialPhoneNumber=dataSnapshot.getValue().toString();
                    System.out.println(initialPhoneNumber);
                    if(!initialPhoneNumber.equals("0")) {
                       isPhoneNumberVerified=true;
                        phoneNumber.setText(initialPhoneNumber);
                        latestVerifiedPhoneNumber=initialPhoneNumber;
                        verify.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading your profile, few details may not be displayed", Toast.LENGTH_SHORT).show();
            }
        });
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                    if(phoneNumber.getText().toString().length()==10 && !phoneNumber.getText().toString().equals(initialPhoneNumber) &&!phoneNumber.getText().toString().equals(latestVerifiedPhoneNumber))
                    {   Toast.makeText(getContext(), "Please verify your phone number", Toast.LENGTH_SHORT).show();
                        verify.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.INVISIBLE);
                        isPhoneNumberVerified=false;
                    }
                    else if(phoneNumber.getText().toString().equals(initialPhoneNumber)|| phoneNumber.getText().toString().equals(latestVerifiedPhoneNumber)) {
                        isPhoneNumberVerified = true;
                        verify.setVisibility(View.INVISIBLE);
                    }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.getText().toString().length() == 10) {
                    currentPhoneNumber=phoneNumber.getText().toString();
                    Call<MessageResponse> c = RetrofitClient.getInstance().getApi().sentOTP(apiKey, "+91".concat(currentPhoneNumber));
                    c.enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            String status = null;
                            if (response.body() != null) {
                                status = response.body().getStatus();
                                details = response.body().getDetails();
                            }
                            phoneNumber.setText("");
                            submit.setVisibility(View.INVISIBLE);
                            verify.setVisibility(View.VISIBLE);

                            if (status != null && status.toLowerCase().equals("success")) {
                                System.out.println("OTP sent");
                                phoneNumber.setHint("Enter your OTP here");
                                verify.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View view) {
                                                                  OTPVerification();

                                                              }
                                                          }
                                );
                            }
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    });
                }
                else Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                if(mGoogleSignInClient!=null)
                    revokeAccess();
                startActivity(new Intent(getContext(), MainActivity.class));


            }
        });
        return v;
    }

    private  void saveData(){
        if(!name.getText().toString().equals(initialName)) {
            if(name.getText().toString().length()==0)
                Toast.makeText(getContext(),"Name cannot be empty, your name has not been changed", Toast.LENGTH_SHORT).show();
            else {
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                user.updateProfile(userProfileChangeRequest);
            }
        }

        if(currentProfilePic!=user.getPhotoUrl()){
            UserProfileChangeRequest userProfileChangeRequest= new UserProfileChangeRequest.Builder().setPhotoUri(currentProfilePic).build();
            user.updateProfile(userProfileChangeRequest);
        }

        if(isPhoneNumberVerified && !latestVerifiedPhoneNumber.equals(initialPhoneNumber)){
            db.child("phone").setValue(latestVerifiedPhoneNumber);


        }
        else if(!isPhoneNumberVerified) Toast.makeText(getContext(), "Please verify your phone number first", Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Your changes have been saved successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getContext(), mainApp_activity.class));
    }


    private void OTPVerification() {
        Call<MessageResponse> call1 = RetrofitClient.getInstance().getApi().verifyOTP(apiKey, details, phoneNumber.getText().toString());
        call1.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> c2, Response<MessageResponse> response1) {
                if (response1.body().getStatus().toLowerCase().equals("success")) {
                    Toast.makeText(getContext(), "Phone Number verified", Toast.LENGTH_SHORT).show();
                    submit.setVisibility(View.INVISIBLE);
                    isPhoneNumberVerified=true;
                    latestVerifiedPhoneNumber=currentPhoneNumber;
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Phone Number verification failed, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Logged out of Google successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
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
                currentProfilePic = data.getData();
                profilePic.setImageURI(currentProfilePic);




              /**  String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (filePath != null) {
                    Cursor cursor = getContext().getContentResolver().query(filePath,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
               */
            }
        }
    }
}