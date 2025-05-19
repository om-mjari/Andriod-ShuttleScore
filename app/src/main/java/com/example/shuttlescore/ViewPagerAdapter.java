package com.example.shuttlescore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String tournamentId;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String tournamentId) {
        super(fragmentActivity);
        this.tournamentId = tournamentId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Bundle args = new Bundle();
        args.putString("tournamentId", tournamentId);
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = new MatchesFragment();
                break;
            case 1:
                fragment = new TeamsFragment();
                break;
            case 2:
                fragment = new PointsFragment();
                break;
            default:
                fragment = new MatchesFragment();
                break;
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
