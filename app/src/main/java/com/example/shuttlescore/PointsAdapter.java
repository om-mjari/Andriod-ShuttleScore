package com.example.shuttlescore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PointsAdapter extends BaseAdapter {

    private Context context;
    private List<TeamPoints> list;

    public PointsAdapter(Context context, List<TeamPoints> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.points_row, parent, false);

        TextView txtName = view.findViewById(R.id.txtTeamName);
        TextView txtPlayed = view.findViewById(R.id.txtPlayed);
        TextView txtWins = view.findViewById(R.id.txtWins);
        TextView txtLosses = view.findViewById(R.id.txtLosses);

        TeamPoints tp = list.get(i);

        txtName.setText(tp.name);
        txtPlayed.setText(String.valueOf(tp.played));
        txtWins.setText(String.valueOf(tp.wins));
        txtLosses.setText(String.valueOf(tp.losses));

        return view;
    }
}
