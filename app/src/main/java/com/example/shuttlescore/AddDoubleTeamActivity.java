package com.example.shuttlescore;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class AddDoubleTeamActivity extends AppCompatActivity {

    EditText etTeamName, etCity, etCaptain1Number, etCaptain1Name, etCaptain2Number, etCaptain2Name;
    Spinner spinnerCaptain1, spinnerCaptain2;
    Button btnDone;
    String phone;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_double_team);

        etTeamName = findViewById(R.id.etTeamName);
        etCity = findViewById(R.id.etCity);
        etCaptain1Number = findViewById(R.id.etCaptain1Number);
        etCaptain1Name = findViewById(R.id.etCaptain1Name);
        etCaptain2Number = findViewById(R.id.etCaptain2Number);
        etCaptain2Name = findViewById(R.id.etCaptain2Name);
        spinnerCaptain1 = new Spinner(this);
        spinnerCaptain2 = new Spinner(this);
        btnDone = findViewById(R.id.btnDone);

        userRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        etCaptain1Number.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString().trim();
                if (phone.length() >= 10) {
                    fetchNameFromPhone(phone, etCaptain1Name);
                } else {
                    etCaptain1Name.setText("");
                }
            }
        });

        etCaptain2Number.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString().trim();
                if (phone.length() >= 10) {
                    fetchNameFromPhone(phone, etCaptain2Name);
                } else {
                    etCaptain2Name.setText("");
                }
            }
        });

        btnDone.setOnClickListener(v -> {
            String teamName = etTeamName.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String cap1Number = etCaptain1Number.getText().toString().trim();
            String cap1Name = etCaptain1Name.getText().toString().trim();
            String cap2Number = etCaptain2Number.getText().toString().trim();
            String cap2Name = etCaptain2Name.getText().toString().trim();
            String tournamentid = getIntent().getStringExtra("tournamentId");

            if (teamName.isEmpty() || city.isEmpty() || cap1Number.isEmpty()
                    || cap2Number.isEmpty() ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(phone.length() != 10){
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if(cap1Number.equals(cap2Number)){
                Toast.makeText(this, "Both players cannot be same", Toast.LENGTH_SHORT).show();
                return;
            }
            if(cap1Name.isEmpty() || cap2Name.isEmpty()){
                Toast.makeText(this, "Only registered players can be selected", Toast.LENGTH_SHORT).show();
                return;
            }

            long timestamp = System.currentTimeMillis();
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.addDoubleTeam(teamName, city, cap1Name, cap1Number, cap2Name, cap2Number, timestamp, null,tournamentid);

            Toast.makeText(this, "Double Team Added Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void fetchNameFromPhone(String phone, EditText nameEditText) {
        userRef.orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                String name = userSnap.child("name").getValue(String.class);
                                nameEditText.setText(name != null ? name : "Name not found");
                                break;
                            }
                        } else {
                            nameEditText.setHint("Not Registered");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(AddDoubleTeamActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
