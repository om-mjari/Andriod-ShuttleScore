package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class TeamsFragment extends Fragment {

    private ListView teamListView;
    private List<tbl_team> teamList;
    private List<tbl_double_team> doubleTeamList;
    private TeamAdapter adapter;
    private DoubleTeamAdapter doubleAdapter;
    private DatabaseReference teamRef;
    private String tournamentId, singlesDoubles, organizercontact, orgcontact;
    Button btnAddManually;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ThemeUtils.applyTheme(getActivity());

        View view = inflater.inflate(R.layout.fragment_teams, container, false);

        tournamentId = getArguments().getString("tournamentId");
        btnAddManually = view.findViewById(R.id.btnAddManually);

        SharedPreferences sh = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        orgcontact = sh.getString("contact", "");

        singledoubles();

        teamListView = view.findViewById(R.id.teamListView);
        registerForContextMenu(teamListView);

        teamList = new ArrayList<>();
        adapter = new TeamAdapter(getContext(), teamList, false);
        teamListView.setAdapter(adapter);

        doubleTeamList = new ArrayList<>();

        btnAddManually.setOnClickListener(v -> {
            if (singlesDoubles.equalsIgnoreCase("Singles")) {
                Intent i = new Intent(getActivity(), newAddTeamsss.class);
                i.putExtra("tournamentId", tournamentId);
                startActivity(i);
            }
            if (singlesDoubles.equalsIgnoreCase("Doubles")) {
                Intent i = new Intent(getActivity(), AddDoubleTeamActivity.class);
                i.putExtra("tournamentId", tournamentId);
                startActivity(i);
            }
        });

        return view;
    }
    public void onclick(){


    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.teamListView) {
            menu.setHeaderTitle("Select Action");
            menu.add(0, v.getId(), 0, "UPDATE");
            menu.add(0, v.getId(), 1, "DELETE");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (singlesDoubles.equalsIgnoreCase("Singles")) {
            final tbl_team selectedTeam = teamList.get(info.position);

            if (item.getTitle().equals("DELETE")) {
                teamRef.child(selectedTeam.getTeamId()).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Team deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete team", Toast.LENGTH_SHORT).show());

            } else if (item.getTitle().equals("UPDATE")) {
                Intent intent = new Intent(getActivity(), newAddTeamsss.class);
                intent.putExtra("isUpdate", true);
                intent.putExtra("teamId", selectedTeam.getTeamId());
                intent.putExtra("teamName", selectedTeam.getTeamName());
                intent.putExtra("city", selectedTeam.getCity());
                intent.putExtra("captainName", selectedTeam.getCaptainName());
                intent.putExtra("captainContact", selectedTeam.getCaptainContact());
                intent.putExtra("tournamentId", tournamentId);
                startActivity(intent);
            }
        } else if (singlesDoubles.equalsIgnoreCase("Doubles")) {
            final tbl_double_team selectedTeam = doubleTeamList.get(info.position);

            if (item.getTitle().equals("DELETE")) {
                teamRef.child(selectedTeam.getKey()).removeValue()
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Double team deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete double team", Toast.LENGTH_SHORT).show());

            } else if (item.getTitle().equals("UPDATE")) {
                Intent intent = new Intent(getActivity(), UpdateDoubleTeamActivity.class);
                intent.putExtra("teamKey", selectedTeam.getKey());
                intent.putExtra("teamName", selectedTeam.getTeamName());
                intent.putExtra("city", selectedTeam.getCity());
                intent.putExtra("captain1Name", selectedTeam.getCaptain1Name());
                intent.putExtra("captain1Number", selectedTeam.getCaptain1Number());
                intent.putExtra("captain2Name", selectedTeam.getCaptain2Name());
                intent.putExtra("captain2Number", selectedTeam.getCaptain2Number());
                intent.putExtra("tournamentId", tournamentId);
                startActivity(intent);
            }
        }

        return true;
    }

    public void singledoubles() {
        DatabaseReference tournamentRef = FirebaseDatabase.getInstance().getReference("tbl_Tournaments").child(tournamentId);

        tournamentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    singlesDoubles = snapshot.child("singlesDoubles").getValue(String.class);
                    organizercontact = snapshot.child("organizerContactNo").getValue(String.class);
                    checkIfUserCanAddTeam(organizercontact);
                    fetchTeams();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load tournament type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchTeams() {
        if (singlesDoubles != null) {
            if (singlesDoubles.equalsIgnoreCase("Singles")) {
                teamRef = FirebaseDatabase.getInstance().getReference("tbl_Team");
                teamRef.orderByChild("tournamentId").equalTo(tournamentId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                teamList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    tbl_team team = dataSnapshot.getValue(tbl_team.class);
                                    if (team != null) {
                                        teamList.add(team);
                                    }
                                }
                                adapter = new TeamAdapter(getContext(), teamList, false);
                                teamListView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load teams", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (singlesDoubles.equalsIgnoreCase("Doubles")) {
                teamRef = FirebaseDatabase.getInstance().getReference("tbl_Double_Team");
                teamRef.orderByChild("tournamentId").equalTo(tournamentId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                doubleTeamList.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    tbl_double_team doubleTeam = dataSnapshot.getValue(tbl_double_team.class);
                                    if (doubleTeam != null) {
                                        doubleTeamList.add(doubleTeam);
                                    }
                                }
                                doubleAdapter = new DoubleTeamAdapter(getContext(), doubleTeamList);
                                teamListView.setAdapter(doubleAdapter);
                                doubleAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load double teams", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(getContext(), "Tournament type is not yet available", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfUserCanAddTeam(String organizercontact) {
        DatabaseReference officialsRef = FirebaseDatabase.getInstance()
                .getReference("tbl_Officials");

        officialsRef.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> officialPhones = new ArrayList<>();
                        int totalOfficials = (int) snapshot.getChildrenCount();

                        if (totalOfficials == 0) {
                            boolean isOrganizer = orgcontact.equals(organizercontact);
                            btnAddManually.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);
                            return;
                        }

                        final int[] processedCount = {0};

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String userId = snap.child("userId").getValue(String.class);

                            if (userId != null) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance()
                                        .getReference("tbl_Users")
                                        .child(userId);

                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnap) {
                                        if (userSnap.exists()) {
                                            String phone = userSnap.child("phone").getValue(String.class);
                                            if (phone != null) {
                                                officialPhones.add(phone);
                                            }
                                        }
                                        processedCount[0]++;
                                        if (processedCount[0] == totalOfficials) {
                                            evaluatePermissions(officialPhones, organizercontact);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        processedCount[0]++;
                                        if (processedCount[0] == totalOfficials) {
                                            evaluatePermissions(officialPhones, organizercontact);
                                        }
                                    }
                                });
                            } else {
                                processedCount[0]++;
                                if (processedCount[0] == totalOfficials) {
                                    evaluatePermissions(officialPhones, organizercontact);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error fetching officials", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void evaluatePermissions(List<String> officialPhones, String organizercontact) {
        boolean isOrganizer = orgcontact.equals(organizercontact);
        boolean isOfficial = officialPhones.contains(orgcontact);
        btnAddManually.setVisibility((isOrganizer || isOfficial) ? View.VISIBLE : View.GONE);
    }
}
