package com.example.shuttlescore;

public class tbl_double_team {
    private String teamName;
    private String city;
    private String captain1Name;
    private String captain1Number;
    private String captain2Name;
    private String captain2Number;
    private String key;
    private String tournamentId;

    public tbl_double_team() {}

    public tbl_double_team(String teamName, String city, String captain1Name, String captain1Number,
                           String captain2Name, String captain2Number, String key, String tournamentId) {
        this.teamName = teamName;
        this.city = city;
        this.captain1Name = captain1Name;
        this.captain1Number = captain1Number;
        this.captain2Name = captain2Name;
        this.captain2Number = captain2Number;
        this.key = key;
        this.tournamentId = tournamentId;
    }

    // Getters and Setters
    // Existing fields...

    public String getTeamId() {
        return key;
    }
    public String getTeamName() { return teamName; }
    public String getCity() { return city; }
    public String getCaptain1Name() { return captain1Name; }
    public String getCaptain1Number() { return captain1Number; }
    public String getCaptain2Name() { return captain2Name; }
    public String getCaptain2Number() { return captain2Number; }
    public String getKey() { return key; }
    public String getTournamentId() { return tournamentId; } // Getter for tournamentId

    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setCity(String city) { this.city = city; }
    public void setCaptain1Name(String captain1Name) { this.captain1Name = captain1Name; }
    public void setCaptain1Number(String captain1Number) { this.captain1Number = captain1Number; }
    public void setCaptain2Name(String captain2Name) { this.captain2Name = captain2Name; }
    public void setCaptain2Number(String captain2Number) { this.captain2Number = captain2Number; }
    public void setKey(String key) { this.key = key; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; } // Setter for tournamentId
}
