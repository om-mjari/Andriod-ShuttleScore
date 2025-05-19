package com.example.shuttlescore;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private static final String TAG = "FeedbackFragment";

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText feedbackText = view.findViewById(R.id.feedbackText);
        Button submitFeedback = view.findViewById(R.id.submitFeedback);

        submitFeedback.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login", MODE_PRIVATE);
            String contactNo = sharedPreferences.getString("contact", null);
            float ratingValue = ratingBar.getRating();
            String feedback = feedbackText.getText().toString().trim();

            if (contactNo == null || contactNo.isEmpty()) {
                Toast.makeText(getActivity(), "Contact number not found", Toast.LENGTH_SHORT).show();
                return;
            }

            if (feedback.isEmpty() || ratingValue == 0) {
                Toast.makeText(getActivity(), "Please enter feedback and rating", Toast.LENGTH_SHORT).show();
                return;
            }

            insertFeedback(contactNo, feedback, (int) ratingValue);
        });

        return view;
    }

    private void insertFeedback(String contactNo, String feedback, int ratingValue) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("tbl_Users").orderByChild("phone").equalTo(contactNo)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.getKey();

                            DatabaseReference feedbacksRef = database.child("tbl_App_Feedbacks");
                            String feedbackId = feedbacksRef.push().getKey();

                            if (feedbackId != null) {
                                Map<String, Object> feedbackData = new HashMap<>();
                                feedbackData.put("userId", userId);
                                feedbackData.put("feedback", feedback);
                                feedbackData.put("ratings", ratingValue);
                                feedbackData.put("createdAt", System.currentTimeMillis());

                                feedbacksRef.child(feedbackId).setValue(feedbackData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Reset UI elements properly
                                            View view = getView();
                                            if (view != null) {
                                                EditText feedbackText = view.findViewById(R.id.feedbackText);
                                                RatingBar ratingBar = view.findViewById(R.id.ratingBar);

                                                feedbackText.setText("");  // Clear EditText
                                                ratingBar.setRating(0);  // Reset RatingBar
                                            }

                                            Toast.makeText(getActivity(), "Thank you for feedback...", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Feedback successfully added.");
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getActivity(), "Failed to add feedback", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error adding feedback: ", e);
                                        });
                            }
                            break;
                        }
                    } else {
                        Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "No user found with contactNo: " + contactNo);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to fetch user", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching user: ", e);
                });
    }

}