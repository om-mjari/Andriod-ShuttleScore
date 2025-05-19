package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TournamentTeams extends AppCompatActivity {

    String tournamentId,excludeTeamId,SinglesDoubles,tournamentName,groundName,organizercontact;
    Button btndone;
    ListView lstteam;
    ArrayList<tbl_team> teamList;
    TeamAdapter teamAdapter;
    private DoubleTeamAdapter doubleAdapter;
    private DatabaseReference teamRef;
    private List<tbl_double_team> doubleTeamList;

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_teams);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });


        tournamentId = getIntent().getStringExtra("tournamentId");
        excludeTeamId = getIntent().getStringExtra("excludeTeamId");
        SinglesDoubles = getIntent().getStringExtra("SinglesDoubles");
        tournamentName = getIntent().getStringExtra("tournamentName");
        groundName = getIntent().getStringExtra("groundName");
        organizercontact = getIntent().getStringExtra("organizerContactNo");


        lstteam = findViewById(R.id.lstteam);
        btndone = findViewById(R.id.btndone);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TournamentTeams.this,Schedule.class);
                i.putExtra("tournamentId",tournamentId);
                i.putExtra("SinglesDoubles",SinglesDoubles);
                i.putExtra("excludeTeamId",excludeTeamId);
                i.putExtra("tournamentName",tournamentName);
                i.putExtra("groundName",groundName);
                i.putExtra("organizerContactNo",organizercontact);
                startActivity(i);
            }
        });

        teamList = new ArrayList<>();
        doubleTeamList = new ArrayList<>();
        teamAdapter = new TeamAdapter(this, teamList, true);
        lstteam.setAdapter(teamAdapter);

        loadTeams();

        btndone.setOnClickListener(v -> {
            if (SinglesDoubles.equalsIgnoreCase("Singles")) {
                int selectedPosition = teamAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    tbl_team selectedTeam = teamList.get(selectedPosition);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedTeamId", selectedTeam.getTeamId());
                    resultIntent.putExtra("selectedTeamName", selectedTeam.getTeamName());
                    resultIntent.putExtra("selectedCaptainName", selectedTeam.getCaptainName());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(TournamentTeams.this, "Please select a team", Toast.LENGTH_SHORT).show();
                }
            }
            else if (SinglesDoubles.equalsIgnoreCase("Doubles")) {
                int selectedPosition = doubleAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    tbl_double_team selectedTeam = doubleTeamList.get(selectedPosition);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedTeamId", selectedTeam.getTeamId());
                    resultIntent.putExtra("selectedTeamName", selectedTeam.getTeamName());
                    resultIntent.putExtra("selectedCaptain1Name", selectedTeam.getCaptain1Name());
                    resultIntent.putExtra("selectedCaptain2Name", selectedTeam.getCaptain2Name());
                    setResult(RESULT_OK, resultIntent);
                    String teamId = selectedTeam.getTeamId();
                    if (teamId != null && !teamId.isEmpty()) {
                        Toast.makeText(this, teamId, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No team selected", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    Toast.makeText(TournamentTeams.this, "Please select a team", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadTeams() {
        if (SinglesDoubles != null) {
            if (SinglesDoubles.equalsIgnoreCase("Singles")) {
                teamRef = FirebaseDatabase.getInstance().getReference("tbl_Team");
                teamRef.orderByChild("tournamentId").equalTo(tournamentId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                teamList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    tbl_team team = dataSnapshot.getValue(tbl_team.class);
                                    if (team != null && !team.getTeamId().equals(excludeTeamId)) {
                                        teamList.add(team);
                                    }
                                }
                                teamAdapter = new TeamAdapter(TournamentTeams.this, teamList, true); // Use teamAdapter here
                                lstteam.setAdapter(teamAdapter);
                                teamAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Failed to load teams", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (SinglesDoubles.equalsIgnoreCase("Doubles")) {
                teamRef = FirebaseDatabase.getInstance().getReference("tbl_Double_Team");
                teamRef.orderByChild("tournamentId").equalTo(tournamentId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                doubleTeamList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    tbl_double_team doubleTeam = dataSnapshot.getValue(tbl_double_team.class);
                                    if (doubleTeam != null && !doubleTeam.getTeamId().equals(excludeTeamId)) {
                                        doubleTeamList.add(doubleTeam);
                                    }
                                }
                                doubleAdapter = new DoubleTeamAdapter(TournamentTeams.this, doubleTeamList);
                                lstteam.setAdapter(doubleAdapter);
                                doubleAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "Failed to load double teams", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(getApplicationContext(), "Tournament type is not yet available", Toast.LENGTH_SHORT).show();
        }
    }


}

