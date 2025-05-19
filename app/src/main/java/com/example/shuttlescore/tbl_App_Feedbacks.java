package com.example.shuttlescore;

import com.google.firebase.firestore.DocumentReference;

public class tbl_App_Feedbacks {
    public DocumentReference userRef;
    private String feedback;
    private int ratings;
    private long createdAt;

    public tbl_App_Feedbacks() {
        // Required for Firebase deserialization
    }

    public tbl_App_Feedbacks(DocumentReference userRef, String feedback, int ratings, long createdAt) {
        this.userRef = userRef;
        this.feedback = feedback;
        this.ratings = ratings;
        this.createdAt = createdAt;
    }

    public DocumentReference getUserRef() {
        return userRef;
    }

    public String getFeedback() {
        return feedback;
    }

    public int getRatings() {
        return ratings;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
