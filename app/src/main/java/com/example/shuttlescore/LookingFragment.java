package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LookingFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView tvMessage;
    private ListView matchListView;
    private List<MatchModel> matchList;
    private MatchListAdapter adapter;
    private HashMap<String, tbl_live_score> liveScoreMap = new HashMap<>();
    private HashMap<String, String> phoneNameMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_looking, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        tvMessage = view.findViewById(R.id.tvMessage);
        matchListView = view.findViewById(R.id.matchListView);

        matchList = new ArrayList<>();
        adapter = new MatchListAdapter(getContext(), matchList);
        matchListView.setAdapter(adapter);

        matchListView.setOnItemClickListener((parent, view1, position, id) -> {
            MatchModel selectedMatch = matchList.get(position);
            Intent i = new Intent(getContext(), MatchDetailsActivity.class);

            i.putExtra("matchId", selectedMatch.getMatchId());
            i.putExtra("player1", selectedMatch.getPl1id());
            i.putExtra("player2", selectedMatch.getPl2id());
            i.putExtra("player3", selectedMatch.getPl3id());
            i.putExtra("player4", selectedMatch.getPl4id());
            i.putExtra("score", selectedMatch.getGamePoints());
            i.putExtra("Match_Status", selectedMatch.getMatchStatus());

            // Attach live score data
            tbl_live_score live = liveScoreMap.get(selectedMatch.getMatchId());
            if (live != null) {
                i.putExtra("Team1_Score", live.getTeam1Score());
                i.putExtra("Team2_Score", live.getTeam2Score());
                i.putExtra("Team1_Set1", live.getTeam1Set1());
                i.putExtra("Team2_Set1", live.getTeam2Set1());
                i.putExtra("Team1_Set2", live.getTeam1Set2());
                i.putExtra("Team2_Set2", live.getTeam2Set2());
                i.putExtra("Team1_Set3", live.getTeam1Set3());
                i.putExtra("Team2_Set3", live.getTeam2Set3());
            }

            startActivity(i);
        });

        loadMatches();

        return view;
    }

    private void loadMatches() {
        progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.GONE);

        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("tbl_matches");
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MatchModel match = ds.getValue(MatchModel.class);
                    if (match != null) {
                        matchList.add(match);
                    }
                }

                // First load user names if not already loaded
                if (phoneNameMap.isEmpty()) {
                    fetchAllUserNames();
                } else {
                    updateMatchNames();
                }

                // Always fetch live scores for real-time updates
                fetchLiveScores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText("Error loading matches.");
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLiveScores() {
        DatabaseReference liveRef = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score");
        liveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, tbl_live_score> newLiveScores = new HashMap<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    tbl_live_score live = ds.getValue(tbl_live_score.class);
                    if (live != null && live.getMatchId() != null) {
                        newLiveScores.put(live.getMatchId(), live);
                    }
                }
                liveScoreMap = newLiveScores;
                adapter.updateLiveScores(newLiveScores);

                // Update UI
                progressBar.setVisibility(View.GONE);
                tvMessage.setVisibility(matchList.isEmpty() ? View.VISIBLE : View.GONE);
                if (matchList.isEmpty()) {
                    tvMessage.setText("No matches found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error loading live scores: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAllUserNames() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("tbl_Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phoneNameMap.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String phone = ds.child("phone").getValue(String.class);
                    String name = ds.child("name").getValue(String.class);
                    if (phone != null && name != null) {
                        phoneNameMap.put(phone, name);
                    }
                }
                updateMatchNames();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText("Error loading users.");
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMatchNames() {
        for (MatchModel match : matchList) {
            String pl1 = phoneNameMap.getOrDefault(match.getPl1id(), "Unknown");
            String pl2 = phoneNameMap.getOrDefault(match.getPl2id(), "Unknown");
            String pl3 = phoneNameMap.getOrDefault(match.getPl3id(), "Unknown");
            String pl4 = phoneNameMap.getOrDefault(match.getPl4id(), "Unknown");

            if (!match.getPl1id().isEmpty() && !match.getPl4id().isEmpty() &&
                    match.getPl2id().isEmpty() && match.getPl3id().isEmpty()) {
                match.setTeam1Name(pl1);
                match.setTeam2Name(pl4);
            }
            else if (!match.getPl2id().isEmpty() && !match.getPl3id().isEmpty() &&
                    match.getPl1id().isEmpty() && match.getPl4id().isEmpty()) {
                match.setTeam1Name(pl2);
                match.setTeam2Name(pl3);
            }
            else {
                match.setTeam1Name(pl1 + " & " + pl2);
                match.setTeam2Name(pl3 + " & " + pl4);
            }
        }

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(matchList.isEmpty() ? View.VISIBLE : View.GONE);
        if (matchList.isEmpty()) {
            tvMessage.setText("No matches found.");
        }
    }
}