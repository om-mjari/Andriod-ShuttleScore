package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ListView tournamentListView;
    private TournamentAdapter adapter;
    private List<TournamentModel> tournamentList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tournamentListView = view.findViewById(R.id.tournamentListView);
        progressBar = view.findViewById(R.id.progressBar);

        tournamentList = new ArrayList<>();
        adapter = new TournamentAdapter(requireContext(), tournamentList);
        tournamentListView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("tbl_Tournaments");

        progressBar.setVisibility(View.VISIBLE);
        fetchTournaments();

        tournamentListView.setOnItemClickListener((parent, view1, position, id) -> {
            TournamentModel selectedTournament = tournamentList.get(position);
            Intent intent = new Intent(requireContext(), TournamentDetailActivity.class);
            intent.putExtra("tournamentId", selectedTournament.getTournamentId());
            intent.putExtra("tournamentName", selectedTournament.getTournamentName());
            intent.putExtra("groundName", selectedTournament.getGroundName());
            intent.putExtra("tournamentPhoto", selectedTournament.getTournamentPhoto());
            intent.putExtra("cityName", selectedTournament.getCityName());
            intent.putExtra("createdAt", selectedTournament.getCreatedAt());
            intent.putExtra("deletedAt", selectedTournament.getDeletedAt());
            intent.putExtra("organizerContactNo", selectedTournament.getOrganizerContactNo());
            intent.putExtra("organizerEmailId", selectedTournament.getOrganizerEmailId());
            intent.putExtra("organizerName", selectedTournament.getOrganizerName());
            intent.putExtra("shuttlecockType", selectedTournament.getShuttlecockType());
            intent.putExtra("singlesDoubles", selectedTournament.getSinglesDoubles());
            intent.putExtra("tournamentCategory", selectedTournament.getTournamentCategory());
            intent.putExtra("tournamentStartDate", selectedTournament.getTournamentStartDate());
            intent.putExtra("tournamentEndDate", selectedTournament.getTournamentEndDate());
            startActivity(intent);
        });

        return view;
    }

    private void fetchTournaments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tournamentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TournamentModel tournament = dataSnapshot.getValue(TournamentModel.class);
                    if (tournament != null) {
                        tournamentList.add(tournament);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
