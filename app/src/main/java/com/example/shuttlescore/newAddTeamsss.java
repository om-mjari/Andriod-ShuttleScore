package com.example.shuttlescore;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class newAddTeamsss extends AppCompatActivity {

    EditText etTeamName, etCity, etCaptainNumber, etCaptainName;
    Spinner spinnerUserNames;
    Button btnDone;

    DatabaseReference teamRef, userRef;

    boolean isUpdate = false;
    String updateTeamId = null;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_teamsss);

        etTeamName = findViewById(R.id.etTeamName);
        etCity = findViewById(R.id.etCity);
        etCaptainNumber = findViewById(R.id.etCaptainNumber);
        etCaptainName = findViewById(R.id.etCaptainName);
        spinnerUserNames = findViewById(R.id.spinnerUserNames);
        btnDone = findViewById(R.id.btnDone);

        teamRef = FirebaseDatabase.getInstance().getReference("tbl_Team");
        userRef = FirebaseDatabase.getInstance().getReference("tbl_Users");

        spinnerUserNames.setVisibility(View.GONE);

        if (getIntent().getBooleanExtra("isUpdate", false)) {
            isUpdate = true;
            updateTeamId = getIntent().getStringExtra("teamId");

            etTeamName.setText(getIntent().getStringExtra("teamName"));
            etCity.setText(getIntent().getStringExtra("city"));
            etCaptainNumber.setText(getIntent().getStringExtra("captainContact"));
            etCaptainName.setText(getIntent().getStringExtra("captainName"));
        }

        etCaptainNumber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString().trim();
                if (phone.length() >= 10) {
                    fetchUserNamesByPhone(phone);
                } else {
                    spinnerUserNames.setVisibility(View.GONE);
                    etCaptainName.setText("");
                }
            }
        });

        btnDone.setOnClickListener(v -> {
            String teamName = etTeamName.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String captainNumber = etCaptainNumber.getText().toString().trim();
            String captainName = etCaptainName.getText().toString().trim();

            if (teamName.isEmpty() || city.isEmpty() || captainNumber.isEmpty()) {
                Toast.makeText(newAddTeamsss.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(phone.length() != 10){
                Toast.makeText(newAddTeamsss.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if(captainName.isEmpty()){
                Toast.makeText(newAddTeamsss.this, "Only registered players can be selected", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isUpdate) {
                teamRef.child(updateTeamId).child("teamName").setValue(teamName);
                teamRef.child(updateTeamId).child("city").setValue(city);
                teamRef.child(updateTeamId).child("captainName").setValue(captainName);
                teamRef.child(updateTeamId).child("captainContact").setValue(captainNumber);

                Toast.makeText(newAddTeamsss.this, "Team updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                String tournamentId = getIntent().getStringExtra("tournamentId");

                if (tournamentId == null || tournamentId.isEmpty()) {
                    Toast.makeText(newAddTeamsss.this, "Tournament ID is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                String teamId = teamRef.push().getKey();
                long timestamp = System.currentTimeMillis();

                tbl_team team = new tbl_team();
                team.setTeamId(teamId);
                team.setTeamName(teamName);
                team.setCity(city);
                team.setCaptainName(captainName);
                team.setCaptainContact(captainNumber);
                team.setTournamentId(tournamentId);
                team.setCreatedAt(timestamp);
                team.setDeletedAt(timestamp);

                teamRef.child(teamId).setValue(team);
                Toast.makeText(newAddTeamsss.this, "Team added successfully", Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }

    private void fetchUserNamesByPhone(String phone) {
        userRef.orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ArrayList<String> names = new ArrayList<>();
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String name = userSnap.child("name").getValue(String.class);
                            if (name != null) names.add(name);
                        }

                        if (!names.isEmpty()) {
                            spinnerUserNames.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(newAddTeamsss.this, android.R.layout.simple_spinner_dropdown_item, names);
                            spinnerUserNames.setAdapter(adapter);

                            if (names.size() == 1) {
                                etCaptainName.setText(names.get(0));
                            }
                        } else {
                            spinnerUserNames.setVisibility(View.GONE);
                            etCaptainName.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(newAddTeamsss.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
