package com.example.shuttlescore;

public class tbl_Tournament_reviews {
    public String reviewId;
    public String userId;

    public String tournamentId;
    public String feedback;
    public float rating;

    public String createdAt;


    public tbl_Tournament_reviews() {
    }

    public tbl_Tournament_reviews(String reviewId, String userId, String tournamentId,
                                  String feedback, float rating, String createdAt) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.tournamentId = tournamentId;
        this.feedback = feedback;
        this.rating = rating;
        this.createdAt = createdAt;
    }
}
