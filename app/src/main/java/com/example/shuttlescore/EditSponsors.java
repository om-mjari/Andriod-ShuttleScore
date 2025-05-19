package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditSponsors extends AppCompatActivity {

    ImageView back;
    ListView listViewEditSponsors;
    MaterialButton EditSponsorsButton;
    private ArrayList<tbl_Sponsor> addedSponsorsList;
    private ArrayAdapter<tbl_Sponsor> addedSponsorsAdapter;
    private int selectedPosition = -1;

    String organizerContactNo, tournamentName, groundName, tournamentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sponsors);
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

        back = findViewById(R.id.back);
        listViewEditSponsors = findViewById(R.id.listViewEditSponsors);
        EditSponsorsButton = findViewById(R.id.EditSponsorsButton);

        back.setOnClickListener(v -> goBack());

        addedSponsorsList = new ArrayList<>();
        addedSponsorsAdapter = new ArrayAdapter<tbl_Sponsor>(this, R.layout.list_item_sponsor, addedSponsorsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView != null ? convertView : getLayoutInflater().inflate(R.layout.list_item_sponsor, parent, false);

                TextView sponsorNameText = row.findViewById(R.id.sponsorNameText);
                TextView sponsorContactText = row.findViewById(R.id.sponsorContactText);
                TextView sponsorDescriptionText = row.findViewById(R.id.sponsorDescriptionText);
                ImageView iv = row.findViewById(R.id.editIcon);
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

        listViewEditSponsors.setAdapter(addedSponsorsAdapter);

        fetchSponsors(tournamentId);

        listViewEditSponsors.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            addedSponsorsAdapter.notifyDataSetChanged();
        });

        EditSponsorsButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                tbl_Sponsor selectedSponsor = addedSponsorsList.get(selectedPosition);
                showEditDialog(selectedSponsor);
            } else {
                Toast.makeText(EditSponsors.this, "Please select a sponsor to edit", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditSponsors.this, "Error fetching sponsors", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditDialog(tbl_Sponsor sponsor) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_sponsor, null);
        EditText sponsorNameEdit = dialogView.findViewById(R.id.sponsorNameEdit);
        EditText sponsorContactEdit = dialogView.findViewById(R.id.sponsorContactEdit);
        EditText sponsorDescriptionEdit = dialogView.findViewById(R.id.sponsorDescriptionEdit);

        sponsorNameEdit.setText(sponsor.getSponsorName());
        sponsorContactEdit.setText(sponsor.getSponsorContactNo());
        sponsorDescriptionEdit.setText(sponsor.getSponsorDescription() != null ? sponsor.getSponsorDescription() : "");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Sponsor");
        builder.setView(dialogView);
        builder.setPositiveButton("Edit", null); // Don't assign listener yet

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            boolean isValid = true;

            if (sponsorNameEdit.getText().toString().isEmpty()) {
                sponsorNameEdit.setError("Sponsor name is required");
                sponsorNameEdit.requestFocus();
                isValid = false;
            }

            if (sponsorContactEdit.getText().toString().isEmpty()) {
                sponsorContactEdit.setError("Contact number is required");
                sponsorContactEdit.requestFocus();
                isValid = false;
            } else if (sponsorContactEdit.getText().toString().length() != 10) {
                sponsorContactEdit.setError("Invalid contact number");
                sponsorContactEdit.requestFocus();
                isValid = false;
            }

            if (sponsorDescriptionEdit.getText().toString().isEmpty()) {
                sponsorDescriptionEdit.setError("Description is required");
                sponsorDescriptionEdit.requestFocus();
                isValid = false;
            }

            if (isValid) {
                sponsor.setSponsorName(sponsorNameEdit.getText().toString());
                sponsor.setSponsorContactNo(sponsorContactEdit.getText().toString());
                sponsor.setSponsorDescription(sponsorDescriptionEdit.getText().toString());

                updateSponsor(sponsor);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss());
        dialog.show();

    }

    private void updateSponsor(tbl_Sponsor sponsor) {
        FirebaseDatabase.getInstance().getReference("tbl_Sponsor")
                .child(sponsor.getSponsorId())
                .setValue(sponsor)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditSponsors.this, "Sponsor details updated", Toast.LENGTH_SHORT).show();
                        fetchSponsors(tournamentId);
                    } else {
                        Toast.makeText(EditSponsors.this, "Error updating sponsor", Toast.LENGTH_SHORT).show();
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
