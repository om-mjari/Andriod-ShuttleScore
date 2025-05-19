package com.example.shuttlescore;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MatchesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private String tournamentId,tournamentName,groundName;
    ArrayList<String> contactNos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches, container, false);

        tournamentId = getArguments().getString("tournamentId");
        tournamentName = getArguments().getString("tournamentName");
        groundName = getArguments().getString("groundName");
        contactNos = getArguments().getStringArrayList("officialContactNos");

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        MatchesPagerAdapter adapter = new MatchesPagerAdapter(this, tournamentId,tournamentName,groundName,contactNos);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Live");
                    break;
                case 1:
                    tab.setText("Upcoming");
                    break;
                case 2:
                    tab.setText("Past");
                    break;
            }
        }).attach();

        return view;
    }
}
