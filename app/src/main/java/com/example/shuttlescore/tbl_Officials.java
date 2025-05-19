package com.example.shuttlescore;

public class tbl_Officials {
    private String officialId;
    private String userId;
    private String tournamentId;  // New field
    private String role;

    // Empty constructor required by Firebase
    public tbl_Officials() {
    }

    // Constructor including the new tournamentId
    public tbl_Officials(String officialId, String userId, String tournamentId) {
        this.officialId = officialId;
        this.userId = userId;
        this.tournamentId = tournamentId;
        this.role = "Scorer";
    }

    // Getters & setters

    public String getOfficialId() {
        return officialId;
    }

    public void setOfficialId(String officialId) {
        this.officialId = officialId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
