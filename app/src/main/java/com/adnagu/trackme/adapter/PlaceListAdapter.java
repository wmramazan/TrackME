package com.adnagu.trackme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adnagu.trackme.R;
import com.adnagu.trackme.item.Place;

import java.util.ArrayList;

/**
 * Created by wmramazan on 28.05.2017.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceListViewHolder> {

    private Context context;
    private ArrayList<Place> places;
    private Place place;

    public PlaceListAdapter(Context context, ArrayList<Place> places) {
        this.context = context;
        this.places = places;
    }

    @Override
    public PlaceListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaceListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false));
    }

    @Override
    public void onBindViewHolder(PlaceListViewHolder holder, int position) {
        place = places.get(position);

        holder.name.setText(place.getName());
        holder.date.setText(DateUtils.getRelativeTimeSpanString(place.getDate(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        holder.details.setText(Html.fromHtml(
                "<b>" + context.getString(R.string.address) + ": </b><i>" + place.getAddress() + "</i><br>" +
                "<b>" + context.getString(R.string.freq_visit) + ": </b><i>" + place.getVisit() + "</i><br>" +
                "<b>" + context.getString(R.string.freq_time) + ": </b><i>" + place.getTime() + "</i><br>" +
                "<b>" + context.getString(R.string.last_visit_date) + ": </b><i>" + DateUtils.getRelativeTimeSpanString(place.getLast_visit_date(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) + "</i><br>"
        ));
    }

    @Override
    public int getItemCount() {
        return (null == places ? 0 : places.size());
    }

    public static class PlaceListViewHolder extends RecyclerView.ViewHolder {

        protected TextView name, date, details;

        public PlaceListViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.place_name);
            this.date = (TextView) itemView.findViewById(R.id.place_date);
            this.details = (TextView) itemView.findViewById(R.id.place_details);
        }

    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
        notifyDataSetChanged();
    }
}
