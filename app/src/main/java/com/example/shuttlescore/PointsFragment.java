package com.example.shuttlescore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class PointsFragment extends Fragment {

    private ListView listView;
    private PointsAdapter adapter;
    private ArrayList<TeamPoints> teamPointsList = new ArrayList<>();
    private HashMap<String, TeamPoints> teamMap = new HashMap<>();
    private String tournamentId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_points, container, false);
        tournamentId = getArguments().getString("tournamentId");
        listView = view.findViewById(R.id.listPoints);
        adapter = new PointsAdapter(getContext(), teamPointsList);
        listView.setAdapter(adapter);

        loadPointsData();
        return view;
    }

    private void loadPointsData() {
        FirebaseDatabase.getInstance().getReference("tbl_Score")
                .orderByChild("tournamentId")
                .equalTo(tournamentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        teamPointsList.clear();
                        teamMap.clear();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String team1 = snap.child("team1Name").getValue(String.class);
                            String team2 = snap.child("team2Name").getValue(String.class);
                            String winner = snap.child("winnerTeam").getValue(String.class);
                            String loser = snap.child("looserTeam").getValue(String.class);

                            updateTeamStats(team1, winner, loser);
                            updateTeamStats(team2, winner, loser);
                        }

                        teamPointsList.addAll(teamMap.values());

                        // SORTING LOGIC: Wins desc, then played desc
                        Collections.sort(teamPointsList, new Comparator<TeamPoints>() {
                            @Override
                            public int compare(TeamPoints t1, TeamPoints t2) {
                                if (t2.wins != t1.wins) {
                                    return Integer.compare(t2.wins, t1.wins); // More wins first
                                } else {
                                    return Integer.compare(t2.played, t1.played); // If wins equal, more matches first
                                }
                            }
                        });

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void updateTeamStats(String teamName, String winner, String loser) {
        if (teamName == null) return;

        TeamPoints team = teamMap.getOrDefault(teamName, new TeamPoints(teamName, 0, 0, 0));
        team.played += 1;
        if (teamName.equals(winner)) team.wins += 1;
        if (teamName.equals(loser)) team.losses += 1;
        teamMap.put(teamName, team);
    }
}
