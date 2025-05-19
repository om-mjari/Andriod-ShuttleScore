package com.example.shuttlescore;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private DatabaseReference database;
    private DatabaseReference tournamentsDatabase;
    private DatabaseReference teamsDatabase;
    private DatabaseReference doubleTeamsDatabase;
    private DatabaseReference matches;
    private DatabaseReference livescore;
    private DatabaseReference feedback;
    private DatabaseReference tournamentReviewsDatabase;
    private DatabaseReference officialsDatabase;
    private final DatabaseReference sponsorsDatabase;
    private static final String TAG = "FirebaseHelper";

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance().getReference("tbl_Users");
        tournamentsDatabase = FirebaseDatabase.getInstance().getReference("tbl_Tournaments");
        teamsDatabase = FirebaseDatabase.getInstance().getReference("tbl_Team");
        doubleTeamsDatabase = FirebaseDatabase.getInstance().getReference("tbl_Double_Team");
        matches = FirebaseDatabase.getInstance().getReference("tbl_matches");
        livescore = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score");
        feedback = FirebaseDatabase.getInstance().getReference("tbl_App_Feedbacks");
        feedback = FirebaseDatabase.getInstance().getReference("tbl_App_Feedbacks");
        tournamentReviewsDatabase = FirebaseDatabase.getInstance().getReference("tbl_Tournament_Reviews");
        officialsDatabase = FirebaseDatabase.getInstance().getReference("tbl_Officials");
        sponsorsDatabase = FirebaseDatabase.getInstance().getReference("tbl_Sponsor");
    }


    public void addUserWithMap(String userId,String name, String email,String address, String phone,String gender, String handedness,long dateOfBirth, long createdAt, Long deletedAt) {
//        String userid = database.push().getKey();
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", userId);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("gender", gender);
        userData.put("address",address);
        userData.put("handedness", handedness);
        userData.put("dateOfBirth", dateOfBirth);
        userData.put("createdAt", createdAt);
        userData.put("deletedAt", deletedAt);

        database.child(userId).updateChildren(userData)
                .addOnSuccessListener(aVoid -> System.out.println("Data Inserted"))
                .addOnFailureListener(e -> System.err.println("Failed: " + e.getMessage()));
    }

    public void addTournament(String tournamentName, String tournamentPhoto, String groundName,
                              String cityName, String organizerName, String organizerContactNo,
                              String organizerEmailId, long tournamentStartDate, long tournamentEndDate,
                              String tournamentCategory, String shuttlecockType, String singlesDoubles,
                              long createdAt, long deletedAt,
                              TournamentCallback callback) {

        String tournamentId = tournamentsDatabase.push().getKey();
        Map<String, Object> tournamentData = new HashMap<>();
        tournamentData.put("tournamentId", tournamentId);
        tournamentData.put("tournamentName", tournamentName);
        tournamentData.put("tournamentPhoto", tournamentPhoto);
        tournamentData.put("groundName", groundName);
        tournamentData.put("cityName", cityName);
        tournamentData.put("organizerName", organizerName);
        tournamentData.put("organizerContactNo", organizerContactNo);
        tournamentData.put("organizerEmailId", organizerEmailId);
        tournamentData.put("tournamentStartDate", tournamentStartDate);
        tournamentData.put("tournamentEndDate", tournamentEndDate);
        tournamentData.put("tournamentCategory", tournamentCategory);
        tournamentData.put("shuttlecockType", shuttlecockType);
        tournamentData.put("singlesDoubles", singlesDoubles);
        tournamentData.put("createdAt", createdAt);
        tournamentData.put("deletedAt", deletedAt);

        if (tournamentId != null) {
            tournamentsDatabase.child(tournamentId).updateChildren(tournamentData)
                    .addOnSuccessListener(aVoid -> {
                        if (callback != null) callback.onTournamentAdded(tournamentId);
                    })
                    .addOnFailureListener(e -> {
                        if (callback != null) callback.onTournamentAddFailed(e.getMessage());
                    });
        } else {
            if (callback != null) callback.onTournamentAddFailed("Tournament ID is null");
        }
    }

    public void updateMatchStatus(String matchId, String matchStatus) {
    }

    public void endMatch(String matchId) {
    }

    public void updateLiveScore(String matchId, int scoreA, int scoreB, int currentSet) {
    }


    public interface TournamentCallback {
        void onTournamentAdded(String tournamentId);
        void onTournamentAddFailed(String errorMessage);
    }

    public void addTeam(String teamName, String city,
                        String captainName, String captainContact,String tournamentId,
                        long createdAt, long deletedAt) {

        String teamId = teamsDatabase.push().getKey();
        Map<String, Object> teamData = new HashMap<>();
        teamData.put("teamName", teamName);
        teamData.put("city", city);
        teamData.put("captainName", captainName);
        teamData.put("captainContact", captainContact);
        teamData.put("tournamentId", tournamentId);
        teamData.put("createdAt", createdAt);
        teamData.put("deletedAt", deletedAt);

        if (teamId != null) {
            teamsDatabase.child(teamId).updateChildren(teamData)
                    .addOnSuccessListener(aVoid -> System.out.println("Team Added Successfully"))
                    .addOnFailureListener(e -> System.err.println("Failed to Add Team: " + e.getMessage()));
        }
    }

    public void addFeedback(String userId, String feedback, int ratings, long createdAt) {
        DatabaseReference feedbacksDatabase = FirebaseDatabase.getInstance().getReference("tbl_App_Feedbacks");

        String feedbackId = feedbacksDatabase.push().getKey();

        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("userId", userId);
        feedbackData.put("feedback", feedback);
        feedbackData.put("ratings", ratings);
        feedbackData.put("createdAt", createdAt);

        if (feedbackId != null) {
            feedbacksDatabase.child(feedbackId).updateChildren(feedbackData)
                    .addOnSuccessListener(aVoid -> System.out.println("Feedback Added Successfully"))
                    .addOnFailureListener(e -> System.err.println("Failed to add feedback: " + e.getMessage()));
        }
    }

    public interface MatchInsertCallback {
        void onMatchInserted(String matchId);
    }
    public void addMatch(String tournamentId, String scheduleTime,
                         String gameMode, String gamePoints,
                         String pl1id, String pl2id,
                         String pl3id, String pl4id,
                         String matchFormat, String matchStatus,
                         MatchInsertCallback callback) {

        DatabaseReference matchesDatabase = FirebaseDatabase.getInstance().getReference("tbl_matches");

        String matchId = matchesDatabase.push().getKey();

        if (matchId != null) {

            Map<String, Object> matchData = new HashMap<>();
            matchData.put("matchId", matchId);
            matchData.put("tournamentId", tournamentId);
            matchData.put("scheduleTime", scheduleTime);
            matchData.put("gameMode", gameMode);
            matchData.put("gamePoints", gamePoints);
            matchData.put("pl1id", pl1id);
            matchData.put("pl2id", pl2id);
            matchData.put("pl3id", pl3id);
            matchData.put("pl4id", pl4id);
            matchData.put("matchFormat", matchFormat);
            matchData.put("matchStatus", matchStatus);

            matchesDatabase.child(matchId).updateChildren(matchData)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Match added successfully");
                        callback.onMatchInserted(matchId);
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Failed to add match: " + e.getMessage());
                        callback.onMatchInserted(null);
                    });

        } else {
            callback.onMatchInserted(null);
        }
    }

    public void addtournamentMatch(String tournamentId, String scheduleTime,
                         String gameMode, String gamePoints,
                         String team1id, String team2id,
                         String matchFormat,String matchtype, String matchStatus,
                         MatchInsertCallback callback) {

        DatabaseReference matchesDatabase = FirebaseDatabase.getInstance().getReference("tbl_tournament_matches");

        String matchId = matchesDatabase.push().getKey();

        if (matchId != null) {

            Map<String, Object> matchData = new HashMap<>();
            matchData.put("matchId", matchId);
            matchData.put("tournamentId", tournamentId);
            matchData.put("scheduleTime", scheduleTime);
            matchData.put("gameMode", gameMode);
            matchData.put("gamePoints", gamePoints);
            matchData.put("team1id", team1id);
            matchData.put("team2id", team2id);
            matchData.put("matchFormat", matchFormat);
            matchData.put("matchtype", matchtype);
            matchData.put("matchStatus", matchStatus);

            matchesDatabase.child(matchId).updateChildren(matchData)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Match added successfully");
                        callback.onMatchInserted(matchId);
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Failed to add match: " + e.getMessage());
                        callback.onMatchInserted(null);
                    });

        } else {
            callback.onMatchInserted(null);
        }
    }

    public void addLiveScore(Context context, String liveScoreId, String matchId,
                             int team1Score, int team2Score,
                             int team1Set1, int team2Set1,
                             int team1Set2, int team2Set2,
                             int team1Set3, int team2Set3,
                             String matchStatus) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Tbl_Live_Score");

        if (liveScoreId == null || liveScoreId.isEmpty()) {
            liveScoreId = dbRef.push().getKey();
        }

        HashMap<String, Object> scoreData = new HashMap<>();
        scoreData.put("Live_Score_Id", liveScoreId);
        scoreData.put("matchId", matchId);
        scoreData.put("Team1_Score", team1Score);
        scoreData.put("Team2_Score", team2Score);
        scoreData.put("Team1_Set1", team1Set1);
        scoreData.put("Team2_Set1", team2Set1);
        scoreData.put("Team1_Set2", team1Set2);
        scoreData.put("Team2_Set2", team2Set2);
        scoreData.put("Team1_Set3", team1Set3);
        scoreData.put("Team2_Set3", team2Set3);
        scoreData.put("matchStatus", matchStatus);

        dbRef.child(liveScoreId).setValue(scoreData);
    }

    public void addDoubleTeam(String teamName, String city,
                              String captain1Name, String captain1Contact,
                              String captain2Name, String captain2Contact,
                              long createdAt, Long deletedAt,String tournamentId) {

        String teamId = doubleTeamsDatabase.push().getKey();
        Map<String, Object> teamData = new HashMap<>();
        teamData.put("teamName", teamName);
        teamData.put("city", city);
        teamData.put("captain1Name", captain1Name);
        teamData.put("captain1Contact", captain1Contact);
        teamData.put("captain2Name", captain2Name);
        teamData.put("captain2Contact", captain2Contact);
        teamData.put("createdAt", createdAt);
        teamData.put("deletedAt", deletedAt);
        teamData.put("tournamentId", tournamentId);
        teamData.put("key", teamId);

        if (teamId != null) {
            doubleTeamsDatabase.child(teamId).updateChildren(teamData)
                    .addOnSuccessListener(aVoid -> System.out.println("Double Team Added Successfully"))
                    .addOnFailureListener(e -> System.err.println("Failed to Add Double Team: " + e.getMessage()));
        }
    }

    public void addTournamentReview(String userId, String tournamentId,
                                    String feedback, int ratings, long createdAt) {

        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("tbl_Tournament_Reviews");

        String reviewId = reviewRef.push().getKey();

        if (reviewId == null) {
            System.err.println("Failed to generate a review ID.");
            return;
        }

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("id", reviewId);
        reviewData.put("userId", userId);
        reviewData.put("tournamentId", tournamentId);
        reviewData.put("feedback", feedback);
        reviewData.put("ratings", ratings);
        reviewData.put("createdAt", createdAt);

        reviewRef.child(reviewId).setValue(reviewData)
                .addOnSuccessListener(aVoid -> System.out.println("Tournament Review Added Successfully"))
                .addOnFailureListener(e -> System.err.println("Failed to add tournament review: " + e.getMessage()));
    }


    public void getTournamentReviews(String tournamentId, ValueEventListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tbl_Tournament_Reviews");
        ref.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(listener);
    }

    public void addOfficial(String userId, String tournamentId) {
        if (userId == null || tournamentId == null) {
            Log.e(TAG, "addOfficial: userId or tournamentId is null");
            return;
        }


        String officialId = officialsDatabase.push().getKey();
        if (officialId == null) {
            Log.e(TAG, "addOfficial: failed to get push key");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("officialId",   officialId);
        data.put("userId",       userId);
        data.put("tournamentId", tournamentId);
        data.put("role",         "Scorer");

        officialsDatabase.child(officialId)
                .setValue(data)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "addOfficial: wrote " + officialId))
                .addOnFailureListener(e ->
                        Log.e(TAG, "addOfficial: failed", e));
    }

    public void deleteTournamentById(String tournamentId) {
        tournamentsDatabase.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting tournament: " + error.getMessage());
                    }
                });

        teamsDatabase.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting teams: " + error.getMessage());
                    }
                });

        doubleTeamsDatabase.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting double teams: " + error.getMessage());
                    }
                });

        FirebaseDatabase.getInstance().getReference("tbl_tournament_matches")
                .orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting tournament matches: " + error.getMessage());
                    }
                });

        tournamentReviewsDatabase.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting tournament reviews: " + error.getMessage());
                    }
                });

        FirebaseDatabase.getInstance().getReference("tbl_officials")
                .orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting tournament officials: " + error.getMessage());
                    }
                });
        sponsorsDatabase.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error deleting teams: " + error.getMessage());
                    }
                });
    }

    public interface FirebaseCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static void removeOfficialByPhone(String phone, String tournamentId, FirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference("tbl_Officials")
                .orderByChild("tournamentId")
                .equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot officialSnapshot : snapshot.getChildren()) {
                            String userId = officialSnapshot.child("userId").getValue(String.class);
                            FirebaseDatabase.getInstance().getReference("tbl_Users").child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                            String userPhone = userSnapshot.child("phone").getValue(String.class);
                                            if (phone.equals(userPhone)) {
                                                officialSnapshot.getRef().removeValue()
                                                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                                                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            callback.onFailure("Error deleting official: " + error.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure("Error fetching officials for removal: " + error.getMessage());
                    }
                });
    }
    public static void removeSponsorByPhone(String contactNo, String tournamentId, FirebaseCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tbl_Sponsor");
        ref.orderByChild("tournamentId").equalTo(tournamentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean found = false;
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            tbl_Sponsor sponsor = snap.getValue(tbl_Sponsor.class);
                            if (sponsor != null && contactNo.equals(sponsor.getSponsorContactNo())) {
                                snap.getRef().removeValue().addOnSuccessListener(aVoid -> {
                                    callback.onSuccess();
                                }).addOnFailureListener(e -> {
                                    callback.onFailure(e.getMessage());
                                });
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            callback.onFailure("Sponsor not found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

}


