package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class MyMatchesFragment extends Fragment {
    ListView lsmymacthes;
    ArrayList<tbl_matches> matchList;
    int Team1_Score = 0, Team2_Score = 0, Team1_Set1 = 0, Team2_Set1 = 0;
    MatchAdapter adapter;

    public MyMatchesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_matches, container, false);

        lsmymacthes = view.findViewById(R.id.lsmymacthes);
        matchList = new ArrayList<>();
        adapter = new MatchAdapter(getContext(), matchList);
        lsmymacthes.setAdapter(adapter);

        lsmymacthes.setOnItemClickListener((parent, view1, position, id) -> {
            tbl_matches selectedMatch = matchList.get(position);

            DatabaseReference liveScoreRef = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score");
            liveScoreRef.orderByChild("matchId").equalTo(selectedMatch.matchId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            for (DataSnapshot scoreSnap : snapshot.getChildren()) {
                                tbl_live_score score = scoreSnap.getValue(tbl_live_score.class);
                                if (score != null) {

                                    Team1_Score = score.getTeam1Score();
                                    Team2_Score = score.getTeam2Score();
                                    Team1_Set1 = score.getTeam1Set1();
                                    Team2_Set1 = score.getTeam2Set1();

                                    break;
                                }else {
                                    Toast.makeText(getContext(), "No live score found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            Intent i = new Intent(getContext(), MatchDetailsActivity.class);
                            i.putExtra("matchId", selectedMatch.matchId);
                            i.putExtra("Match_Status", selectedMatch.matchStatus);
                            i.putExtra("player1", selectedMatch.pl1id);
                            i.putExtra("player2", selectedMatch.pl2id);
                            i.putExtra("player3", selectedMatch.pl3id);
                            i.putExtra("player4", selectedMatch.pl4id);
                            i.putExtra("score", selectedMatch.gamePoints);

                            i.putExtra("Team1_Score", Team1_Score);
                            i.putExtra("Team2_Score", Team2_Score);
                            i.putExtra("Team1_Set1", Team1_Set1);
                            i.putExtra("Team2_Set1", Team2_Set1);

                            startActivity(i);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(getContext(), "Failed to load live score", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        fetchMatchesFromFirebase();
        return view;
    }

    private void fetchMatchesFromFirebase() {
        Context context = getContext();
        if (context == null) return;

        String phoneNumber = context.getSharedPreferences("login", Context.MODE_PRIVATE)
                .getString("contact", null);

        if (phoneNumber == null) {
            Toast.makeText(context, "Phone number not found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("tbl_matches");
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                matchList.clear();
                for (DataSnapshot matchSnap : snapshot.getChildren()) {
                    tbl_matches match = matchSnap.getValue(tbl_matches.class);
                    if (match != null && (
                            phoneNumber.equals(match.pl1id) ||
                                    phoneNumber.equals(match.pl2id) ||
                                    phoneNumber.equals(match.pl3id) ||
                                    phoneNumber.equals(match.pl4id))) {
                        matchList.add(match);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Failed to load matches", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
