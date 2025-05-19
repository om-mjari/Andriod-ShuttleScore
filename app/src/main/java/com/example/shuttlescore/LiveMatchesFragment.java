package com.example.shuttlescore;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LiveMatchesFragment extends Fragment {

    private String tournamentId;
    private String singlesDoubles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_live_matches, container, false);

        ListView lsliveMatches = view.findViewById(R.id.lsliveMatches);
        List<MatchModel> liveMatchList = new ArrayList<>();
        TournamentMatchAdapter adapter = new TournamentMatchAdapter(getContext(), liveMatchList);
        lsliveMatches.setAdapter(adapter);

        tournamentId = getArguments().getString("tournamentId");

        lsliveMatches.setOnItemClickListener((parent, view1, position, id) -> {
            MatchModel selectedMatch = liveMatchList.get(position);
            Intent intent = new Intent(getContext(), TeamMatchDetailsActivity.class);
            intent.putExtra("matchId", selectedMatch.getMatchId());
            intent.putExtra("tournamentId", selectedMatch.getTournamentId());
            intent.putExtra("singlesDoubles", singlesDoubles);
            startActivity(intent);
        });

        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference("tbl_Tournaments").child(tournamentId);
        tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    singlesDoubles = snapshot.child("singlesDoubles").getValue(String.class);
                    fetchLiveMatches(adapter, liveMatchList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

    private void fetchLiveMatches(TournamentMatchAdapter adapter, List<MatchModel> liveMatchList) {
        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("tbl_tournament_matches");

        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                liveMatchList.clear();
                for (DataSnapshot matchSnap : snapshot.getChildren()) {
                    MatchModel match = matchSnap.getValue(MatchModel.class);

                    if (match != null && tournamentId.equals(match.getTournamentId())
                            && "Live".equalsIgnoreCase(match.getMatchStatus())) {

                        String team1Id = match.getTeam1id();
                        String team2Id = match.getTeam2id();

                        String teamTable = "Singles".equalsIgnoreCase(singlesDoubles) ? "tbl_Team" : "tbl_Double_Team";
                        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference(teamTable);

                        teamRef.child(team1Id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot team1Snap) {
                                String team1Name = team1Snap.child("teamName").getValue(String.class);
                                match.setTeam1Name(team1Name != null ? team1Name : "Team 1");

                                teamRef.child(team2Id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot team2Snap) {
                                        String team2Name = team2Snap.child("teamName").getValue(String.class);
                                        match.setTeam2Name(team2Name != null ? team2Name : "Team 2");

                                        liveMatchList.add(match);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}

