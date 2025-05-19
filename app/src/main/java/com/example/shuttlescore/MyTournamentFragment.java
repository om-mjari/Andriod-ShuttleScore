package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;

public class MyTournamentFragment extends Fragment {

    ListView lsmytournaments;
    ArrayList<TournamentModel> myTournaments;
    TournamentAdapter adapter;

    public MyTournamentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tournament, container, false);

        lsmytournaments = view.findViewById(R.id.lsmytournaments);
        myTournaments = new ArrayList<>();
        adapter = new TournamentAdapter(getContext(), myTournaments);
        lsmytournaments.setAdapter(adapter);

        fetchMyTournaments();

        lsmytournaments.setOnItemClickListener((parent, view1, position, id) -> {
            TournamentModel selectedTournament = myTournaments.get(position);
            Intent intent = new Intent(requireContext(), TournamentDetailActivity.class);
            intent.putExtra("tournamentId", selectedTournament.getTournamentId());
            intent.putExtra("tournamentName", selectedTournament.getTournamentName());
            intent.putExtra("groundName", selectedTournament.getGroundName());
            intent.putExtra("tournamentLogo", selectedTournament.getTournamentPhoto());
            intent.putExtra("organizerContactNo", selectedTournament.getOrganizerContactNo());
            startActivity(intent);
        });

        return view;
    }

    private void fetchMyTournaments() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String phoneNumber = prefs.getString("contact", null);

        if (phoneNumber == null) {
            Toast.makeText(getContext(), "Phone number not found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference("tbl_Tournaments");
        DatabaseReference teamRef = FirebaseDatabase.getInstance().getReference("tbl_Team");
        DatabaseReference doubleTeamRef = FirebaseDatabase.getInstance().getReference("tbl_Double_Team");
        DatabaseReference officialRef = FirebaseDatabase.getInstance().getReference("tbl_Officials");

        // Step 1: HashSet to keep track of unique tournament IDs
        HashSet<String> tournamentIdsToShow = new HashSet<>();

        // Step 2: Get all tournament IDs where user is organizer
        tournamentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tournamentIdsToShow.clear(); // Clear the existing set to avoid duplicates

                // Check if the user is an organizer
                for (DataSnapshot data : snapshot.getChildren()) {
                    tbl_ManageTournament tournament = data.getValue(tbl_ManageTournament.class);
                    if (tournament != null && phoneNumber.equals(tournament.getOrganizerContactNo())) {
                        tournamentIdsToShow.add(tournament.getTournamentId());
                    }
                }

                // Step 3: Check in tbl_Team (for players)
                teamRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String contact = data.child("playerContact").getValue(String.class);
                            String tournamentId = data.child("tournamentId").getValue(String.class);
                            if (contact != null && contact.equals(phoneNumber) && tournamentId != null) {
                                tournamentIdsToShow.add(tournamentId); // Ensure unique tournament IDs
                            }
                        }

                        // Step 4: Check in tbl_Double_Team (for double team players)
                        doubleTeamRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    String player1 = data.child("player1Contact").getValue(String.class);
                                    String player2 = data.child("player2Contact").getValue(String.class);
                                    String tournamentId = data.child("tournamentId").getValue(String.class);
                                    if (tournamentId != null &&
                                            (phoneNumber.equals(player1) || phoneNumber.equals(player2))) {
                                        tournamentIdsToShow.add(tournamentId); // Ensure unique IDs
                                    }
                                }

                                // Final Step: Check in tbl_Officials (for officials)
                                officialRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        final boolean[] isOfficialFound = {false};
                                        for (DataSnapshot data : snapshot.getChildren()) {
                                            String tournamentId = data.child("tournamentId").getValue(String.class);
                                            String userId = data.child("userId").getValue(String.class);

                                            if (userId != null) {
                                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("tbl_Users").child(userId);
                                                userRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String userPhone = snapshot.child("phone").getValue(String.class);
                                                        if (userPhone != null && userPhone.equals(phoneNumber)) {
                                                            tournamentIdsToShow.add(tournamentId); // Ensure unique tournament IDs
                                                            isOfficialFound[0] = true;
                                                        }
                                                        // Fetch tournament details after gathering all IDs
                                                        if (isOfficialFound[0] || tournamentIdsToShow.size() > 0) {
                                                            fetchTournamentDetails(tournamentIdsToShow, tournamentRef);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(getContext(), "Failed to get user info", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                        // Proceed to fetch tournament details even if the user is not in tbl_Officials
                                        if (!isOfficialFound[0]) {
                                            fetchTournamentDetails(tournamentIdsToShow, tournamentRef);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(getContext(), "Failed to load officials", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load double teams", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load teams", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load tournaments", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchTournamentDetails(HashSet<String> tournamentIdsToShow, DatabaseReference tournamentRef) {
        tournamentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    tbl_ManageTournament tournament = data.getValue(tbl_ManageTournament.class);
                    if (tournament != null && tournamentIdsToShow.contains(tournament.getTournamentId())) {

                        boolean alreadyAdded = false;
                        for (TournamentModel existingTournament : myTournaments) {
                            if (existingTournament.getTournamentId().equals(tournament.getTournamentId())) {
                                alreadyAdded = true;
                                break;
                            }
                        }
                        if (!alreadyAdded) {
                            addTournamentToList(tournament);  // Add only if not already added
                        }
                    }
                }
                adapter.notifyDataSetChanged();  // Update the adapter after processing all tournaments
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load tournaments", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addTournamentToList(tbl_ManageTournament tournament) {
        TournamentModel tournamentModel = new TournamentModel(
                tournament.getTournamentId(),
                tournament.getTournamentName(),
                tournament.getGroundName(),
                tournament.getTournamentPhoto(),
                tournament.getCityName(),
                tournament.getCreatedAt(),
                tournament.getDeletedAt(),
                tournament.getOrganizerContactNo(),
                tournament.getOrganizerEmailId(),
                tournament.getOrganizerName(),
                tournament.getShuttlecockType(),
                tournament.getSinglesDoubles(),
                tournament.getTournamentCategory(),
                tournament.getTournamentStartDate(),
                tournament.getTournamentEndDate()
        );
        myTournaments.add(tournamentModel);
    }


}
