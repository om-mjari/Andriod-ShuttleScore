package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MatchDetailActivity extends AppCompatActivity {

    ImageView imgCoin,back;
    TextView txtResult;
    private final Random random = new Random();
    private final int[] coinImages = {R.drawable.head, R.drawable.tail};
    TextView txtGamePoints, txtGameSets,selectTeamB,selectTeamA;
    RadioGroup rgGamePoints, rgGameSets;
    RadioButton rb7, rb11, rb21, rbSet1, rbSet3;
    DatabaseReference matchRef;
    String gamePoints, matchFormat,matchId,team1Name,team2Name,selectedMatchType,matchType;
    Button btnStartMatch;
    Spinner spinnerMatchType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });


        spinnerMatchType = findViewById(R.id.spinnerMatchType);
        txtGamePoints = findViewById(R.id.txtGamePoints);
        txtGameSets = findViewById(R.id.txtGameSets);
        rgGamePoints = findViewById(R.id.rgGamePoints);
        rgGameSets = findViewById(R.id.rgGameSets);
        rb7 = findViewById(R.id.rb7);
        rb11 = findViewById(R.id.rb11);
        rb21 = findViewById(R.id.rb21);
        rbSet1 = findViewById(R.id.rbSet1);
        rbSet3 = findViewById(R.id.rbSet3);
        selectTeamA = findViewById(R.id.selectTeamA);
        selectTeamB = findViewById(R.id.selectTeamB);
        btnStartMatch = findViewById(R.id.btnStartMatch);
        back = findViewById(R.id.back);
        imgCoin = findViewById(R.id.imgCoin);
        txtResult = findViewById(R.id.txtResult);

        imgCoin.setOnClickListener(v -> flipCoin());

        matchId = getIntent().getStringExtra("matchId");
        team1Name = getIntent().getStringExtra("team1Name");
        team2Name = getIntent().getStringExtra("team2Name");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.match_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatchType.setAdapter(adapter);

        matchRef = FirebaseDatabase.getInstance().getReference("tbl_tournament_matches").child(matchId);

        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String tournamentId = snapshot.child("tournamentId").getValue(String.class);
                    String gameMode = snapshot.child("gameMode").getValue(String.class);
                    gamePoints = snapshot.child("gamePoints").getValue(String.class);
                    matchFormat = snapshot.child("matchFormat").getValue(String.class);
                    String matchStatus = snapshot.child("matchStatus").getValue(String.class);
                    String scheduleTime = snapshot.child("scheduleTime").getValue(String.class);
                    matchType = snapshot.child("matchtype").getValue(String.class);


                    selectTeamA.setText(team1Name);
                    selectTeamB.setText(team2Name);

                    if (gamePoints != null) {
                        if (gamePoints.equals("7")) {
                            rb7.setChecked(true);
                        } else if (gamePoints.equals("11")) {
                            rb11.setChecked(true);
                        } else if (gamePoints.equals("21")) {
                            rb21.setChecked(true);
                        }
                    }
                    if (matchFormat != null) {
                        if (matchFormat.equals("1")) {
                            rbSet1.setChecked(true);
                        } else if (matchFormat.equals("3")) {
                            rbSet3.setChecked(true);
                        }
                    }

                    onclick();

                }
                if (matchType != null) {
                    spinnerMatchType.post(() -> {
                        int position = adapter.getPosition(matchType);
                        if (position >= 0) {
                            spinnerMatchType.setSelection(position);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void flipCoin() {
        txtResult.setText("Tossing...");
        imgCoin.setEnabled(false);

        new Handler().postDelayed(() -> {
            int toss = random.nextInt(2);
            imgCoin.setImageResource(coinImages[toss]);
            txtResult.setText(toss == 0 ? "Heads" : "Tails");

            new Handler().postDelayed(() -> {
                imgCoin.setImageResource(R.drawable.coin);
                txtResult.setText("Tap coin to toss");
                imgCoin.setEnabled(true);
            }, 2000);

        }, 1000);
    }

    public void onclick(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                String matchId = i.getStringExtra("matchId");
                String tournamentId = i.getStringExtra("tournamentId");
                String tournamentName = i.getStringExtra("tournamentName");
                String groundName = i.getStringExtra("groundName");
                String organizerContactNo = i.getStringExtra("organizerContactNo");
                String team1Name = i.getStringExtra("team1Name");
                String team2Name = i.getStringExtra("team2Name");
                Intent intent = new Intent(MatchDetailActivity.this,TournamentDetailActivity.class);
                intent.putExtra("matchId",matchId);
                intent.putExtra("tournamentId",tournamentId);
                intent.putExtra("tournamentName",tournamentName);
                intent.putExtra("groundName",groundName);
                intent.putExtra("organizerContactNo",organizerContactNo);
                intent.putExtra("team1Name",team1Name);
                intent.putExtra("team2Name",team2Name);
                startActivity(intent);

            }
        });
        btnStartMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rb7.isChecked()) {
                    gamePoints = "7";
                } else if (rb11.isChecked()) {
                    gamePoints = "11";
                } else if (rb21.isChecked()) {
                    gamePoints = "21";
                }

                if (rbSet1.isChecked()) {
                    matchFormat = "1";
                } else if (rbSet3.isChecked()) {
                    matchFormat = "3";
                }
                selectedMatchType = spinnerMatchType.getSelectedItem().toString();

                matchRef.child("matchtype").setValue(selectedMatchType);
                matchRef.child("gamePoints").setValue(gamePoints);
                matchRef.child("matchFormat").setValue(matchFormat);

                Intent intent = new Intent(MatchDetailActivity.this,tournamentscoring.class);
                intent.putExtra("matchId",matchId);
                intent.putExtra("team1Name",team1Name);
                intent.putExtra("team2Name",team2Name);
                intent.putExtra("gamePoints",gamePoints);
                intent.putExtra("matchFormat",matchFormat);
                intent.putExtra("tournamentId",getIntent().getStringExtra("tournamentId"));
                intent.putExtra("tournamentName",getIntent().getStringExtra("tournamentName"));
                intent.putExtra("organizerContactNo",getIntent().getStringExtra("organizerContactNo"));
                intent.putExtra("groundName",getIntent().getStringExtra("groundName"));
                matchRef.child("matchStatus").setValue("Live");
                startActivity(intent);
            }
        });
    }
}
