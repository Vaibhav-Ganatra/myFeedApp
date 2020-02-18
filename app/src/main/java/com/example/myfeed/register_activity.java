package com.example.myfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register_activity extends AppCompatActivity {

    EditText name_registerScreen_java, password_registerScreen_java, confirmPassword_registerScreen_java, email_registerScreen_java;
    String name;
    String password;
    String confirmPassword, email;
    Button registerButton_registerScreen_java;
    ProgressDialog mProgress;
    FirebaseAuth mAuth;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        name_registerScreen_java = findViewById(R.id.name_registerActivity_xml);
        password_registerScreen_java = findViewById(R.id.password_registerActivity_xml);
        confirmPassword_registerScreen_java = findViewById(R.id.reenterPassword_registerActivity_xml);
        password_registerScreen_java.getTransformationMethod();
        confirmPassword_registerScreen_java.getTransformationMethod();
        email_registerScreen_java = findViewById(R.id.email_registerActivity_xml);
        registerButton_registerScreen_java = findViewById(R.id.registerButton_registerActivity_xml);

        registerButton_registerScreen_java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = name_registerScreen_java.getText().toString();
                password = password_registerScreen_java.getText().toString();
                confirmPassword = confirmPassword_registerScreen_java.getText().toString();
                email = email_registerScreen_java.getText().toString();
                if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.equals(confirmPassword))
                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        Toast.makeText(getApplicationContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                                    databaseReference.child(uid).child("name").setValue(name);
                                    databaseReference.child(uid).child("phone").setValue(0);


                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                                    mAuth.getCurrentUser().updateProfile(profileChangeRequest);
                                    Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                                            "://" + getApplicationContext().getResources().getResourcePackageName(R.drawable.login_icon)
                                            + '/' + getApplicationContext().getResources().getResourceTypeName(R.drawable.login_icon)
                                            + '/' + getApplicationContext().getResources().getResourceEntryName(R.drawable.login_icon) );
                                    UserProfileChangeRequest userProfileChangeRequest= new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();
                                    mAuth.getCurrentUser().updateProfile(userProfileChangeRequest);


                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "E-mail sent your E-mail address, kindly check your E-mail", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(i);
                                                mAuth.signOut();
                                            } else
                                                Toast.makeText(getApplicationContext(), "E-mail Verification failed, please try again", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                } else {
                                    Toast.makeText(getApplicationContext(), "Some error occurred, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                }

            }
        });
    }
}
