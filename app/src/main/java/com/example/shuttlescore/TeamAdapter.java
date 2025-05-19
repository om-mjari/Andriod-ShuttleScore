package com.example.shuttlescore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TeamAdapter extends BaseAdapter {

    private Context context;
    private List<tbl_team> teamList;
    private int selectedPosition = -1;
    private boolean isTournamentActivity;

    public TeamAdapter(Context context, List<tbl_team> teamList, boolean isTournamentActivity) {
        this.context = context;
        this.teamList = teamList;
        this.isTournamentActivity = isTournamentActivity;
    }

    @Override
    public int getCount() {
        return teamList.size();
    }

    @Override
    public Object getItem(int position) {
        return teamList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.team_item, parent, false);
        }

        TextView teamCity = convertView.findViewById(R.id.teamname);
        TextView captainName = convertView.findViewById(R.id.captainName);

        tbl_team team = teamList.get(position);
        teamCity.setText(team.getTeamName());
        captainName.setText(team.getCaptainName());

        if (isTournamentActivity) {
            if (position == selectedPosition) {
                convertView.setBackgroundColor(0xFFE0E0E0);
            } else {
                convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }

            convertView.setOnClickListener(v -> {
                selectedPosition = position;
                notifyDataSetChanged();
            });
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
