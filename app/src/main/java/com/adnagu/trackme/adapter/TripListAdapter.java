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
import com.adnagu.trackme.item.Trip;

import java.util.ArrayList;

/**
 * Created by wmramazan on 28.05.2017.
 */

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripListViewHolder> {

    private Context context;
    private ArrayList<Trip> trips;
    private Trip trip;

    public TripListAdapter(Context context, ArrayList<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @Override
    public TripListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TripListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false));
    }

    @Override
    public void onBindViewHolder(TripListViewHolder holder, int position) {
        trip = trips.get(position);

        holder.name.setText(trip.getName());
        holder.date.setText(DateUtils.getRelativeTimeSpanString(trip.getDate(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        holder.details.setText(Html.fromHtml(
                "<b>" + context.getString(R.string.distance) + ": </b><i>" + trip.getDistance() + "</i><br>" +
                "<b>" + context.getString(R.string.maximum_speed) + ": </b><i>" + trip.getDistance() + "</i><br>" +
                "<b>" + context.getString(R.string.average_speed) + ": </b><i>" + trip.getAvg_speed() + "</i>"
        ));
    }

    @Override
    public int getItemCount() {
        return (null != trips ? trips.size() : 0);
    }

    public static class TripListViewHolder extends RecyclerView.ViewHolder {

        protected TextView name, date, details;

        public TripListViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.trip_name);
            this.date = (TextView) itemView.findViewById(R.id.trip_date);
            this.details = (TextView) itemView.findViewById(R.id.trip_details);
        }

    }
}
