package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class otp extends AppCompatActivity {

    private FirebaseHelper firebaseHelper;
    String name, address, contactNo, emailId, gender, handedness,userId,votp;
    Long deletedAt, dateOfBirth, createdAt;;
    Button btnlogin;
    EditText loginotp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnlogin = findViewById(R.id.btnlogin);
        loginotp = findViewById(R.id.loginotp);

        votp = getIntent().getStringExtra("generatedotp");

        onclick();
        firebaseHelper = new FirebaseHelper();
    }
    private void registerUser() {

        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Error: No data received", Toast.LENGTH_SHORT).show();
            return;
        }else {
            userId = intent.getStringExtra("userId");
            name = intent.getStringExtra("name");
            address = intent.getStringExtra("address");
            contactNo = intent.getStringExtra("contactNo");
            emailId = intent.getStringExtra("emailId");
            gender = intent.getStringExtra("gender");
            handedness = intent.getStringExtra("handedness");
            dateOfBirth = intent.getLongExtra("dateOfBirth", 0);
            createdAt = intent.getLongExtra("createdAt", 0);
            deletedAt = null;
        }

        try {
            firebaseHelper.addUserWithMap(userId,name, emailId, address, contactNo, gender, handedness, dateOfBirth, createdAt, deletedAt);
            Intent i = new Intent(otp.this,MainActivity.class);
            startActivity(i);

        } catch (Exception e) {
            Toast.makeText(otp.this, "om: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void onclick(){
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = loginotp.getText().toString();
                if(otp.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter OTP",Toast.LENGTH_SHORT).show();
                } else if (otp.equals(votp)) {
                    loginotp.setText("");
                    SharedPreferences sh = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sh.edit();

                    ed.putString("contact",contactNo);
                    ed.commit();
                    registerUser();
                }else {
                    Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}