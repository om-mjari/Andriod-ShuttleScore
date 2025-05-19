package com.example.shuttlescore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AllMatchesFragment extends Fragment {

    public AllMatchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_matches, container, false);

        Button backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(v-> getParentFragmentManager().popBackStack());

        return view;
    }
}