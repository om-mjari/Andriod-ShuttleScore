package com.example.shuttlescore;

import java.io.Serializable;

public class tbl_team implements Serializable {
    private String teamId;
    private String teamName;
    private String city;
    private String captainName;
    private String captainContact;
    private long createdAt;
    private long deletedAt;
    private String tournamentId;

    public tbl_team() {
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCaptainName() {
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }

    public String getCaptainContact() {
        return captainContact;
    }

    public void setCaptainContact(String captainContact) {
        this.captainContact = captainContact;
    }

    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getTournamentId() { return tournamentId; }

    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }
}
