package com.example.shuttlescore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;


import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class Schedule extends AppCompatActivity {

    TextView infoText;
    ImageView teamAIcon, teamBIcon;
    Button selectTeamA, selectTeamB,btnschedule;
    RadioButton radio7, radio11, radio21, radio1Set, radio3Sets;
    String tournamentId,selectedTeamId,selectedTeamName,selectedCaptainName,team1id,team2id,SinglesDoubles,matchType;
    String tournamentName, groundName,organizercontact;
    FirebaseHelper firebaseHelper;
    Spinner spinnerMatchType;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseHelper = new FirebaseHelper();
        tournamentId = getIntent().getStringExtra("tournamentId");
        SinglesDoubles = getIntent().getStringExtra("SinglesDoubles");
        tournamentName = getIntent().getStringExtra("tournamentName");
        groundName = getIntent().getStringExtra("groundName");
        organizercontact = getIntent().getStringExtra("organizerContactNo");


        spinnerMatchType = findViewById(R.id.spinnerMatchType);
        infoText = findViewById(R.id.infoText);
        teamAIcon = findViewById(R.id.teamAIcon);
        teamBIcon = findViewById(R.id.teamBIcon);
        selectTeamA = findViewById(R.id.selectTeamA);
        selectTeamB = findViewById(R.id.selectTeamB);
        radio7 = findViewById(R.id.radio7);
        radio11 = findViewById(R.id.radio11);
        radio21 = findViewById(R.id.radio21);
        radio1Set = findViewById(R.id.radio1Set);
        radio3Sets = findViewById(R.id.radio3Sets);
        btnschedule = findViewById(R.id.btnschedule);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Schedule.this, TournamentDetailActivity.class);
                i.putExtra("tournamentId", tournamentId);
                i.putExtra("tournamentName", tournamentName);
                i.putExtra("groundName", groundName);
                i.putExtra("organizerContactNo", organizercontact);
                startActivity(i);
            }
        });

        onclick();

    }
    public void onclick(){
        teamAIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent i = new Intent(Schedule.this, TournamentTeams.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("excludeTeamId", team2id);
                    i.putExtra("tournamentName",tournamentName);
                    i.putExtra("groundName",groundName);
                    i.putExtra("organizerContactNo",organizercontact);
                    i.putExtra("SinglesDoubles", SinglesDoubles);
                    startActivityForResult(i, 1001);
            }

        });
        teamBIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Schedule.this,TournamentTeams.class);
                i.putExtra("tournamentId",tournamentId);
                i.putExtra("tournamentName",tournamentName);
                i.putExtra("groundName",groundName);
                i.putExtra("organizerContactNo",organizercontact);
                i.putExtra("excludeTeamId", team1id);
                i.putExtra("SinglesDoubles", SinglesDoubles);
                startActivityForResult(i, 1002);
            }
        });
        btnschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (team1id == null || team2id == null) {
                    Toast.makeText(Schedule.this, "Please select both Team A and Team B", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (team1id.equals(team2id)) {
                    Toast.makeText(Schedule.this, "Team A and Team B cannot be the same", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!radio7.isChecked() && !radio11.isChecked() && !radio21.isChecked()) {
                    Toast.makeText(Schedule.this, "Please select game points (7, 11, or 21)", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!radio1Set.isChecked() && !radio3Sets.isChecked()) {
                    Toast.makeText(Schedule.this, "Please select number of sets (1 or 3)", Toast.LENGTH_SHORT).show();
                    return;
                }

                String gamePoints = radio7.isChecked() ? "7" : (radio11.isChecked() ? "11" : "21");
                String matchformate = radio1Set.isChecked() ? "1" : "3";
                String scheduleTime = String.valueOf(System.currentTimeMillis());
                String status = "Scheduled";
                String gameMode = "Singles";
                if (SinglesDoubles.equalsIgnoreCase("Doubles")) {
                    gameMode = "Doubles";
                }

                matchType = spinnerMatchType.getSelectedItem().toString();

                firebaseHelper.addtournamentMatch(
                        tournamentId,
                        scheduleTime,
                        gameMode,
                        gamePoints,
                        team1id,
                        team2id,
                        matchformate,
                        matchType,
                        status,
                        new FirebaseHelper.MatchInsertCallback() {
                            @Override
                            public void onMatchInserted(String matchId) {
                                if (matchId != null) {
                                    Toast.makeText(Schedule.this, "Match scheduled successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Schedule.this, TournamentDetailActivity.class);
                                    intent.putExtra("tournamentId", tournamentId);
                                    intent.putExtra("tournamentName", tournamentName);
                                    intent.putExtra("groundName", groundName);
                                    intent.putExtra("organizerContactNo", organizercontact);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Schedule.this, "Failed to schedule match", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        );

            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            selectedTeamId = data.getStringExtra("selectedTeamId");
            selectedTeamName = data.getStringExtra("selectedTeamName");

            if (SinglesDoubles.equalsIgnoreCase("Singles")) {
                selectedCaptainName = data.getStringExtra("selectedCaptainName");
            } else if (SinglesDoubles.equalsIgnoreCase("Doubles")) {
                String captain1 = data.getStringExtra("selectedCaptain1Name");
                String captain2 = data.getStringExtra("selectedCaptain2Name");
                selectedCaptainName = captain1 + " & " + captain2;
            }

            if (requestCode == 1001) {
                selectTeamA.setText(selectedTeamName);
                team1id = selectedTeamId;
            } else if (requestCode == 1002) {
                selectTeamB.setText(selectedTeamName);
                team2id = selectedTeamId;
            }
        }
    }



}
