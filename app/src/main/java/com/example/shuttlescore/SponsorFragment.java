package com.example.shuttlescore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SponsorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SponsorFragment extends Fragment {
    String tournamentId;
    private ListView lssponsors;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SponsorFragment() {
    }

    public static SponsorFragment newInstance(String param1, String param2) {
        SponsorFragment fragment = new SponsorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sponsor, container, false);

        lssponsors = view.findViewById(R.id.lssponsors);
        ArrayList<tbl_Sponsor> sponsorList = new ArrayList<>();
        SponsorAdapter sponsorAdapter = new SponsorAdapter(getContext(), sponsorList);
        lssponsors.setAdapter(sponsorAdapter);

        if (getArguments() != null) {
            tournamentId = getArguments().getString("tournamentId");
            DatabaseReference sponsorRef = FirebaseDatabase.getInstance().getReference("tbl_Sponsor");

            sponsorRef.orderByChild("tournamentId").equalTo(tournamentId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            sponsorList.clear();
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                tbl_Sponsor sponsor = snap.getValue(tbl_Sponsor.class);
                                if (sponsor != null) {
                                    sponsorList.add(sponsor);
                                }
                            }
                            sponsorAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error fetching sponsors", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return view;
    }

}