package com.example.shuttlescore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

public class ContactFragment extends Fragment {

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // Phone Call Functionality
        LinearLayout phoneLayout = view.findViewById(R.id.phoneLayout);
        phoneLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+917874426640"));
            startActivity(intent);
        });

        // Email Functionality
        LinearLayout emailLayout = view.findViewById(R.id.emailLayout);
        emailLayout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:shuttlescoreofficial@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry");
            startActivity(intent);
        });

        // Instagram Functionality
        LinearLayout instagramLayout = view.findViewById(R.id.instagramLayout);
        instagramLayout.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.instagram.com/Shuttle_Score_Official");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.instagram.android");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, uri)); // Open in browser
            }
        });

        // Facebook Functionality
        LinearLayout facebookLayout = view.findViewById(R.id.facebookLayout);
        facebookLayout.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.facebook.com/profile.php?id=61574639818245");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.facebook.katana");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, uri)); // Open in browser
            }
        });

        return view;
    }
}
