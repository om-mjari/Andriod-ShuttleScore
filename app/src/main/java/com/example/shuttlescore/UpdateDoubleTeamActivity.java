package com.example.shuttlescore;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class UpdateDoubleTeamActivity extends AppCompatActivity {

    EditText edtTeamName, edtCity, edtCaptain1Name, edtCaptain1Number, edtCaptain2Name, edtCaptain2Number;
    Button btnUpdate;
    DatabaseReference userRef, teamRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_double_team);

        edtTeamName = findViewById(R.id.edtTeamName);
        edtCity = findViewById(R.id.edtCity);
        edtCaptain1Name = findViewById(R.id.edtCaptain1Name);
        edtCaptain1Number = findViewById(R.id.edtCaptain1Number);
        edtCaptain2Name = findViewById(R.id.edtCaptain2Name);
        edtCaptain2Number = findViewById(R.id.edtCaptain2Number);
        btnUpdate = findViewById(R.id.btnUpdate);

        userRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        String teamKey = getIntent().getStringExtra("teamKey");
        teamRef = FirebaseDatabase.getInstance().getReference("tbl_Double_Team").child(teamKey);

        // Fetch existing team data
        teamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    edtTeamName.setText(snapshot.child("teamName").getValue(String.class));
                    edtCity.setText(snapshot.child("city").getValue(String.class));
                    edtCaptain1Name.setText(snapshot.child("captain1Name").getValue(String.class));
                    edtCaptain1Number.setText(snapshot.child("captain1Contact").getValue(String.class));
                    edtCaptain2Name.setText(snapshot.child("captain2Name").getValue(String.class));
                    edtCaptain2Number.setText(snapshot.child("captain2Contact").getValue(String.class));
                } else {
                    Toast.makeText(UpdateDoubleTeamActivity.this, "Team not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UpdateDoubleTeamActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        edtCaptain1Number.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();
                if (phone.length() >= 10) {
                    fetchNameFromPhone(phone, edtCaptain1Name);
                } else {
                    edtCaptain1Name.setText("");
                }
            }
        });

        edtCaptain2Number.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String phone = s.toString().trim();
                if (phone.length() >= 10) {
                    fetchNameFromPhone(phone, edtCaptain2Name);
                } else {
                    edtCaptain2Name.setText("");
                }
            }
        });

        btnUpdate.setOnClickListener(v -> {
            String updatedTeamName = edtTeamName.getText().toString().trim();
            String updatedCity = edtCity.getText().toString().trim();
            String updatedCap1Number = edtCaptain1Number.getText().toString().trim();
            String updatedCap1Name = edtCaptain1Name.getText().toString().trim();
            String updatedCap2Number = edtCaptain2Number.getText().toString().trim();
            String updatedCap2Name = edtCaptain2Name.getText().toString().trim();

            if (updatedTeamName.isEmpty() || updatedCity.isEmpty() ||
                    updatedCap1Number.isEmpty() ||
                    updatedCap2Number.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(updatedCap1Number.length() != 10 || updatedCap2Number.length() != 10){
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if(updatedCap1Number.equals(updatedCap2Number)){
                Toast.makeText(this, "Both players cannot be same", Toast.LENGTH_SHORT).show();
                return;
            }
            if(updatedCap1Name.isEmpty() || updatedCap2Name.isEmpty()){
                Toast.makeText(this, "Only registered players can be selected", Toast.LENGTH_SHORT).show();
                return;
            }
            teamRef.child("teamName").setValue(updatedTeamName);
            teamRef.child("city").setValue(updatedCity);
            teamRef.child("captain1Name").setValue(updatedCap1Name);
            teamRef.child("captain1Contact").setValue(updatedCap1Number);
            teamRef.child("captain2Name").setValue(updatedCap2Name);
            teamRef.child("captain2Contact").setValue(updatedCap2Number);

            Toast.makeText(this, "Team updated successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UpdateDoubleTeamActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
