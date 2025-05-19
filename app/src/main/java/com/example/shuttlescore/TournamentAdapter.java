package com.example.shuttlescore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;

public class TournamentAdapter extends BaseAdapter {
    private Context context;
    private List<TournamentModel> tournamentList;

    public TournamentAdapter(Context context, List<TournamentModel> tournamentList) {
        this.context = context;
        this.tournamentList = tournamentList;
    }

    @Override
    public int getCount() {
        return tournamentList.size();
    }

    public void setTournamentList(List<TournamentModel> tournamentList) {
        this.tournamentList = tournamentList;
        notifyDataSetChanged();
    }

    public List<TournamentModel> getTournamentList() {
        return tournamentList;
    }

    @Override
    public Object getItem(int position) {
        return tournamentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tournament_item, parent, false);
        }

        TournamentModel tournament = tournamentList.get(position);

        ImageView photo = convertView.findViewById(R.id.tournamentPhoto);
        TextView name = convertView.findViewById(R.id.tournamentName);
        TextView ground = convertView.findViewById(R.id.groundName);
        TextView location = convertView.findViewById(R.id.tournamentLocation);
//        TextView dateTime = convertView.findViewById(R.id.DateTime);

        name.setText(tournament.getTournamentName());
        ground.setText(tournament.getGroundName());

        Glide.with(context)
                .load(tournament.getTournamentPhoto())
                .placeholder(R.drawable.tournamentimage)
                .error(R.drawable.tournamentimage)
                .into(photo);

        return convertView;
    }
}
