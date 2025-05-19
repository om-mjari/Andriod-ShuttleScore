package com.example.shuttlescore;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.shuttlescore.BasicRulesFragment;
import com.example.shuttlescore.ContactFragment;
import com.example.shuttlescore.FAQsFragment;
import com.example.shuttlescore.FeedbackFragment;
import com.example.shuttlescore.R;
import com.example.shuttlescore.TutorialFragment;

public class HelpFragment extends Fragment {

    private Button tutorial, contact, FAQs, feedback, basicrules;
    private ProgressBar progressBar;
    private LinearLayout helpContentLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        tutorial = view.findViewById(R.id.tutorial);
        contact = view.findViewById(R.id.contact);
        FAQs = view.findViewById(R.id.faqs);
        feedback = view.findViewById(R.id.feedback);
        basicrules = view.findViewById(R.id.basicrules);
        progressBar = view.findViewById(R.id.helpProgressBar);
        helpContentLayout = view.findViewById(R.id.helpContentLayout);

        helpContentLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new android.os.Handler().postDelayed(() -> {

            if (isAdded()) {
                progressBar.setVisibility(View.GONE);
                helpContentLayout.setVisibility(View.VISIBLE);
                loadFragment(new TutorialFragment());
            }
        }, 2000);


        View.OnClickListener listener = v -> {

            if (isAdded()) {
                if (v.getId() == R.id.tutorial) {
                    loadFragment(new TutorialFragment());
                } else if (v.getId() == R.id.basicrules) {
                    loadFragment(new BasicRulesFragment());
                } else if (v.getId() == R.id.contact) {
                    loadFragment(new ContactFragment());
                } else if (v.getId() == R.id.faqs) {
                    loadFragment(new FAQsFragment());
                } else if (v.getId() == R.id.feedback) {
                    loadFragment(new FeedbackFragment());
                }
            }
        };

        tutorial.setOnClickListener(listener);
        contact.setOnClickListener(listener);
        FAQs.setOnClickListener(listener);
        feedback.setOnClickListener(listener);
        basicrules.setOnClickListener(listener);

        return view;
    }

    private void loadFragment(Fragment fragment) {
        if (isAdded()) {  // Check if fragment is still added
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frameHelp, fragment);
            transaction.commit();
        }
    }
}
