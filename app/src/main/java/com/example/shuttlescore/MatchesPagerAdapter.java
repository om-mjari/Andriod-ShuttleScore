package com.example.shuttlescore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class MatchesPagerAdapter extends FragmentStateAdapter {

    private final String tournamentId;
    private final String tournamentName;
    private final String groundName;
    private ArrayList<String> contactNos;

    public MatchesPagerAdapter(@NonNull Fragment fragment, String tournamentId,String tournamentName,
                               String groundName,ArrayList<String> contactNos) {
        super(fragment);
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.groundName = groundName;
        this.contactNos = contactNos;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new LiveMatchesFragment();
                break;
            case 1:
                fragment = new UpcomingMatchesFragment();
                break;
            case 2:
                fragment = new PastMatchesFragment();
                break;
            default:
                fragment = new LiveMatchesFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putString("tournamentId", tournamentId);
        bundle.putString("tournamentName", tournamentName);
        bundle.putString("groundName", groundName);
        bundle.putStringArrayList("officialContactNos", contactNos);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

