package com.example.shuttlescore;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Singles extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_singles);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        String tournamentId = getIntent().getStringExtra("tournamentId");
        adapter = new ViewPagerAdapter(this,tournamentId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) ->
                {
                    switch (position) {
                        case 0:
                            tab.setText("MATCHS");
                            break;
                        case 1:
                            tab.setText("TEAMS");
                            break;
                        case 2:
                            tab.setText("POINTS");
                            break;
                    }
                }).attach();
    }
}
