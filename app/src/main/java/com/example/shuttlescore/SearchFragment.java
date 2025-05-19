package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class SearchFragment extends Fragment {

    private SearchView searchView;
    private ListView lvTournaments, searcList;
    private ArrayList<TournamentModel> tournamentList;
    private TournamentAdapter adapter;
    private DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        lvTournaments = view.findViewById(R.id.searcList);
        searcList = view.findViewById(R.id.searcList);

        tournamentList = new ArrayList<>();
        adapter = new TournamentAdapter(getContext(), tournamentList);
        lvTournaments.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("tbl_Tournaments");

        // Set up SearchView
        searchView.setIconifiedByDefault(false);
        searchView.clearFocus();
        searchView.requestFocus();

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTournaments(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    searchTournaments(newText);

                    searcList.setOnItemClickListener((parent, view1, position, id) -> {
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
                } else {
                    tournamentList.clear();
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        return view;
    }

    private void searchTournaments(String name) {
        String query = name.toLowerCase(); // Convert input to lowercase

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tournamentList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        TournamentModel tournament = snap.getValue(TournamentModel.class);
                        if (tournament != null && tournament.getTournamentName() != null) {
                            String tournamentName = tournament.getTournamentName().toLowerCase();
                            if (tournamentName.contains(query)) { // Case-insensitive check
                                tournamentList.add(tournament);
                            }
                        }
                    }

                    if (tournamentList.isEmpty()) {
                        Toast.makeText(getContext(), "No tournaments found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}