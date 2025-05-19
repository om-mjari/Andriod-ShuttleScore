package com.example.shuttlescore;

public class tbl_ManageTournament {
    private String tournamentId;
    private String tournamentName;
    private String tournamentPhoto;
    private String groundName;
    private String cityName;
    private String organizerName;
    private String organizerContactNo;
    private String organizerEmailId;
    private long tournamentStartDate;
    private long tournamentEndDate;
    private String tournamentCategory;
    private String shuttlecockType;
    private String singlesDoubles;
    private long createdAt;
    private long deletedAt;

    public tbl_ManageTournament() {
    }

    public tbl_ManageTournament(String tournamentId, String tournamentName, String tournamentPhoto, String groundName,
                                String cityName, String organizerName, String organizerContactNo,
                                String organizerEmailId, long tournamentStartDate, long tournamentEndDate,
                                String tournamentCategory, String shuttlecockType, String singlesDoubles,
                                long createdAt, long deletedAt) {
        this.tournamentId = tournamentId;
        this.tournamentName = tournamentName;
        this.tournamentPhoto = tournamentPhoto;
        this.groundName = groundName;
        this.cityName = cityName;
        this.organizerName = organizerName;
        this.organizerContactNo = organizerContactNo;
        this.organizerEmailId = organizerEmailId;
        this.tournamentStartDate = tournamentStartDate;
        this.tournamentEndDate = tournamentEndDate;
        this.tournamentCategory = tournamentCategory;
        this.shuttlecockType = shuttlecockType;
        this.singlesDoubles = singlesDoubles;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

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

    public String getTournamentPhoto() {
        return tournamentPhoto;
    }

    public void setTournamentPhoto(String tournamentPhoto) {
        this.tournamentPhoto = tournamentPhoto;
    }

    public String getGroundName() {
        return groundName;
    }

    public void setGroundName(String groundName) {
        this.groundName = groundName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
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

    public String getTournamentCategory() {
        return tournamentCategory;
    }

    public void setTournamentCategory(String tournamentCategory) {
        this.tournamentCategory = tournamentCategory;
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
}
