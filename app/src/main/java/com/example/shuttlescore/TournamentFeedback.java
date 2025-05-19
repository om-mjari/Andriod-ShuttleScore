package com.example.shuttlescore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TournamentFeedback extends AppCompatActivity {

    TextView tournamentNameFeedback,tournamentFeedback;
    RatingBar ratingBarTournament;
    Button addFeedback,viewFeedback;

    EditText WriteFeedback;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tournament_feedback);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing or show a toast
                // Toast.makeText(Login.this, "Back is disabled on login screen", Toast.LENGTH_SHORT).show();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tournamentNameFeedback = findViewById(R.id.tournamentNameFeedback);
        back = findViewById(R.id.back);
        ratingBarTournament = findViewById(R.id.ratingBarTournament);
        addFeedback = findViewById(R.id.addFeedback);
        tournamentFeedback = findViewById(R.id.tournamentFeedback);
        WriteFeedback = findViewById(R.id.WriteFeedback);
        viewFeedback = findViewById(R.id.viewFeedback);

        Intent i = getIntent();
        String tournamentId = i.getStringExtra("tournamentId");
        String tournamentName = i.getStringExtra("tournamentName");
        String groundName = i.getStringExtra("groundName");
        tournamentNameFeedback.setText(tournamentName);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),TournamentDetailActivity.class);
                i.putExtra("tournamentId",tournamentId);
                i.putExtra("tournamentName",tournamentName);
                i.putExtra("groundName",groundName);
                startActivity(i);
            }
        });
        viewFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(),viewFeedback.class);
                i.putExtra("tournamentId",tournamentId);
                i.putExtra("tournamentName",tournamentName);
                startActivity(i);
            }
        });
        onclickbutton();

    }

    private void onclickbutton() {
        addFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sh = getSharedPreferences("login", MODE_PRIVATE);
                String userId = sh.getString("contact", "");
                String tournamentId = getIntent().getStringExtra("tournamentId");

                String feedbackText = WriteFeedback.getText().toString().trim();
                int rating = (int) ratingBarTournament.getRating();
                long createdAt = System.currentTimeMillis();

                if (feedbackText.isEmpty() || rating == 0) {
                    WriteFeedback.setError("Please write something and give a rating");
                    return;
                }

                FirebaseHelper firebaseHelper = new FirebaseHelper();
                firebaseHelper.addTournamentReview(
                        userId,
                        tournamentId,
                        feedbackText,
                        rating,
                        createdAt
                );

                Toast.makeText(getApplicationContext(),"Feedback Added",Toast.LENGTH_SHORT).show();
                WriteFeedback.setText("");
                ratingBarTournament.setRating(0);
            }
        });

    }

}