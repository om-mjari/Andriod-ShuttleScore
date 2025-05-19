package com.example.shuttlescore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class SponsorAdapter extends BaseAdapter {

    private final Context context;
    public final ArrayList<tbl_Sponsor> sponsorsList;
    private int selectedPosition = -1;

    public SponsorAdapter(Context context, ArrayList<tbl_Sponsor> sponsorsList) {
        this.context = context;
        this.sponsorsList = (sponsorsList != null) ? sponsorsList : new ArrayList<>();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return sponsorsList.size();
    }

    @Override
    public Object getItem(int position) {
        return sponsorsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView sponsorName;
        TextView sponsorContact;
        TextView sponsorDescription;
        View container;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sponsor, parent, false);
            holder = new ViewHolder();

            holder.sponsorName = convertView.findViewById(R.id.sponsorNameText);
            holder.sponsorContact = convertView.findViewById(R.id.sponsorContactText);
            holder.sponsorDescription = convertView.findViewById(R.id.sponsorDescriptionText);
            holder.container = convertView.findViewById(R.id.itemContainer);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        tbl_Sponsor sponsor = sponsorsList.get(position);

        if (sponsor != null) {
            holder.sponsorName.setText(sponsor.getSponsorName());
            holder.sponsorContact.setText(sponsor.getSponsorContactNo());
            holder.sponsorDescription.setText(sponsor.getSponsorDescription());

            if (holder.container != null) {
                if (position == selectedPosition) {
                    holder.container.setBackgroundResource(R.drawable.item_selected_background);
                } else {
                    holder.container.setBackgroundResource(R.drawable.item_default_background);
                }
            } else {
                Log.e("SponsorAdapter", "container is null at position " + position);
            }

        }

        return convertView;
    }
}
