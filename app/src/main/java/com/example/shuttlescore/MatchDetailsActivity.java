package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.*;

public class MatchDetailsActivity extends AppCompatActivity {

    // UI components
    private TextView playerNameTop, playerNameBottom;
    private TextView team1Score, team2Score;
    private TextView team1Set, team2Set, team3Set;
    private TextView tvWinner;
    private TextView tvMatchStatus; // Added for status display
    private LinearLayout set2Container, set3Container;

    // Data variables
    private String matchId, player1, player2, player3, player4, matchStatus;
    ImageView back;
    private int playersToFetch = 0;
    private static final String TAG = "MatchDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_match_details);

        initializeViews();
        getIntentData();
        setupInitialVisibility();
        fetchPlayerNames();
        setupBackButton();
    }

    private void setupBackButton() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initializeViews() {
        playerNameTop = findViewById(R.id.playerNameTop);
        playerNameBottom = findViewById(R.id.playerNameBottom);
        team1Score = findViewById(R.id.team1Score);
        team2Score = findViewById(R.id.team2Score);
        team1Set = findViewById(R.id.team1Set);
        team2Set = findViewById(R.id.team2Set);
        team3Set = findViewById(R.id.team3Set);
        set2Container = findViewById(R.id.set2Container);
        set3Container = findViewById(R.id.set3Container);
        tvWinner = findViewById(R.id.tvWinner);
        tvMatchStatus = findViewById(R.id.tvMatchStatus);
        back = findViewById(R.id.back);
    }

    private void getIntentData() {
        matchId = getIntent().getStringExtra("matchId");
        player1 = getIntent().getStringExtra("player1");
        player2 = getIntent().getStringExtra("player2");
        player3 = getIntent().getStringExtra("player3");
        player4 = getIntent().getStringExtra("player4");
        matchStatus = getIntent().getStringExtra("Match_Status");

        // Update status TextView immediately
        if (matchStatus != null) {
            tvMatchStatus.setText("Status: " + matchStatus);
        }

        Log.d(TAG, "Match ID: " + matchId);
        Log.d(TAG, "Players: " + player1 + ", " + player2 + ", " + player3 + ", " + player4);
        Log.d(TAG, "Status: " + matchStatus);
    }

    private void setupInitialVisibility() {
        set2Container.setVisibility(View.GONE);
        set3Container.setVisibility(View.GONE);
        tvWinner.setVisibility(View.GONE);
    }

    private void fetchPlayerNames() {
        if (player1 != null && !player1.isEmpty()) playersToFetch++;
        if (player2 != null && !player2.isEmpty()) playersToFetch++;
        if (player3 != null && !player3.isEmpty()) playersToFetch++;
        if (player4 != null && !player4.isEmpty()) playersToFetch++;

        if (player1 != null && !player1.isEmpty()) fetchPlayerName(player1, true);
        if (player2 != null && !player2.isEmpty()) fetchPlayerName(player2, false);
        if (player3 != null && !player3.isEmpty()) fetchPlayerName(player3, true);
        if (player4 != null && !player4.isEmpty()) fetchPlayerName(player4, false);

        if (playersToFetch == 0) {
            playerNameTop.setText("Team 1");
            playerNameBottom.setText("Team 2");
            fetchLiveScore();
        }
    }

    private void fetchPlayerName(String phone, boolean isTeam1) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("tbl_Users");
        userRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = phone; // Default to phone number

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if (userSnap.child("name").getValue() != null) {
                        name = userSnap.child("name").getValue(String.class);
                    }
                    break;
                }

                updatePlayerName(isTeam1, name);
                checkAllPlayersFetched();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error fetching player name: " + error.getMessage());
                updatePlayerName(isTeam1, isTeam1 ? "Team 1" : "Team 2");
                checkAllPlayersFetched();
            }
        });
    }

    private void updatePlayerName(boolean isTeam1, String name) {
        TextView targetView = isTeam1 ? playerNameTop : playerNameBottom;
        String currentText = targetView.getText().toString();

        if (currentText.isEmpty() || currentText.equals(isTeam1 ? "Team 1" : "Team 2")) {
            targetView.setText(name);
        } else {
            targetView.setText(currentText + " / " + name);
        }

        playersToFetch--;
    }

    private void checkAllPlayersFetched() {
        if (playersToFetch == 0) {
            fetchLiveScore();
        }
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
                        determineWinner(score);

                        // Update status if it changes during the match
                        if (score.getMatchStatus() != null && !score.getMatchStatus().equals(matchStatus)) {
                            matchStatus = score.getMatchStatus();
                            tvMatchStatus.setText("Status: " + matchStatus);
                        }
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error fetching live score: " + error.getMessage());
                setDefaultScores();
            }
        });
    }

    private void updateScores(tbl_live_score score) {
        team1Score.setText(String.valueOf(score.getTeam1Score()));
        team2Score.setText(String.valueOf(score.getTeam2Score()));
    }

    private void updateSets(tbl_live_score score) {
        // Set 1
        team1Set.setText(score.getTeam1Set1() + "-" + score.getTeam2Set1());

        // Set 2
        if (score.getTeam1Set2() != 0 || score.getTeam2Set2() != 0) {
            set2Container.setVisibility(View.VISIBLE);
            team2Set.setText(score.getTeam1Set2() + "-" + score.getTeam2Set2());
        } else {
            set2Container.setVisibility(View.GONE);
        }

        // Set 3
        if (score.getTeam1Set3() != 0 || score.getTeam2Set3() != 0) {
            set3Container.setVisibility(View.VISIBLE);
            team3Set.setText(score.getTeam1Set3() + "-" + score.getTeam2Set3());
        } else {
            set3Container.setVisibility(View.GONE);
        }
    }

    private void determineWinner(tbl_live_score score) {
        if ("Played".equalsIgnoreCase(matchStatus)) {
            int team1Wins = 0;
            int team2Wins = 0;

            // Count set wins
            if (score.getTeam1Set1() > score.getTeam2Set1()) team1Wins++;
            else team2Wins++;

            if (score.getTeam1Set2() > score.getTeam2Set2()) team1Wins++;
            else if (score.getTeam2Set2() > score.getTeam1Set2()) team2Wins++;

            if (score.getTeam1Set3() > score.getTeam2Set3()) team1Wins++;
            else if (score.getTeam2Set3() > score.getTeam1Set3()) team2Wins++;

            // Determine winner
            if (team1Wins > team2Wins) {
                showWinner(playerNameTop.getText().toString(), true);
            } else if (team2Wins > team1Wins) {
                showWinner(playerNameBottom.getText().toString(), false);
            }
        }
    }
    private int getColorFromTheme(int colorAttr) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(colorAttr, typedValue, true);
        return typedValue.data;
    }
    private void showWinner(String winnerName, boolean isTeam1) {
        tvWinner.setText("Winner: " + winnerName);
        tvWinner.setVisibility(View.VISIBLE);
        
        if (isTeam1) {
            int colorPrimary = getColorFromTheme(android.R.attr.colorPrimary);
            playerNameTop.setTextColor(colorPrimary);
            team1Score.setTextColor(colorPrimary);
        } else {
            int colorPrimary = getColorFromTheme(android.R.attr.colorPrimary);
            playerNameBottom.setTextColor(colorPrimary);
            team2Score.setTextColor(colorPrimary);
        }
    }

    private void setDefaultScores() {
        team1Score.setText("0");
        team2Score.setText("0");
        team1Set.setText("0-0");
        team2Set.setText("0-0");
        team3Set.setText("0-0");
    }
}