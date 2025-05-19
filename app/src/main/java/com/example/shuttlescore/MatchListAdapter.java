package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;

public class MatchListAdapter extends BaseAdapter {

    private Context context;
    private List<MatchModel> matchList;
    private HashMap<String, tbl_live_score> liveScoreMap;

    public MatchListAdapter(Context context, List<MatchModel> matchList) {
        this.context = context;
        this.matchList = matchList;
        this.liveScoreMap = new HashMap<>();
    }

    public void updateLiveScores(HashMap<String, tbl_live_score> liveScores) {
        this.liveScoreMap = liveScores;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return matchList.size();
    }

    @Override
    public Object getItem(int position) {
        return matchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.match_list_item, parent, false);

            holder = new ViewHolder();
            holder.tvMatchStatus = convertView.findViewById(R.id.tvMatchStatus);
            holder.tvTeamNames = convertView.findViewById(R.id.tvTeamNames);
            holder.tvMatchScore = convertView.findViewById(R.id.tvMatchScore);
            holder.btnLiveScore = convertView.findViewById(R.id.btnLiveScore);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MatchModel match = matchList.get(position);
        tbl_live_score liveScore = liveScoreMap.get(match.getMatchId());

        // Set match status
        String matchStatus = match.getMatchStatus();
        holder.tvMatchStatus.setText(matchStatus);
        setStatusColor(holder.tvMatchStatus, matchStatus);

        // Set team names
        String team1 = match.getTeam1Name() != null ? match.getTeam1Name() : "Team 1";
        String team2 = match.getTeam2Name() != null ? match.getTeam2Name() : "Team 2";
        holder.tvTeamNames.setText(String.format("%s vs %s", team1, team2));

        // Set dynamic score
        if (liveScore != null) {
            String score = String.format("%d - %d", liveScore.getTeam1Score(), liveScore.getTeam2Score());
            holder.tvMatchScore.setText(score);
            holder.tvMatchScore.setVisibility(View.VISIBLE);
        } else {
            holder.tvMatchScore.setVisibility(View.GONE);
        }

        // Handle button click
        holder.btnLiveScore.setOnClickListener(v -> navigateToMatchDetails(match, team1, team2, matchStatus, liveScore));

        return convertView;
    }

    private void setStatusColor(TextView statusView, String status) {
        if ("Live".equalsIgnoreCase(status)) {
            statusView.setTextColor(Color.parseColor("#FF5722")); // Orange
        } else if ("Played".equalsIgnoreCase(status)) {
            statusView.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            statusView.setTextColor(Color.parseColor("#9E9E9E")); // Gray
        }
    }

    private void navigateToMatchDetails(MatchModel match, String team1, String team2,
                                        String matchStatus, tbl_live_score liveScore) {
        Intent intent = new Intent(context, MatchDetailsActivity.class);
        intent.putExtra("matchId", match.getMatchId());
        intent.putExtra("team1", team1);
        intent.putExtra("team2", team2);
        intent.putExtra("Match_Status", matchStatus);

        intent.putExtra("player1", match.getPl1id());
        intent.putExtra("player2", match.getPl2id());
        intent.putExtra("player3", match.getPl3id());
        intent.putExtra("player4", match.getPl4id());

        if (liveScore != null) {
            intent.putExtra("team1Score", liveScore.getTeam1Score());
            intent.putExtra("team2Score", liveScore.getTeam2Score());
            intent.putExtra("team1Set1", liveScore.getTeam1Set1());
            intent.putExtra("team2Set1", liveScore.getTeam2Set1());
            intent.putExtra("team1Set2", liveScore.getTeam1Set2());
            intent.putExtra("team2Set2", liveScore.getTeam2Set2());
            intent.putExtra("team1Set3", liveScore.getTeam1Set3());
            intent.putExtra("team2Set3", liveScore.getTeam2Set3());
        }

        context.startActivity(intent);
    }


    private static class ViewHolder {
        TextView tvMatchStatus;
        TextView tvTeamNames;
        TextView tvMatchScore;
        MaterialButton btnLiveScore;
    }
}