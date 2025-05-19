package com.example.shuttlescore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DoubleTeamAdapter extends ArrayAdapter<tbl_double_team> {

    private int selectedPosition = -1;

    public DoubleTeamAdapter(Context context, List<tbl_double_team> teamList) {
        super(context, 0, teamList);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        tbl_double_team team = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.double_team_item, parent, false);
        }

        TextView tvTeamName = convertView.findViewById(R.id.tvTeamName);
        TextView tvCity = convertView.findViewById(R.id.tvCity);
        TextView tvCaptain1 = convertView.findViewById(R.id.tvCaptain1);
        TextView tvCaptain2 = convertView.findViewById(R.id.tvCaptain2);

        tvTeamName.setText(team.getTeamName());
        tvCity.setText("City: " + team.getCity());
        tvCaptain1.setText("Player 1: " + team.getCaptain1Name());
        tvCaptain2.setText("Player 2: " + team.getCaptain2Name());


        if (position == selectedPosition) {
            convertView.setBackgroundColor(0xFFE0E0E0);
        } else {
            convertView.setBackgroundColor(0x00000000);
        }

        convertView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        convertView.setLongClickable(true);


        return convertView;
    }
}
