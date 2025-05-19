package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpcomingMatchesFragment extends Fragment {
    Button btnschedule;
    String tournamentId, organizercontact, singlesDoubles,tournamentname,groundname,contact;
    DatabaseReference tournamentRef;
    private ListView lspastmatches;
    private List<MatchModel> matchList;
    private TournamentMatchAdapter adapter;
    boolean isOrganizer,isOfficial;
    List<String> liveContactNos;

    public UpcomingMatchesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_matches, container, false);

        lspastmatches = view.findViewById(R.id.lspastmatches);
        btnschedule = view.findViewById(R.id.btnschedule);
        matchList = new ArrayList<>();
        adapter = new TournamentMatchAdapter(getContext(), matchList);
        lspastmatches.setAdapter(adapter);

        SharedPreferences sh = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        contact = sh.getString("contact", "");

        if (getArguments() != null) {
            tournamentId = getArguments().getString("tournamentId");
        }
        tournamentname = getArguments().getString("tournamentName");
        groundname = getArguments().getString("groundName");

        tournamentRef = FirebaseDatabase.getInstance().getReference("tbl_Tournaments");

        if (tournamentId != null) {
            tournamentRef.child(tournamentId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        organizercontact = snapshot.child("organizerContactNo").getValue(String.class);
                        singlesDoubles = snapshot.child("singlesDoubles").getValue(String.class);
                        DatabaseReference officialsRef = FirebaseDatabase.getInstance().getReference("tbl_Officials");

                        officialsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<String> officialUserIds = new ArrayList<>();
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    String fetchedTournamentId = snap.child("tournamentId").getValue(String.class);
                                    String fetchedUserId = snap.child("userId").getValue(String.class);
                                    if (tournamentId.equals(fetchedTournamentId) && fetchedUserId != null) {
                                        officialUserIds.add(fetchedUserId);
                                    }
                                }

                                if (officialUserIds.isEmpty()) {
                                    isOrganizer = contact.equals(organizercontact);
                                    btnschedule.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);
                                } else {
                                    checkIfOfficialByPhone(officialUserIds);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getActivity(), "Error fetching official data", Toast.LENGTH_SHORT).show();
                            }
                        });

                        onclick();
                        fetchUpcomingMatches();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Error fetching tournament data", Toast.LENGTH_SHORT).show();
                }
            });
        }



        lspastmatches.setOnItemClickListener((parent, view1, position, id) -> {

            if (isOrganizer || isOfficial) {
                MatchModel clickedMatch = matchList.get(position);
                Intent intent = new Intent(getActivity(), MatchDetailActivity.class);
                intent.putExtra("tournamentName", tournamentname);
                intent.putExtra("groundName", groundname);
                intent.putExtra("organizerContactNo", organizercontact);
                intent.putExtra("matchId", clickedMatch.getMatchId());
                intent.putExtra("team1Name", clickedMatch.getTeam1Name());
                intent.putExtra("team2Name", clickedMatch.getTeam2Name());
                intent.putExtra("tournamentId", clickedMatch.getTournamentId());
                intent.putExtra("tournamentName", tournamentname);
                intent.putExtra("organizerContactNo", organizercontact);
                intent.putExtra("groundName", groundname);
                startActivity(intent);
            }
        });

        lspastmatches.setOnItemLongClickListener((parent, view12, position, id) -> {
            if (isOrganizer || isOfficial) {
                MatchModel selectedMatch = matchList.get(position);
                String matchIdToDelete = selectedMatch.getMatchId();

                new android.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Match")
                        .setMessage("Are you sure you want to delete this match?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteMatch(matchIdToDelete);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return true;
        });

        return view;
    }

    public void onclick() {
        btnschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i = new Intent(getActivity(), Schedule.class);
                    i.putExtra("tournamentId", tournamentId);
                    i.putExtra("SinglesDoubles", singlesDoubles);
                    i.putExtra("tournamentName", tournamentname);
                    i.putExtra("groundName", groundname);
                    i.putExtra("organizerContactNo", organizercontact);
                    startActivity(i);
            }
        });
    }

    private void fetchUpcomingMatches() {
        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("tbl_tournament_matches");

        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot matchSnapshot) {
                matchList.clear();
                for (DataSnapshot matchSnap : matchSnapshot.getChildren()) {
                    MatchModel match = matchSnap.getValue(MatchModel.class);

                    if (match != null && tournamentId.equals(match.getTournamentId())
                            && "Scheduled".equalsIgnoreCase(match.getMatchStatus())) {

                        String team1Id = match.getTeam1id();
                        String team2Id = match.getTeam2id();

                        DatabaseReference teamRef;
                        if ("Doubles".equalsIgnoreCase(singlesDoubles)) {
                            teamRef = FirebaseDatabase.getInstance().getReference("tbl_Double_Team");
                        } else{
                            teamRef = FirebaseDatabase.getInstance().getReference("tbl_Team");
                        }

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

                                        matchList.add(match);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error fetching Team 2", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error fetching Team 1", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error fetching Matches", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkIfOfficialByPhone(List<String> userIds) {
        SharedPreferences sh = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String contactInPrefs = sh.getString("contact", "");

        isOfficial = false;
        isOrganizer = contact.equals(organizercontact);

        if (userIds.isEmpty()) {
            btnschedule.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);
            return;
        }

        final int[] checkedCount = {0}; // Counter to track checked users
        for (String userId : userIds) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("tbl_Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    checkedCount[0]++;
                    String userPhone = snapshot.child("phone").getValue(String.class);
                    if (userPhone != null && userPhone.equals(contactInPrefs)) {
                        isOfficial = true;
                    }

                    if (checkedCount[0] == userIds.size()) {
                        // All checks done, decide visibility
                        btnschedule.setVisibility((isOrganizer || isOfficial) ? View.VISIBLE : View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    checkedCount[0]++;
                    if (checkedCount[0] == userIds.size()) {
                        btnschedule.setVisibility((isOrganizer || isOfficial) ? View.VISIBLE : View.GONE);
                    }
                    Toast.makeText(getContext(), "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //    Delete code
    private void deleteMatch(String matchId) {
        DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference("tbl_tournament_matches").child(matchId);

        matchRef.removeValue().addOnSuccessListener(aVoid -> {
            Iterator<MatchModel> iterator = matchList.iterator();
            while (iterator.hasNext()) {
                MatchModel match = iterator.next();
                if (match.getMatchId().equals(matchId)) {
                    iterator.remove();
                    break;
                }
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Match deleted successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to delete match", Toast.LENGTH_SHORT).show();
        });
    }


}
