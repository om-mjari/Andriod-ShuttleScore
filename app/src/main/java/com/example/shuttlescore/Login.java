package com.example.shuttlescore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class Login extends AppCompatActivity {

    Button btnsendotp;
    TextView txttitle,txtregister;
    EditText etxtcontact;
    String generatedOTP = "";
    String otpString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });


        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        initialize();
        onclick();
    }
    private void initialize(){
        btnsendotp = findViewById(R.id.btnsendotp);
        txttitle = findViewById(R.id.txttitle);
        txtregister = findViewById(R.id.txtregister);
        etxtcontact = findViewById(R.id.etxtcontact);
    }
    private void onclick(){
        txtregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,Registration.class);
                startActivity(i);
            }
        });
        btnsendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cnt = etxtcontact.getText().toString();
                if(cnt.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Contact Number",Toast.LENGTH_SHORT).show();
                }
                else{
                    if (cnt.length() < 10) {
                        etxtcontact.setError("Please Enter valid phone number");
                    }else {
                        checkContactExists(cnt);
                    }
                }
            }
        });


    }
    private void checkContactExists(String contactNo) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        usersRef.orderByChild("phone").equalTo(contactNo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Number Found! Proceeding...", Toast.LENGTH_SHORT).show();

                            String cnt = etxtcontact.getText().toString();

                            generatedOTP = generateOTP();
                            if (ContextCompat.checkSelfPermission(Login.this, Manifest.permission.SEND_SMS) ==
                                    PackageManager.PERMISSION_GRANTED) {
                                sendSMS();
                            } else {
                                ActivityCompat.requestPermissions(Login.this,
                                        new String[]{Manifest.permission.SEND_SMS}, 100);
                            }

                            Fragmentotp otpFragment = new Fragmentotp();
                            Bundle bundle = new Bundle();
                            bundle.putString("contact",cnt);
                            bundle.putString("otp_key", otpString);
                            otpFragment.setArguments(bundle);
                            loadFragment(otpFragment);

                        } else {
                            Toast.makeText(getApplicationContext(), "Number not registered!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("Firebase", "Database Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Database error! Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendSMS();
        } else {
            Toast.makeText(this, "Permission Denied!!", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

        otpString = String.valueOf(otp);

        return otpString;
    }

    private void sendSMS() {
        String phone = etxtcontact.getText().toString().trim();
        String message = "Your ShuttleScore login OTP is:\n " + otpString + "\n Please use this code to securely verify your identity and proceed with the login.";



        if (!phone.isEmpty()) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, message, null, null);
                Toast.makeText(this, "OTP Sent Successfully", Toast.LENGTH_SHORT).show();

                btnsendotp.setEnabled(false);
                btnsendotp.setAlpha(0.5f);

                etxtcontact.setEnabled(false);
                etxtcontact.setAlpha(0.5f);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadFragment(Fragment ff){
        FragmentManager fr = getSupportFragmentManager();
        FragmentTransaction ft = fr.beginTransaction();
        ft.replace(R.id.loginframe,ff);
        ft.commit();
    }

}