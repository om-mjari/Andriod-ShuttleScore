package com.example.shuttlescore;

public class tbl_tournament_matches {

    public String matchId;
    public String tournamentId;
    public String scheduleTime;
    public String gameMode;
    public String gamePoints;
    public String team1id,team2id;
    public String matchFormat;
    public String matchtype;

    public String matchStatus;

    public tbl_tournament_matches() {}

    public tbl_tournament_matches(String matchId, String tournamentId, String scheduleTime,
                       String gameMode, String gamePoints,String team1id,String team2id,String matchFormat,String matchtype,String matchStatus) {
        this.matchId = matchId;
        this.tournamentId = tournamentId;
        this.scheduleTime = scheduleTime;
        this.gameMode = gameMode;
        this.gamePoints = gamePoints;
        this.team1id = team1id;
        this.team2id = team2id;
        this.matchFormat = matchFormat;
        this.matchtype = matchtype;
        this.matchStatus = matchStatus;
    }
}
