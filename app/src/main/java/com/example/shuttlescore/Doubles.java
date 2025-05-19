package com.example.shuttlescore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class Doubles extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    DoublesPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doubles);

        tabLayout = findViewById(R.id.tabLayoutDoubles);
        viewPager = findViewById(R.id.viewPagerDoubles);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DoubleMatchesFragment());
        fragmentList.add(new DoubleTeamsFragment());
        fragmentList.add(new DoublePointsFragment());

        ArrayList<String> tabTitles = new ArrayList<>();
        tabTitles.add("MATCHES");
        tabTitles.add("TEAMS");
        tabTitles.add("POINTS");

        pagerAdapter = new DoublesPagerAdapter(this, fragmentList);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles.get(position))
        ).attach();
    }
}
