package com.example.shuttlescore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TournamentMatchAdapter extends BaseAdapter {
    Context context;
    List<MatchModel> matchList;
    LayoutInflater inflater;

    public TournamentMatchAdapter(Context context, List<MatchModel> matchList) {
        this.context = context;
        this.matchList = matchList;
        inflater = LayoutInflater.from(context);
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

    static class ViewHolder {
        TextView team1, vs, team2,txtMatchType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tournament_matches, parent, false);
            h = new ViewHolder();
            h.team1 = convertView.findViewById(R.id.txtTeam1);
            h.vs = convertView.findViewById(R.id.vs);
            h.team2 = convertView.findViewById(R.id.txtTeam2);
            h.txtMatchType = convertView.findViewById(R.id.txtMatchType);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        MatchModel m = matchList.get(position);

        h.team1.setText("" + (m.getTeam1Name() != null ? m.getTeam1Name() : ""));
        h.vs.setText("vs");
        h.team2.setText("" + (m.getTeam2Name() != null ? m.getTeam2Name() : ""));
        h.txtMatchType.setText(m.getMatchtype());

        return convertView;
    }
}
