package com.example.shuttlescore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class TutorialFragment extends Fragment {

    public TutorialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

        Button playButton = view.findViewById(R.id.playButton);

        // Set the YouTube video URL
        ImageView videoThumbnail = view.findViewById(R.id.videoThumbnail);


        final String videoUrl = "https://www.youtube.com/watch?v=c5QLSN6M--M";

        videoThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube"); // Open in YouTube app if available
                startActivity(intent);
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube"); // Open in YouTube app if available
                startActivity(intent);
            }
        });

        return view;
    }
}
