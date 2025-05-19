package com.example.shuttlescore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class DoublesPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList;

    public DoublesPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.fragmentList = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
