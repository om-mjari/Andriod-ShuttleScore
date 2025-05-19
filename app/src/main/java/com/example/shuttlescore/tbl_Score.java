package com.example.shuttlescore;

public class tbl_Score {

    private String resultId;
    private String tournamentId;
    private String matchId;
    private String team1Id;
    private String team2Id;
    private String team1Name;
    private String team2Name;
    private int team1Score;
    private int team2Score;
    private String winnerTeamId;
    private String loserTeamId;
    private int setCount;
    private int pointsAwarded;

    // Empty constructor for Firebase
    public tbl_Score() { }

    // Full constructor
    public tbl_Score(String resultId, String tournamentId, String matchId,
                     String team1Id, String team2Id, String team1Name, String team2Name,
                     int team1Score, int team2Score, String winnerTeamId, String loserTeamId,
                     int setCount, int pointsAwarded) {
        this.resultId = resultId;
        this.tournamentId = tournamentId;
        this.matchId = matchId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.winnerTeamId = winnerTeamId;
        this.loserTeamId = loserTeamId;
        this.setCount = setCount;
        this.pointsAwarded = pointsAwarded;
    }

    // Getters and setters
    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(String team1Id) {
        this.team1Id = team1Id;
    }

    public String getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(String team2Id) {
        this.team2Id = team2Id;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name(String team1Name) {
        this.team1Name = team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public void setTeam2Name(String team2Name) {
        this.team2Name = team2Name;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }

    public String getWinnerTeamId() {
        return winnerTeamId;
    }

    public void setWinnerTeamId(String winnerTeamId) {
        this.winnerTeamId = winnerTeamId;
    }

    public String getLoserTeamId() {
        return loserTeamId;
    }

    public void setLoserTeamId(String loserTeamId) {
        this.loserTeamId = loserTeamId;
    }

    public int getSetCount() {
        return setCount;
    }

    public void setSetCount(int setCount) {
        this.setCount = setCount;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }
}
