package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Stack;

public class scoring extends AppCompatActivity {
    ImageView ivReset, ivMore;
    TextView tvTeamA, tvScoreA, tvTeamB, tvScoreB;
    Button btnAddScoreTop, btnAddScoreBottom;
    FrameLayout courtContainer, topLeft, topRight, bottomLeft, bottomRight;
    TextView tvTopLeftAdd, tvTopRightAdd, tvBottomLeftAdd, tvBottomRightAdd;
    String pl1, pl2, pl3, pl4, cnt1, cnt2, cnt3, cnt4, points, sets, matchStatus;
    int scoreA = 0, scoreB = 0, currentSet = 1, maxSets, teamASetsWon = 0, teamBSetsWon = 0;
    int set1Team1Score = 0, set1Team2Score = 0;
    int set2Team1Score = 0, set2Team2Score = 0;
    int set3Team1Score = 0, set3Team2Score = 0;
    int totalTeam1Score = 0, totalTeam2Score = 0;
    FirebaseHelper firebaseHelper;
    String matchId, liveScoreId;
    boolean isMatchLiveStarted = false;
    private boolean isDialogShowing = false;

    // Stack for undo
    Stack<String> scoreHistory = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scoring);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pl1 = getIntent().getStringExtra("pl1");
        pl2 = getIntent().getStringExtra("pl2");
        pl3 = getIntent().getStringExtra("pl3");
        pl4 = getIntent().getStringExtra("pl4");

        cnt1 = getIntent().getStringExtra("cnt1");
        cnt2 = getIntent().getStringExtra("cnt2");
        cnt3 = getIntent().getStringExtra("cnt3");
        cnt4 = getIntent().getStringExtra("cnt4");

        matchId = getIntent().getStringExtra("matchId");
        points = getIntent().getStringExtra("points");
        sets = getIntent().getStringExtra("sets");

        maxSets = Integer.parseInt(sets);

        initializeViews();

        firebaseHelper = new FirebaseHelper();
        liveScoreId = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score").push().getKey();

        setupListeners();
        setplayer();
    }

    private void initializeViews() {
        ivReset = findViewById(R.id.btn_reset);

        tvTeamA = findViewById(R.id.txt_team1);
        tvScoreA = findViewById(R.id.score_team1);
        tvTeamB = findViewById(R.id.txt_team2);
        tvScoreB = findViewById(R.id.score_team2);

        btnAddScoreTop = findViewById(R.id.btn_add_top);
        btnAddScoreBottom = findViewById(R.id.btn_add_bottom);

        courtContainer = findViewById(R.id.court_container);
        topLeft = findViewById(R.id.top_left);
        topRight = findViewById(R.id.top_right);
        bottomLeft = findViewById(R.id.bottom_left);
        bottomRight = findViewById(R.id.bottom_right);

        tvTopLeftAdd = findViewById(R.id.txt_top_left);
        tvTopRightAdd = findViewById(R.id.txt_top_right);
        tvBottomLeftAdd = findViewById(R.id.txt_bottom_left);
        tvBottomRightAdd = findViewById(R.id.txt_bottom_right);
    }

    public void setplayer() {
        tvTopLeftAdd.setText(pl1);
        tvTopRightAdd.setText(pl2);
        tvBottomLeftAdd.setText(pl3);
        tvBottomRightAdd.setText(pl4);
    }

    private void setupListeners() {
        int maxPoints = Integer.parseInt(points);

        btnAddScoreTop.setOnClickListener(v -> {
            updateTeamLabelsWithSetsWon();
            if (!isMatchLiveStarted) {
                isMatchLiveStarted = true;
                setMatchStatusToLive();
            }

            if (scoreA < maxPoints) {
                scoreA++;
                tvScoreA.setText(String.valueOf(scoreA));
                scoreHistory.push("A");
                updateTeamLabelsWithSetsWon();
                updateSetScoresLive();

                if (scoreA == maxPoints) {
                    teamASetsWon++;
                    totalTeam1Score++;
                    updateTeamLabelsWithSetsWon();

                    if (teamASetsWon == 2 || maxSets == 1) {
                        matchStatus = "Completed";
                        updateSetScoresLive();
                        updateTeamLabelsWithSetsWon();
                        showWinnerDialog("Team A");
                        setMatchStatusToPlayed();
                    } else {
                        nextSet("Team A");
                    }
                }
            }
        });

        btnAddScoreBottom.setOnClickListener(v -> {
            if (!isMatchLiveStarted) {
                isMatchLiveStarted = true;
                setMatchStatusToLive();
            }

            if (scoreB < maxPoints) {
                scoreB++;
                tvScoreB.setText(String.valueOf(scoreB));
                scoreHistory.push("B");
                updateSetScoresLive();
                updateTeamLabelsWithSetsWon();

                if (scoreB == maxPoints) {
                    teamBSetsWon++;
                    totalTeam2Score++;
                    updateTeamLabelsWithSetsWon();

                    if (teamBSetsWon == 2 || maxSets == 1) {
                        matchStatus = "Completed";
                        updateTeamLabelsWithSetsWon();
                        updateSetScoresLive();
                        showWinnerDialog("Team B");
                        setMatchStatusToPlayed();
                    } else {
                        nextSet("Team B");
                    }
                }
            }
        });

        ivReset.setOnClickListener(v -> {
            if (!scoreHistory.isEmpty()) {
                String last = scoreHistory.pop();
                if (last.equals("A") && scoreA > 0) {
                    scoreA--;
                    tvScoreA.setText(String.valueOf(scoreA));
                } else if (last.equals("B") && scoreB > 0) {
                    scoreB--;
                    tvScoreB.setText(String.valueOf(scoreB));
                }
                updateSetScoresLive();
            } else {
                Toast.makeText(this, "Nothing to undo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWinnerDialog(String winnerTeam) {
        new AlertDialog.Builder(this)
                .setTitle("🏆 Game Over")
                .setMessage(winnerTeam + " is the winner! 🎉\n\nGreat match!")
                .setIcon(R.drawable.trophy)
                .setNegativeButton("Exit", (dialog, which) -> {
                    Intent i = new Intent(scoring.this, MainActivity.class);
                    startActivity(i);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void nextSet(String winnerTeam) {
        if (currentSet == 1) {
            set1Team1Score = scoreA;
            set1Team2Score = scoreB;
        } else if (currentSet == 2) {
            set2Team1Score = scoreA;
            set2Team2Score = scoreB;
        } else if (currentSet == 3) {
            set3Team1Score = scoreA;
            set3Team2Score = scoreB;
        }

        Toast.makeText(this, winnerTeam + " won Set " + currentSet, Toast.LENGTH_SHORT).show();

        currentSet++;
        scoreA = 0;
        scoreB = 0;
        tvScoreA.setText("0");
        tvScoreB.setText("0");

        scoreHistory.clear();
        updateSetScoresLive();

        Toast.makeText(this, "Set " + currentSet + " started", Toast.LENGTH_SHORT).show();
    }

    private void updateSetScoresLive() {
        switch (currentSet) {
            case 1:
                set1Team1Score = scoreA;
                set1Team2Score = scoreB;
                break;
            case 2:
                set2Team1Score = scoreA;
                set2Team2Score = scoreB;
                break;
            case 3:
                set3Team1Score = scoreA;
                set3Team2Score = scoreB;
                break;
        }

        saveScoreToFirebase();
    }

    private void saveScoreToFirebase() {
        firebaseHelper.addLiveScore(
                this,
                liveScoreId,
                matchId,
                totalTeam1Score,
                totalTeam2Score,
                set1Team1Score,
                set1Team2Score,
                set2Team1Score,
                set2Team2Score,
                set3Team1Score,
                set3Team2Score,
                matchStatus
        );
    }

    private void setMatchStatusToLive() {
        FirebaseDatabase.getInstance().getReference("tbl_matches")
                .child(matchId)
                .child("matchStatus")
                .setValue("Live");
    }

    private void setMatchStatusToPlayed() {
        FirebaseDatabase.getInstance().getReference("tbl_matches")
                .child(matchId)
                .child("matchStatus")
                .setValue("Played");
    }

    private void setMatchStatusToAbonded() {
        FirebaseDatabase.getInstance().getReference("tbl_matches")
                .child(matchId)
                .child("matchStatus")
                .setValue("Abondod");
    }

    private void updateTeamLabelsWithSetsWon() {
        tvTeamA.setText("Team A (Sets: " + teamASetsWon + ")");
        tvTeamB.setText("Team B (Sets: " + teamBSetsWon + ")");
    }

    @Override
    public void onBackPressed() {
        if (!isDialogShowing) {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        isDialogShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(scoring.this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    setMatchStatusToAbonded();
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                    isDialogShowing = false;
                })
                .setOnDismissListener(dialog -> isDialogShowing = false)
                .show();
    }
}
