package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RemoveSponsors extends AppCompatActivity {

    ImageView back;
    ListView listViewRemoveSponsors;
    MaterialButton removeSponsorsButton;

    private ArrayList<tbl_Sponsor> addedSponsorsList;
    private ArrayAdapter<tbl_Sponsor> addedSponsorsAdapter;
    private int selectedPosition = -1;

    String organizerContactNo, tournamentName, groundName, tournamentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_sponsors);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });

        Intent i = getIntent();
        organizerContactNo = i.getStringExtra("organizerContactNo");
        tournamentName = i.getStringExtra("tournamentName");
        groundName = i.getStringExtra("groundName");
        tournamentId = i.getStringExtra("tournamentId");

        // Bind views
        back = findViewById(R.id.back);
        listViewRemoveSponsors = findViewById(R.id.listViewRemoveSponsors);
        removeSponsorsButton = findViewById(R.id.removeSponsorsButton);

        back.setOnClickListener(v -> goBack());

        addedSponsorsList = new ArrayList<>();
        addedSponsorsAdapter = new ArrayAdapter<tbl_Sponsor>(this, R.layout.list_item_sponsor, addedSponsorsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView != null ? convertView : getLayoutInflater().inflate(R.layout.list_item_sponsor, parent, false);

                TextView sponsorNameText = row.findViewById(R.id.sponsorNameText);
                TextView sponsorContactText = row.findViewById(R.id.sponsorContactText);
                TextView sponsorDescriptionText = row.findViewById(R.id.sponsorDescriptionText);
                ImageView iv = row.findViewById(R.id.removeIcon);
                View container = row.findViewById(R.id.itemContainer);

                tbl_Sponsor sponsor = addedSponsorsList.get(position);
                sponsorNameText.setText(sponsor.getSponsorName());
                sponsorContactText.setText(sponsor.getSponsorContactNo());
                sponsorDescriptionText.setText(sponsor.getSponsorDescription() != null ? sponsor.getSponsorDescription() : "");

                if (position == selectedPosition) {
                    container.setBackgroundResource(R.drawable.item_selected_background);
                    iv.setVisibility(View.VISIBLE);
                } else {
                    container.setBackgroundResource(R.drawable.item_default_background);
                    iv.setVisibility(View.GONE);
                }

                return row;
            }
        };

        listViewRemoveSponsors.setAdapter(addedSponsorsAdapter);

        fetchSponsors(tournamentId);

        listViewRemoveSponsors.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            addedSponsorsAdapter.notifyDataSetChanged();
        });

        removeSponsorsButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                tbl_Sponsor sponsor = addedSponsorsList.get(selectedPosition);
                FirebaseHelper.removeSponsorByPhone(sponsor.getSponsorContactNo(), tournamentId, new FirebaseHelper.FirebaseCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(RemoveSponsors.this, "Sponsor removed successfully", Toast.LENGTH_SHORT).show();
                        addedSponsorsList.remove(selectedPosition);
                        selectedPosition = -1;
                        addedSponsorsAdapter.notifyDataSetChanged();
                        goBack();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(RemoveSponsors.this, "Error removing sponsor: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(RemoveSponsors.this, "Please select a sponsor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSponsors(String tournamentId) {
        FirebaseDatabase.getInstance().getReference("tbl_Sponsor")
                .orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        addedSponsorsList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            tbl_Sponsor sponsor = snap.getValue(tbl_Sponsor.class);
                            if (sponsor != null) {
                                addedSponsorsList.add(sponsor);
                            }
                        }
                        addedSponsorsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RemoveSponsors.this, "Error fetching sponsors", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goBack() {
        Intent intent = new Intent(getApplicationContext(), TournamentDetailActivity.class);
        intent.putExtra("tournamentId", tournamentId);
        intent.putExtra("tournamentName", tournamentName);
        intent.putExtra("groundName", groundName);
        intent.putExtra("organizerContactNo", organizerContactNo);
        startActivity(intent);
    }
}
