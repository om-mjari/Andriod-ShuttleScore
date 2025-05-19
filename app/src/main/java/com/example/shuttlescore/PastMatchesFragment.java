package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class PastMatchesFragment extends Fragment {
    private String tournamentId;
    private String singlesDoubles;

    private List<MatchModel> liveMatchList;
    private TournamentMatchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_matches, container, false);

        ListView lspastmatches = view.findViewById(R.id.lspastmatches);
        liveMatchList = new ArrayList<>();
        adapter = new TournamentMatchAdapter(getContext(), liveMatchList);
        lspastmatches.setAdapter(adapter);

        tournamentId = getArguments().getString("tournamentId");

        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference("tbl_Tournaments").child(tournamentId);
        tournamentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    singlesDoubles = snapshot.child("singlesDoubles").getValue(String.class);
                    fetchPastMatches();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        lspastmatches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MatchModel match = liveMatchList.get(position);
                Intent intent = new Intent(getContext(), TeamMatchDetailsActivity.class);
                intent.putExtra("matchId", match.getMatchId());
                intent.putExtra("tournamentId", tournamentId);
                intent.putExtra("singlesDoubles", singlesDoubles);
                startActivity(intent);
            }
        });

        return view;
    }

    private void fetchPastMatches() {
        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("tbl_tournament_matches");

        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                liveMatchList.clear();
                for (DataSnapshot matchSnap : snapshot.getChildren()) {
                    MatchModel match = matchSnap.getValue(MatchModel.class);

                    if (match != null && tournamentId.equals(match.getTournamentId()))
                    {
                        if (match.getMatchStatus() != null && "Played".equalsIgnoreCase(match.getMatchStatus()) || "Abandoned".equalsIgnoreCase(match.getMatchStatus())) {

                            String team1Id = match.getTeam1id();
                            String team2Id = match.getTeam2id();

                            String teamTable = "Singles".equalsIgnoreCase(singlesDoubles) ? "tbl_Team" : "tbl_Double_Team";
                            DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference(teamTable);

                            teamRef.child(team1Id).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
