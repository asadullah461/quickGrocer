package com.example.quickgrocer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quickgrocer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button btnsignup;
    Button btnlogin;
    EditText etemaillogin,etpasswordlogin;
    FirebaseAuth auth;
    ProgressBar progressBar;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Check if user is already logged in
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Navigate to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        btnlogin=findViewById(R.id.btnlogin);

        etemaillogin=findViewById(R.id.etemaillogin);
        etpasswordlogin=findViewById(R.id.etpasswordlogin);

        auth=FirebaseAuth.getInstance();

        btnsignup=findViewById(R.id.btnregister);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent1);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loginUser() {
        String userEmail=etemaillogin.getText().toString();
        String userPassword=etpasswordlogin.getText().toString();


        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "Email Is Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword)){
            Toast.makeText(this, "Password Is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 6){
            Toast.makeText(this, "Password Length Must Be Greater Than 6 Letter", Toast.LENGTH_SHORT).show();
            return;
        }
//        Login User
        auth.signInWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Save login state
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("userEmail", userEmail);
                            editor.apply();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Error Logging In", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}