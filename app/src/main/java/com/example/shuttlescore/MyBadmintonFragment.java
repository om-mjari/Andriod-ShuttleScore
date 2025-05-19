package com.example.shuttlescore;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MyBadmintonFragment extends Fragment {

    private ProgressBar progressBar;

    public MyBadmintonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_badminton, container, false);

        Button firstButton = view.findViewById(R.id.firstButton);
        Button secondButton = view.findViewById(R.id.secondButton);
        progressBar = view.findViewById(R.id.progressBar); // Reference to ProgressBar

        firstButton.setOnClickListener(v -> showLoadingAndLoadFragment(new MyTournamentFragment()));
        secondButton.setOnClickListener(v -> showLoadingAndLoadFragment(new MyMatchesFragment()));

        firstButton.performClick();
        return view;
    }

    private void showLoadingAndLoadFragment(Fragment fragment) {
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(() -> {
            if (isAdded()) {
                loadFragment(fragment);
                progressBar.setVisibility(View.GONE);
            }
        }, 500);
    }

    private void loadFragment(Fragment fragment) {
        if (isAdded()) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }
    }
}
