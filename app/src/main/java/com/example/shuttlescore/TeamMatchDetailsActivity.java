package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TeamMatchDetailsActivity extends AppCompatActivity {

    private TextView teamNameTop, teamNameBottom;
    private TextView team1Score, team2Score;
    private TextView team1Set, team2Set, team3Set;
    private TextView tvWinner, tvMatchStatus;
    private LinearLayout set2Container, set3Container, winnerContainer;
    private ImageView back;
    private String matchId, tournamentId, singlesDoubles;
    private String team1Id, team2Id;
    private static final String TAG = "TeamMatchDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });

        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_match_details);

        initializeViews();
        getIntentData();
        setupInitialVisibility();
        fetchMatchDetails();
        setupBackButton();
    }

    private void initializeViews() {
        teamNameTop = findViewById(R.id.teamNameTop);
        teamNameBottom = findViewById(R.id.teamNameBottom);
        team1Score = findViewById(R.id.team1Score);
        team2Score = findViewById(R.id.team2Score);
        team1Set = findViewById(R.id.team1Set);
        team2Set = findViewById(R.id.team2Set);
        team3Set = findViewById(R.id.team3Set);
        set2Container = findViewById(R.id.set2Container);
        set3Container = findViewById(R.id.set3Container);
        tvWinner = findViewById(R.id.tvWinner);
        winnerContainer = findViewById(R.id.winnerContainer);
        tvMatchStatus = findViewById(R.id.tvMatchStatus);
        back = findViewById(R.id.back);
    }

    private void getIntentData() {
        matchId = getIntent().getStringExtra("matchId");
        tournamentId = getIntent().getStringExtra("tournamentId");
        singlesDoubles = getIntent().getStringExtra("singlesDoubles");

        Log.d(TAG, "Match ID: " + matchId);
        Log.d(TAG, "Tournament ID: " + tournamentId);
        Log.d(TAG, "Type: " + singlesDoubles);
    }

    private void setupInitialVisibility() {
        set2Container.setVisibility(View.GONE);
        set3Container.setVisibility(View.GONE);
        winnerContainer.setVisibility(View.GONE);
    }

    private void setupBackButton() {
        back.setOnClickListener(v -> {
            Intent intent = new Intent(TeamMatchDetailsActivity.this, TournamentDetailActivity.class);
            intent.putExtra("fragmentToLoad", "LookingFragment");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void fetchMatchDetails() {
        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("tbl_tournament_matches").child(matchId);

        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    MatchModel match = snapshot.getValue(MatchModel.class);
                    if (match != null) {
                        team1Id = match.getTeam1id();
                        team2Id = match.getTeam2id();
                        tvMatchStatus.setText("Status: " + match.getMatchStatus());
                        fetchTeamNames();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to load match details: " + error.getMessage());
            }
        });
    }

    private void fetchTeamNames() {
        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference("tbl_Team");

        teamRef.child(team1Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("teamName").getValue(String.class);
                teamNameTop.setText(name != null ? name : "Team 1");
                fetchLiveScore();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                teamNameTop.setText("Team 1");
                fetchLiveScore();
            }
        });

        teamRef.child(team2Id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = snapshot.child("teamName").getValue(String.class);
                teamNameBottom.setText(name != null ? name : "Team 2");
                fetchLiveScore();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                teamNameBottom.setText("Team 2");
                fetchLiveScore();
            }
        });
    }

    private void fetchLiveScore() {
        DatabaseReference liveScoreRef = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score");
        Query query = liveScoreRef.orderByChild("matchId").equalTo(matchId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    setDefaultScores();
                    return;
                }

                for (DataSnapshot scoreSnap : snapshot.getChildren()) {
                    tbl_live_score score = scoreSnap.getValue(tbl_live_score.class);
                    if (score != null) {
                        updateScores(score);
                        updateSets(score);
                        determineWinnerLive(); // Determine winner without storing
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Live score fetch failed: " + error.getMessage());
                setDefaultScores();
            }
        });
    }

    private void setDefaultScores() {
        team1Score.setText("0");
        team2Score.setText("0");
        team1Set.setText("0-0");
        team2Set.setText("");
        team3Set.setText("");
    }

    private void updateScores(tbl_live_score score) {
        team1Score.setText(String.valueOf(score.getTeam1Score()));
        team2Score.setText(String.valueOf(score.getTeam2Score()));
    }

    private void updateSets(tbl_live_score score) {
        team1Set.setText(score.getTeam1Set1() + "-" + score.getTeam2Set1());
    }

    private void determineWinnerLive() {
        int team1ScoreValue = Integer.parseInt(team1Score.getText().toString());
        int team2ScoreValue = Integer.parseInt(team2Score.getText().toString());

        if (team1ScoreValue > team2ScoreValue) {
            tvWinner.setText("Winner: " + teamNameTop.getText().toString());
            winnerContainer.setVisibility(View.VISIBLE);
        } else if (team2ScoreValue > team1ScoreValue) {
            tvWinner.setText("Winner: " + teamNameBottom.getText().toString());
            winnerContainer.setVisibility(View.VISIBLE);
        } else {
            winnerContainer.setVisibility(View.GONE);
        }
    }
}