package com.example.shuttlescore;

public class DoubleTeamModel {
    String teamId, teamName, city;
    String captain1Number, captain1Name;
    String captain2Number, captain2Name;

    public DoubleTeamModel() {}

    public DoubleTeamModel(String teamId, String teamName, String city,
                           String captain1Number, String captain1Name,
                           String captain2Number, String captain2Name) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.city = city;
        this.captain1Number = captain1Number;
        this.captain1Name = captain1Name;
        this.captain2Number = captain2Number;
        this.captain2Name = captain2Name;
    }

    // Add getters and setters if needed
}
