package com.example.shuttlescore;

public class tbl_live_score {
    public String liveScoreId;
    public String matchId;
    public int Team1_Score;
    public int Team2_Score;
    public int Team1_Set1;
    public int Team1_Set2;
    public int Team1_Set3;
    public int Team2_Set1;
    public int Team2_Set2;
    public int Team2_Set3;

    public String matchStatus;
    public String winner;


    public tbl_live_score() {}

    public tbl_live_score(String liveScoreId, String matchId,
                          int Team1_Score, int Team2_Score,
                          int Team1_Set1, int Team2_Set1,
                          int Team1_Set2, int Team2_Set2,
                          int Team1_Set3, int Team2_Set3,
                          String match_Status, String winner) {
        this.liveScoreId = liveScoreId;
        this.matchId = matchId;
        this.Team1_Score = Team1_Score;
        this.Team2_Score = Team2_Score;
        this.Team1_Set1 = Team1_Set1;
        this.Team1_Set2 = Team1_Set2;
        this.Team1_Set3 = Team1_Set3;
        this.Team2_Set1 = Team2_Set1;
        this.Team2_Set2 = Team2_Set2;
        this.Team2_Set3 = Team2_Set3;
        this.matchStatus = match_Status;
        this.winner = winner;
    }


    public String getLiveScoreId() {
        return liveScoreId;
    }

    public void setLive_Score_Id(String liveScoreId) {
        this.liveScoreId = liveScoreId;
    }
    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getTeam1Score() {
        return Team1_Score;
    }

    public void setTeam1Score(int Team1_Score) {
        this.Team1_Score = Team1_Score;
    }

    public int getTeam2Score() {
        return Team2_Score;
    }

    public void setTeam2Score(int Team2_Score) {
        this.Team2_Score = Team2_Score;
    }

    public int getTeam1Set1() {
        return Team1_Set1;
    }

    public void setTeam1Set1(int Team1_Set1) {
        this.Team1_Set1 = Team1_Set1;
    }

    public int getTeam2Set1() {
        return Team2_Set1;
    }

    public void setTeam2Set1(int Team2_Set1) {
        this.Team2_Set1 = Team2_Set1;
    }

    public int getTeam1Set2() {
        return Team1_Set2;
    }

    public void setTeam1Set2(int Team1_Set2) {
        this.Team1_Set2 = Team1_Set2;
    }

    public int getTeam2Set2() {
        return Team2_Set2;
    }

    public void setTeam2Set2(int Team2_Set2) {
        this.Team2_Set2 = Team2_Set2;
    }

    public int getTeam1Set3() {
        return Team1_Set3;
    }

    public void setTeam1Set3(int Team1_Set3) {
        this.Team1_Set3 = Team1_Set3;
    }

    public int getTeam2Set3() {
        return Team2_Set3;
    }

    public void setTeam2Set3(int Team2_Set3) {
        this.Team2_Set3 = Team2_Set3;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

}
