package com.example.shuttlescore;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class TournamentPagerAdapter extends FragmentStateAdapter {

    private final String tournamentId;
    private final String tournamentName;
    private final String groundName;
    private final ArrayList<String> officialContactNos;

    public TournamentPagerAdapter(@NonNull AppCompatActivity fragmentActivity,
                                  String tournamentId,
                                  String tournamentName,
                                  String groundName,
                                  ArrayList<String> officialContactNos) {
        super(fragmentActivity);
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.groundName = groundName;
        this.officialContactNos = officialContactNos;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new MatchesFragment();
                break;
            case 1:
                fragment = new PointsFragment();
                break;
            case 2:
                fragment = new TeamsFragment();
                break;
            case 3:
                fragment = new SponsorFragment();
                break;
            default:
                fragment = new MatchesFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("tournamentId", tournamentId);
        bundle.putString("tournamentName", tournamentName);
        bundle.putString("groundName", groundName);
        bundle.putStringArrayList("officialContactNos", officialContactNos);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
