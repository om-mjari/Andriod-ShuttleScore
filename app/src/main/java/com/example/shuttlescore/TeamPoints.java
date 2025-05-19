package com.example.shuttlescore;

public class TeamPoints {
    public String name;
    public int played;
    public int wins;
    public int losses;

    public TeamPoints(String name, int played, int wins, int losses) {
        this.name = name;
        this.played = played;
        this.wins = wins;
        this.losses = losses;
    }
}
