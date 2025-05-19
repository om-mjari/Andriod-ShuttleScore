package com.example.shuttlescore;

public class tbl_matches {
    public String matchId;
    public String tournamentId;
    public String scheduleTime;
    public String gameMode;
    public String gamePoints;
    public String pl1id,pl2id,pl3id,pl4id;
    public String matchFormat;
    public String matchStatus;

    public tbl_matches() {

    }

    public tbl_matches(String matchId, String tournamentId, String scheduleTime,
                 String gameMode, String gamePoints,String pl1id,String pl2id,String pl3id,String pl4id,String matchFormat,String matchStatus) {
        this.matchId = matchId;
        this.tournamentId = tournamentId;
        this.scheduleTime = scheduleTime;
        this.gameMode = gameMode;
        this.gamePoints = gamePoints;
        this.pl1id = pl1id;
        this.pl2id = pl2id;
        this.pl3id = pl3id;
        this.pl4id = pl4id;
        this.matchFormat = matchFormat;
        this.matchStatus = matchStatus;
    }
}
