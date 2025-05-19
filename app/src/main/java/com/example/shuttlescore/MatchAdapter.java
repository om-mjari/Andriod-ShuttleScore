package com.example.shuttlescore;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MatchAdapter extends BaseAdapter {
    Context context;
    ArrayList<tbl_matches> matchList;
    LayoutInflater inflater;

    public MatchAdapter(Context context, ArrayList<tbl_matches> matchList) {
        this.context = context;
        this.matchList = matchList;
        this.inflater = LayoutInflater.from(context);
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
        TextView txtMatchStatus, txtGameMode, txtMatchFormat, txtGamePoints;
        TextView tvPlayer1, tvPlayer2, tvPlayer3, tvPlayer4;
        MaterialButton btnLiveScore;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.match_item, parent, false);
            holder = new ViewHolder();
            holder.txtMatchStatus = convertView.findViewById(R.id.txtMatchStatus);
            holder.txtGameMode = convertView.findViewById(R.id.txtGameMode);
            holder.txtMatchFormat = convertView.findViewById(R.id.txtMatchFormat);
            holder.txtGamePoints = convertView.findViewById(R.id.txtGamePoints);
            holder.tvPlayer1 = convertView.findViewById(R.id.tvPlayer1);
            holder.tvPlayer2 = convertView.findViewById(R.id.tvPlayer2);
            holder.tvPlayer3 = convertView.findViewById(R.id.tvPlayer3);
            holder.tvPlayer4 = convertView.findViewById(R.id.tvPlayer4);
            holder.btnLiveScore = convertView.findViewById(R.id.btnLiveScore);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        tbl_matches match = matchList.get(position);

        holder.txtMatchStatus.setText(match.matchStatus);
        switch (match.matchStatus.toLowerCase()) {
            case "played":
                holder.txtMatchStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.txtMatchStatus.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "live":
                holder.txtMatchStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.txtMatchStatus.setTextColor(Color.parseColor("#F44336"));
                break;
            case "scheduled":
                holder.txtMatchStatus.setBackgroundColor(Color.parseColor("#F5F5F5"));
                holder.txtMatchStatus.setTextColor(Color.parseColor("#9E9E9E"));
                break;
            default:
                holder.txtMatchStatus.setBackgroundColor(Color.parseColor("#E3F2FD"));
                holder.txtMatchStatus.setTextColor(Color.BLACK);
        }

        holder.txtGameMode.setText("Game Mode: " + match.gameMode);
        holder.txtMatchFormat.setText("Format: " + match.matchFormat);
        holder.txtGamePoints.setText("Points to Win: " + match.gamePoints);

        holder.tvPlayer1.setVisibility(View.GONE);
        holder.tvPlayer2.setVisibility(View.GONE);
        holder.tvPlayer3.setVisibility(View.GONE);
        holder.tvPlayer4.setVisibility(View.GONE);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("tbl_Users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, String> nameMap = new HashMap<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String phone = userSnap.child("phone").getValue(String.class);
                    String name = userSnap.child("name").getValue(String.class);
                    if (phone != null && name != null) {
                        nameMap.put(phone, name);
                    }
                }

                String name1 = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    name1 = nameMap.getOrDefault(match.pl1id, match.pl1id);
                }
                String name2 = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    name2 = nameMap.getOrDefault(match.pl2id, match.pl2id);
                }
                String name3 = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    name3 = nameMap.getOrDefault(match.pl3id, match.pl3id);
                }
                String name4 = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    name4 = nameMap.getOrDefault(match.pl4id, match.pl4id);
                }

                if (match.pl1id != null && !match.pl1id.isEmpty()) {
                    holder.tvPlayer1.setText(name1);
                    holder.tvPlayer1.setVisibility(View.VISIBLE);
                }
                if (match.pl2id != null && !match.pl2id.isEmpty()) {
                    holder.tvPlayer2.setText(name2);
                    holder.tvPlayer2.setVisibility(View.VISIBLE);
                }
                if (match.pl3id != null && !match.pl3id.isEmpty()) {
                    holder.tvPlayer3.setText(name3);
                    holder.tvPlayer3.setVisibility(View.VISIBLE);
                }
                if (match.pl4id != null && !match.pl4id.isEmpty()) {
                    holder.tvPlayer4.setText(name4);
                    holder.tvPlayer4.setVisibility(View.VISIBLE);
                }

                // Set button click with player names
                holder.btnLiveScore.setOnClickListener(v -> {
                    Intent intent = new Intent(context, MatchDetailsActivity.class);
                    intent.putExtra("matchId", match.matchId);
                    intent.putExtra("player1", match.pl1id);
                    intent.putExtra("player2", match.pl2id);
                    intent.putExtra("player3", match.pl3id);
                    intent.putExtra("player4", match.pl4id);
                    intent.putExtra("Match_Status", match.matchStatus);
                    context.startActivity(intent);
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Error loading player data", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
