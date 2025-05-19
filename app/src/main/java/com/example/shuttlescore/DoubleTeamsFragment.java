package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class DoubleTeamsFragment extends Fragment {

    Button btnAddManually;
    ListView listDoubleTeams;
    ArrayList<tbl_double_team> teamList;
    DoubleTeamAdapter adapter;
    DatabaseReference doubleTeamRef;

    int selectedPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_double_teams, container, false);

        btnAddManually = view.findViewById(R.id.btnAddManually);
        listDoubleTeams = view.findViewById(R.id.listDoubleTeams);

        teamList = new ArrayList<>();
        adapter = new DoubleTeamAdapter(getActivity(), teamList);
        listDoubleTeams.setAdapter(adapter);

        doubleTeamRef = FirebaseDatabase.getInstance().getReference("tbl_Double_Team");

        loadDoubleTeams();

        btnAddManually.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddDoubleTeamActivity.class));
        });

        // Register for context menu
        registerForContextMenu(listDoubleTeams);

        // Capture selected item on long press (used for context menu)
        listDoubleTeams.setOnItemLongClickListener((parent, view1, position, id) -> {
            selectedPosition = position;
            return false; // Required for context menu to show
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listDoubleTeams) {
            super.onCreateContextMenu(menu, v, menuInfo);
            menu.setHeaderTitle("Select Action");
            menu.add(0, 1, 0, "Update");
            menu.add(0, 2, 1, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedPosition == -1 || selectedPosition >= teamList.size()) return super.onContextItemSelected(item);

        tbl_double_team selectedTeam = teamList.get(selectedPosition);

        switch (item.getItemId()) {
            case 1: // Update
                Intent intent = new Intent(getActivity(), UpdateDoubleTeamActivity.class);
                intent.putExtra("teamKey", selectedTeam.getKey());
                intent.putExtra("teamName", selectedTeam.getTeamName());
                intent.putExtra("city", selectedTeam.getCity());
                intent.putExtra("captain1Name", selectedTeam.getCaptain1Name());
                intent.putExtra("captain1Number", selectedTeam.getCaptain1Number());
                intent.putExtra("captain2Name", selectedTeam.getCaptain2Name());
                intent.putExtra("captain2Number", selectedTeam.getCaptain2Number());
                startActivity(intent);
                break;

            case 2: // Delete
                doubleTeamRef.child(selectedTeam.getKey()).removeValue();
                Toast.makeText(getActivity(), "Team Deleted", Toast.LENGTH_SHORT).show();
                break;
        }

        selectedPosition = -1;
        return true;
    }

    private void loadDoubleTeams() {
        doubleTeamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                teamList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    tbl_double_team team = snap.getValue(tbl_double_team.class);
                    if (team != null) {
                        team.setKey(snap.getKey());
                        teamList.add(team);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
