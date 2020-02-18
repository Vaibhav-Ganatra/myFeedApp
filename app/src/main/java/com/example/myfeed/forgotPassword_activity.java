package com.example.myfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword_activity extends AppCompatActivity {

    EditText resetEmail_forgotPasswordScreen_java;
    Button sendEmailButton_forgotPasswordScreen_java;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_activity);

        resetEmail_forgotPasswordScreen_java = findViewById(R.id.resetEmail_forgotPasswordScreen_xml);
        sendEmailButton_forgotPasswordScreen_java = findViewById(R.id.sendEmailButton_forgotPasswordScreen_xml);
        mAuth = FirebaseAuth.getInstance();

        sendEmailButton_forgotPasswordScreen_java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = resetEmail_forgotPasswordScreen_java.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter E-mail", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password reset link has been sent successfully, please check your E-mail", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(forgotPassword_activity.this, MainActivity.class);
                                startActivity(i);
                            }

                        }
                    });
                }
            }
        });
    }
}