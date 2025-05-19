package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSponsors extends AppCompatActivity {
    private static final String TAG = "AddSponsors";

    private EditText sponsorNameEditText, sponsorContactNoEditText, sponsorDescriptionEditText;
    private Button addSponsorButton;
    private ListView sponsorsListView;
    private ImageView back;

    private String tournamentId;
    private DatabaseReference sponsorsDatabase;
    public ArrayList<tbl_Sponsor> sponsorsList;
    private SponsorAdapter sponsorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_sponsors);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });

        initViews();
        loadIntentData();
        setupListeners();


        sponsorsList = new ArrayList<>();
        sponsorAdapter = new SponsorAdapter(this, sponsorsList);
        sponsorsListView.setAdapter(sponsorAdapter);

        sponsorsDatabase = FirebaseDatabase.getInstance().getReference("tbl_Sponsor");
        fetchSponsors();
    }

    private void initViews() {
        sponsorNameEditText = findViewById(R.id.sponsorName);
        sponsorContactNoEditText = findViewById(R.id.contactNo);
        sponsorDescriptionEditText = findViewById(R.id.description);
        addSponsorButton = findViewById(R.id.addSponsorButton);
        sponsorsListView = findViewById(R.id.listViewAddedSponsors);
        back = findViewById(R.id.back);
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        tournamentId = intent.getStringExtra("tournamentId");
        if (tournamentId == null) {
            Toast.makeText(this, "No tournament selected", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupListeners() {
        back.setOnClickListener(v -> {
            Intent intent = getIntent();
            Intent i = new Intent(getApplicationContext(), TournamentDetailActivity.class);
            i.putExtra("tournamentId", intent.getStringExtra("tournamentId"));
            i.putExtra("tournamentName", intent.getStringExtra("tournamentName"));
            i.putExtra("groundName", intent.getStringExtra("groundName"));
            i.putExtra("cityName", intent.getStringExtra("cityName"));
            i.putExtra("organizerContactNo", intent.getStringExtra("organizerContactNo"));
            startActivity(i);
        });

        addSponsorButton.setOnClickListener(v -> addSponsor());
    }

    private void fetchSponsors() {
        sponsorsDatabase.orderByChild("tournamentId").equalTo(tournamentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        sponsorsList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String id = child.getKey();
                            String name = child.child("sponsorName").getValue(String.class);
                            String phone = child.child("sponsorContactNo").getValue(String.class);
                            String desc = child.child("sponsorDescription").getValue(String.class);
                            if (name != null && phone != null) {
                                if (desc == null) desc = "";
                                tbl_Sponsor sponsor = new tbl_Sponsor(id, name, phone, desc, tournamentId);
                                sponsorsList.add(sponsor);
                            }
                        }
                        sponsorAdapter.notifyDataSetChanged();
                        sponsorsListView.setVisibility(sponsorsList.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error fetching sponsors", error.toException());
                        Toast.makeText(AddSponsors.this, "Error fetching sponsors", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addSponsor() {
        String name = sponsorNameEditText.getText().toString().trim();
        String phone = sponsorContactNoEditText.getText().toString().trim();
        String desc = sponsorDescriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter both sponsor name and contact number.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(sponsorContactNoEditText.length()<10){
            Toast.makeText(getApplicationContext(),"Enter a valid contact number",Toast.LENGTH_SHORT).show();
            return;
        }
        sponsorsDatabase.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String existingPhone = child.child("sponsorContactNo").getValue(String.class);
                            if (phone.equals(existingPhone)) {
                                Toast.makeText(AddSponsors.this, "This contact number is already used.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        String sponsorId = sponsorsDatabase.push().getKey();
                        if (sponsorId == null) {
                            Toast.makeText(AddSponsors.this, "Error generating sponsor ID.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> sponsorData = new HashMap<>();
                        sponsorData.put("sponsorId", sponsorId);
                        sponsorData.put("sponsorName", name);
                        sponsorData.put("sponsorContactNo", phone);
                        sponsorData.put("sponsorDescription", desc);
                        sponsorData.put("tournamentId", tournamentId);

                        sponsorsDatabase.child(sponsorId).setValue(sponsorData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AddSponsors.this, "Sponsor added!", Toast.LENGTH_SHORT).show();
                                    sponsorNameEditText.setText("");
                                    sponsorContactNoEditText.setText("");
                                    sponsorDescriptionEditText.setText("");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to add sponsor", e);
                                    Toast.makeText(AddSponsors.this, "Failed to add sponsor.", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Sponsor check cancelled", error.toException());
                        Toast.makeText(AddSponsors.this, "Database error.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
