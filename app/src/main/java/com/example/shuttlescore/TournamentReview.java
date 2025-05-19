package com.example.shuttlescore;

public class TournamentReview {
    public String id;
    public String userId;
    public String username;
    public String feedback;
    public int rating;
    public long createdAt;

    // Required empty constructor
    public TournamentReview() {}

    public TournamentReview(
            String id,
            String userId,
            String username,
            String feedback,
            int rating,
            long createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.feedback = feedback;
        this.rating = rating;
        this.createdAt = createdAt;
    }
}
