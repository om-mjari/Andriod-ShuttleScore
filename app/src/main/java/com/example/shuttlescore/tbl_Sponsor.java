package com.example.shuttlescore;

public class tbl_Sponsor {
    private String sponsorId;
    private String sponsorName;
    private String sponsorContactNo;
    private String sponsorDescription;
    private String tournamentId;

    // Public no-arg constructor (required for Firebase)
    public tbl_Sponsor() { }

    // All-args constructor
    public tbl_Sponsor(String sponsorId,
                       String sponsorName,
                       String sponsorContactNo,
                       String sponsorDescription,
                       String tournamentId) {
        this.sponsorId = sponsorId;
        this.sponsorName = sponsorName;
        this.sponsorContactNo = sponsorContactNo;
        this.sponsorDescription = sponsorDescription;
        this.tournamentId = tournamentId;
    }

    // Getters (and setters if needed)
    public String getSponsorId() {
        return sponsorId;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public String getSponsorContactNo() {
        return sponsorContactNo;
    }

    public String getSponsorDescription() {
        return sponsorDescription;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public void setSponsorContactNo(String sponsorContactNo) {
        this.sponsorContactNo = sponsorContactNo;
    }

    public void setSponsorDescription(String sponsorDescription) {
        this.sponsorDescription = sponsorDescription;
    }

}
