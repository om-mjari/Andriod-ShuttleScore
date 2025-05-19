package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Stack;

public class tournamentscoring extends AppCompatActivity {

    private ImageButton btnUndoIcon;
    private ImageView btnMore;
    private TextView txtTeam1, txtTeam2, scoreTeam1, scoreTeam2, txtTeamA, txtTeamB;
    private Button btnAddScoreTeamA, btnAddScoreTeamB;

    private DatabaseReference liveScoreRef;
    private int scoreA = 0, scoreB = 0;
    private int currentSet = 1;
    private int teamASetsWon = 0, teamBSetsWon = 0;
    private int team1Set1 = 0, team2Set1 = 0;
    private int team1Set2 = 0, team2Set2 = 0;
    private int team1Set3 = 0, team2Set3 = 0;

    private String matchId, team1Name, team2Name, gamePoints, matchFormat, liveScoreId;
    private boolean isMatchLiveStarted = false;
    private boolean isDialogShowing = false;
    private Stack<String> lastScoredTeamStack = new Stack<>();
    private static final int MENU_PENALTY_TEAM_A = 1;
    private static final int MENU_PENALTY_TEAM_B = 2;
    private static final int MENU_RESET_MATCH = 3;
    private static final int MENU_END_MATCH_EARLY = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournamentscoring);

        initializeViews();
        getIntentExtras();
        setupFirebase();
        setupButtonListeners();
        registerForContextMenu(btnMore);
        updateSetScoresLive();
    }

    private void initializeViews() {
        btnUndoIcon = findViewById(R.id.btnUndoIcon);
        btnMore = findViewById(R.id.btn_more);
        btnAddScoreTeamA = findViewById(R.id.btnAddScoreTeamA);
        btnAddScoreTeamB = findViewById(R.id.btnAddScoreTeamB);
        txtTeam1 = findViewById(R.id.txt_team1);
        txtTeam2 = findViewById(R.id.txt_team2);
        scoreTeam1 = findViewById(R.id.score_team1);
        scoreTeam2 = findViewById(R.id.score_team2);
        txtTeamA = findViewById(R.id.txtTeamA);
        txtTeamB = findViewById(R.id.txtTeamB);
    }

    private void getIntentExtras() {
        matchId = getIntent().getStringExtra("matchId");
        team1Name = getIntent().getStringExtra("team1Name");
        team2Name = getIntent().getStringExtra("team2Name");
        gamePoints = getIntent().getStringExtra("gamePoints");
        matchFormat = getIntent().getStringExtra("matchFormat");

        txtTeam1.setText(team1Name);
        txtTeam2.setText(team2Name);
        txtTeamA.setText(team1Name);
        txtTeamB.setText(team2Name);
    }

    private void setupFirebase() {
        liveScoreId = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score").push().getKey();
        liveScoreRef = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score").child(liveScoreId);
    }

    private void setupButtonListeners() {
        btnAddScoreTeamA.setOnClickListener(v -> {
            if (scoreA < Integer.parseInt(gamePoints)) {
                scoreA++;
                scoreTeam1.setText(String.valueOf(scoreA));
                lastScoredTeamStack.push("A");
                handleMatchStartAndCheck();
            }
        });

        btnAddScoreTeamB.setOnClickListener(v -> {
            if (scoreB < Integer.parseInt(gamePoints)) {
                scoreB++;
                scoreTeam2.setText(String.valueOf(scoreB));
                lastScoredTeamStack.push("B");
                handleMatchStartAndCheck();
            }
        });

        btnUndoIcon.setOnClickListener(v -> undoLastAction());

        btnMore.setOnClickListener(this::openContextMenu);
    }

    private void handleMatchStartAndCheck() {
        if (!isMatchLiveStarted) {
            isMatchLiveStarted = true;
            setMatchStatusToLive();
        }
        checkSetCompletion();
    }

    private void undoLastAction() {
        if (!lastScoredTeamStack.isEmpty()) {
            String lastTeam = lastScoredTeamStack.pop();
            if (lastTeam.equals("A") && scoreA > 0) {
                scoreA--;
                scoreTeam1.setText(String.valueOf(scoreA));
            } else if (lastTeam.equals("B") && scoreB > 0) {
                scoreB--;
                scoreTeam2.setText(String.valueOf(scoreB));
            }
            updateSetScoresLive();
            Toast.makeText(this, "Undo successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nothing to undo", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkSetCompletion() {
        int gamePointsInt = Integer.parseInt(gamePoints);
        if (scoreA == gamePointsInt) {
            teamASetsWon++;
            updateSetScoresLive();
            nextSet(team1Name);
        } else if (scoreB == gamePointsInt) {
            teamBSetsWon++;
            updateSetScoresLive();
            nextSet(team2Name);
        } else {
            updateSetScoresLive();
        }
    }

    private void nextSet(String winnerTeam) {
        switch (currentSet) {
            case 1: team1Set1 = scoreA; team2Set1 = scoreB; break;
            case 2: team1Set2 = scoreA; team2Set2 = scoreB; break;
            case 3: team1Set3 = scoreA; team2Set3 = scoreB; break;
        }

        if ((matchFormat.equals("1") && (teamASetsWon == 1 || teamBSetsWon == 1)) ||
                (matchFormat.equals("3") && (teamASetsWon == 2 || teamBSetsWon == 2))) {
            showWinnerDialog(winnerTeam);
        } else {
            scoreA = 0;
            scoreB = 0;
            currentSet++;
            lastScoredTeamStack.clear();
            scoreTeam1.setText("0");
            scoreTeam2.setText("0");
            updateSetScoresLive();
        }
    }

    private void updateSetScoresLive() {
        switch (currentSet) {
            case 1: team1Set1 = scoreA; team2Set1 = scoreB; break;
            case 2: team1Set2 = scoreA; team2Set2 = scoreB; break;
            case 3: team1Set3 = scoreA; team2Set3 = scoreB; break;
        }

        liveScoreRef.child("Live_Score_Id").setValue(liveScoreId);
        liveScoreRef.child("Team1_Score").setValue(teamASetsWon);
        liveScoreRef.child("Team2_Score").setValue(teamBSetsWon);
        liveScoreRef.child("Team1_Set1").setValue(team1Set1);
        liveScoreRef.child("Team2_Set1").setValue(team2Set1);
        liveScoreRef.child("Team1_Set2").setValue(team1Set2);
        liveScoreRef.child("Team2_Set2").setValue(team2Set2);
        liveScoreRef.child("Team1_Set3").setValue(team1Set3);
        liveScoreRef.child("Team2_Set3").setValue(team2Set3);
        liveScoreRef.child("matchId").setValue(matchId);
        liveScoreRef.child("matchStatus").setValue("Ongoing");

        updateSetIndicators();
    }

    private void updateSetIndicators() {
        TextView setIndicatorTeam1 = findViewById(R.id.set_indicator_team1);
        TextView setIndicatorTeam2 = findViewById(R.id.set_indicator_team2);
        setIndicatorTeam1.setText("⭐".repeat(teamASetsWon));
        setIndicatorTeam2.setText("⭐".repeat(teamBSetsWon));
    }

    private void showWinnerDialog(String winnerTeam) {
        String tournamentId = getIntent().getStringExtra("tournamentId");
        String tournamentName = getIntent().getStringExtra("tournamentName");
        String organizerContactNo = getIntent().getStringExtra("organizerContactNo");
        String groundName = getIntent().getStringExtra("groundName");
        new AlertDialog.Builder(this)
                .setTitle("🏆 Game Over")
                .setMessage(winnerTeam + " wins the match!") // Shows winning team
                .setPositiveButton("OK", (dialog, which) -> {
                    insertMatchResult(winnerTeam);
                    setMatchStatusToPlayed();
                    Intent intent = new Intent(tournamentscoring.this, TournamentDetailActivity.class);
                    intent.putExtra("tournamentId", tournamentId);
                    intent.putExtra("tournamentName", tournamentName);
                    intent.putExtra("organizerContactNo", organizerContactNo);
                    intent.putExtra("groundName", groundName);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void setMatchStatusToLive() {
        FirebaseDatabase.getInstance().getReference("tbl_tournament_matches")
                .child(matchId)
                .child("matchStatus")
                .setValue("Live");
    }

    private void setMatchStatusToPlayed() {
        FirebaseDatabase.getInstance().getReference("tbl_tournament_matches")
                .child(matchId)
                .child("matchStatus")
                .setValue("Played");
    }
    private void setMatchStatusToAbonded() {
        FirebaseDatabase.getInstance().getReference("tbl_tournament_matches")
                .child(matchId)
                .child("matchStatus")
                .setValue("Abandoned");
    }


    @Override
    public void onBackPressed() {
        if (!isDialogShowing) {
            showExitDialog();
        }
    }

    private void showExitDialog() {
        isDialogShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(tournamentscoring.this);
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
    private void insertMatchResult(String winnerTeam) {
        DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference("tbl_Score");
        String scoreId = scoreRef.push().getKey();

        if (scoreId == null) return;
        String tournamentId = getIntent().getStringExtra("tournamentId");

        String looserTeam = winnerTeam.equals(team1Name) ? team2Name : team1Name;

        scoreRef.child(scoreId).child("scoreId").setValue(scoreId);
        scoreRef.child(scoreId).child("matchId").setValue(matchId);
        scoreRef.child(scoreId).child("team1Name").setValue(team1Name);
        scoreRef.child(scoreId).child("team2Name").setValue(team2Name);
        scoreRef.child(scoreId).child("team1Set1").setValue(team1Set1);
        scoreRef.child(scoreId).child("team2Set1").setValue(team2Set1);
        scoreRef.child(scoreId).child("team1Set2").setValue(team1Set2);
        scoreRef.child(scoreId).child("team2Set2").setValue(team2Set2);
        scoreRef.child(scoreId).child("team1Set3").setValue(team1Set3);
        scoreRef.child(scoreId).child("team2Set3").setValue(team2Set3);
        scoreRef.child(scoreId).child("winnerTeam").setValue(winnerTeam);
        scoreRef.child(scoreId).child("looserTeam").setValue(looserTeam);
        scoreRef.child(scoreId).child("tournamentId").setValue(tournamentId);
        scoreRef.child(scoreId).child("timestamp").setValue(System.currentTimeMillis());
    }

    private void resetMatch() {
        scoreA = 0;
        scoreB = 0;
        teamASetsWon = 0;
        teamBSetsWon = 0;
        currentSet = 1;
        lastScoredTeamStack.clear();

        scoreTeam1.setText("0");
        scoreTeam2.setText("0");
        updateSetScoresLive();

        Toast.makeText(this, "Match reset successfully!", Toast.LENGTH_SHORT).show();
    }

    private void endMatchEarly(String winnerTeam) {
        setMatchStatusToAbonded();
        showWinnerDialog(winnerTeam);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.btn_more) {
            menu.setHeaderTitle("Match Actions");
            menu.add(0, MENU_PENALTY_TEAM_A, 0, "Penalty for " + team1Name);
            menu.add(0, MENU_PENALTY_TEAM_B, 0, "Penalty for " + team2Name);
            menu.add(0, MENU_RESET_MATCH, 0, "Reset Match");
            menu.add(0, MENU_END_MATCH_EARLY, 0, "End Match Early");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PENALTY_TEAM_A:
                applyPenalty(true);
                return true;
            case MENU_PENALTY_TEAM_B:
                applyPenalty(false);
                return true;
            case MENU_RESET_MATCH:
                resetMatch();
                return true;
            case MENU_END_MATCH_EARLY:
                endMatchEarly("Team A");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void applyPenalty(boolean isTeamA) {
        if (isTeamA && scoreB < Integer.parseInt(gamePoints)) {
            scoreB = Math.min(scoreB + 1, Integer.parseInt(gamePoints));
            scoreTeam2.setText(String.valueOf(scoreB));
            lastScoredTeamStack.push("B");
            Toast.makeText(this, "Penalty: Point to " + team2Name, Toast.LENGTH_SHORT).show();
        } else if (!isTeamA && scoreA < Integer.parseInt(gamePoints)) {
            scoreA = Math.min(scoreA + 1, Integer.parseInt(gamePoints));
            scoreTeam1.setText(String.valueOf(scoreA));
            lastScoredTeamStack.push("A");
            Toast.makeText(this, "Penalty: Point to " + team1Name, Toast.LENGTH_SHORT).show();
        }
        checkSetCompletion();
    }

}
