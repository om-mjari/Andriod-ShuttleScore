package com.example.shuttlescore;

public class TournamentModel {
    private String tournamentId;
    private String tournamentName;
    private String groundName;
    private String tournamentPhoto;

    private String cityName;
    private long createdAt;
    private long deletedAt;

    private String organizerContactNo;
    private String organizerEmailId;
    private String organizerName;

    private String shuttlecockType;
    private String singlesDoubles;
    private String tournamentCategory;

    private long tournamentStartDate;
    private long tournamentEndDate;

    // Default constructor required for Firebase
    public TournamentModel() {
    }
    public TournamentModel(String tournamentId,
                           String tournamentName,
                           String groundName,
                           String tournamentPhoto,
                           String cityName,
                           long createdAt,
                           long deletedAt,
                           String organizerContactNo,
                           String organizerEmailId,
                           String organizerName,
                           String shuttlecockType,
                           String singlesDoubles,
                           String tournamentCategory,
                           long tournamentStartDate,
                           long tournamentEndDate) {
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.groundName = groundName;
        this.tournamentPhoto = tournamentPhoto;
        this.cityName = cityName;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.organizerContactNo = organizerContactNo;
        this.organizerEmailId = organizerEmailId;
        this.organizerName = organizerName;
        this.shuttlecockType = shuttlecockType;
        this.singlesDoubles = singlesDoubles;
        this.tournamentCategory = tournamentCategory;
        this.tournamentStartDate = tournamentStartDate;
        this.tournamentEndDate = tournamentEndDate;
    }
    // Getters and Setters
    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getGroundName() {
        return groundName;
    }

    public void setGroundName(String groundName) {
        this.groundName = groundName;
    }

    public String getTournamentPhoto() {
        return tournamentPhoto;
    }

    public void setTournamentPhoto(String tournamentPhoto) {
        this.tournamentPhoto = tournamentPhoto;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getOrganizerContactNo() {
        return organizerContactNo;
    }

    public void setOrganizerContactNo(String organizerContactNo) {
        this.organizerContactNo = organizerContactNo;
    }

    public String getOrganizerEmailId() {
        return organizerEmailId;
    }

    public void setOrganizerEmailId(String organizerEmailId) {
        this.organizerEmailId = organizerEmailId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getShuttlecockType() {
        return shuttlecockType;
    }

    public void setShuttlecockType(String shuttlecockType) {
        this.shuttlecockType = shuttlecockType;
    }

    public String getSinglesDoubles() {
        return singlesDoubles;
    }

    public void setSinglesDoubles(String singlesDoubles) {
        this.singlesDoubles = singlesDoubles;
    }

    public String getTournamentCategory() {
        return tournamentCategory;
    }

    public void setTournamentCategory(String tournamentCategory) {
        this.tournamentCategory = tournamentCategory;
    }

    public long getTournamentStartDate() {
        return tournamentStartDate;
    }

    public void setTournamentStartDate(long tournamentStartDate) {
        this.tournamentStartDate = tournamentStartDate;
    }

    public long getTournamentEndDate() {
        return tournamentEndDate;
    }

    public void setTournamentEndDate(long tournamentEndDate) {
        this.tournamentEndDate = tournamentEndDate;
    }
}
