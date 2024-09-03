package com.example.quickgrocer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickgrocer.R;
import com.example.quickgrocer.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    EditText etname,etemailsignup,etpasswordsignup;
    TextView txtsignin;
    Button btnsignup;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressBar=findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        btnsignup=findViewById(R.id.btnsignup);
        etname=findViewById(R.id.etname);
        etemailsignup=findViewById(R.id.etemailsignup);
        etpasswordsignup=findViewById(R.id.etpasswordsignup);
        txtsignin=findViewById(R.id.txtsignin);

        txtsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentsignin=new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intentsignin);
            }
        });
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createuser();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void createuser() {

        String userName=etname.getText().toString();
        String userEmail=etemailsignup.getText().toString();
        String userPassword=etpasswordsignup.getText().toString();

        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Name Is Empty", Toast.LENGTH_SHORT).show();
            return;
        }

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
//        Create User
        auth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            UserModel userModel=new UserModel(userName,userEmail,userPassword);
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Admin").child(id).setValue(userModel);
                            progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(RegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Error:", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}