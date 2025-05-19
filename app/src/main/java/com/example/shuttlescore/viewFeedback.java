package com.example.shuttlescore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class viewFeedback extends AppCompatActivity {

    ListView listFeedbacks;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_feedback);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listFeedbacks = findViewById(R.id.listFeedbacks);
        back = findViewById(R.id.back);


        onclickbutton();

        Intent i = getIntent();
        String tournamentId = i.getStringExtra("tournamentId");
        String tournamentName = i.getStringExtra("tournamentName");
        Log.d("TournamentIDCheck", "Tournament ID: " + tournamentId);

        back.setOnClickListener(v -> {
            Intent in = new Intent(getApplicationContext(), TournamentFeedback.class);
            in.putExtra("tournamentId", tournamentId);
            in.putExtra("tournamentName",tournamentName);
            startActivity(in);
        });
        fetchTournamentReviews(tournamentId);
    }

    public void onclickbutton() {
        // Add any future functionality here
    }

    private void fetchTournamentReviews(String tournamentId) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getTournamentReviews(tournamentId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> feedbackList = new ArrayList<>();
                long totalReviews = snapshot.getChildrenCount();
                Log.d("FeedbackCheck", "Total feedbacks: " + totalReviews);

                if (totalReviews == 0) {
                    Toast.makeText(getApplicationContext(), "No feedbacks found", Toast.LENGTH_SHORT).show();
                    return;
                }

                final int[] loadedCount = {0};

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String contactNo = snap.child("userId").getValue(String.class); // your saved contact
                    String feedback  = snap.child("feedback").getValue(String.class);
                    Long ratingLong  = snap.child("ratings").getValue(Long.class);
                    int    rating    = ratingLong != null ? ratingLong.intValue() : 0;

                    Log.d("FeedbackData", "contactNo=" + contactNo + ", rating=" + rating + ", feedback=" + feedback);

                    if (contactNo == null) {
                        loadedCount[0]++;
                        if (loadedCount[0] == totalReviews) updateList(feedbackList);
                        continue;
                    }

                    Query userQuery = FirebaseDatabase.getInstance()
                            .getReference("tbl_Users")
                            .orderByChild("phone")
                            .equalTo(contactNo);

                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            String username = "Unknown User";

                            if (userSnapshot.exists()) {
                                // Grab the first matching user
                                DataSnapshot userSnap = userSnapshot.getChildren().iterator().next();
                                String name = userSnap.child("name").getValue(String.class);
                                if (name != null) username = name;
                                Log.d("UserFetch", "Found user " + userSnap.getKey() + " → " + username);
                            } else {
                                Log.w("UserFetch", "No user with contact " + contactNo);
                            }

                            String combined = "\nUser: " + username +
                                    "\n\nRating: " + rating + "/5" +
                                    "\n\nFeedback: " + feedback+"\n";
                            feedbackList.add(combined);

                            loadedCount[0]++;
                            if (loadedCount[0] == totalReviews) {
                                updateList(feedbackList);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("UserFetch", "Query cancelled", error.toException());
                            loadedCount[0]++;
                            if (loadedCount[0] == totalReviews) {
                                updateList(feedbackList);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchFeedback", "Error loading reviews", error.toException());
                Toast.makeText(getApplicationContext(), "Error loading feedbacks", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateList(List<String> feedbackList) {
        Log.d("FinalFeedbackList", "Updating list with " + feedbackList.size() + " items.");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                viewFeedback.this,
                android.R.layout.simple_list_item_1,
                feedbackList
        );
        listFeedbacks.setAdapter(adapter);
    }
}
