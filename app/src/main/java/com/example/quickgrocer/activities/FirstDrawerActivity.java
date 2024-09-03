package com.example.quickgrocer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quickgrocer.R;
import com.google.firebase.auth.FirebaseAuth;

public class FirstDrawerActivity extends AppCompatActivity {
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_drawer);

        logout = findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences.Editor editor = getSharedPreferences("MySharedPref", MODE_PRIVATE).edit();
                editor.putBoolean("isLoginIn", false);
                editor.apply();
                Toast.makeText(FirstDrawerActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FirstDrawerActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
    @Override
    public void onBackPressed() {
        //        Toast.makeText(this, "Can't back from this state", Toast.LENGTH_SHORT).show()
        super.onBackPressed();
        finishAffinity();
    }
}