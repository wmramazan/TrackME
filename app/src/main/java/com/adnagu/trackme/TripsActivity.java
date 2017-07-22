package com.adnagu.trackme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adnagu.trackme.adapter.TripListAdapter;
import com.adnagu.trackme.item.Trip;
import com.adnagu.trackme.util.RecyclerTouchListener;

import java.util.ArrayList;

public class TripsActivity extends AppCompatActivity {

    RecyclerView tripList;
    TripListAdapter tripListAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<Trip> trips;
    LinearLayout noContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        tripList = (RecyclerView) findViewById(R.id.list_item);
        noContent = (LinearLayout) findViewById(R.id.list_item_noContent);

        // TODO: 11.06.2017 UI -> Delete or rename trips

        trips = TrackME.db.getTrips();

        if(trips.size() == 0) {
            ImageView icon = (ImageView) findViewById(R.id.list_item_noContent_icon);
            icon.setImageResource(R.drawable.trip);
            TextView text = (TextView) findViewById(R.id.list_item_noContent_text) ;
            text.setText(R.string.no_trips);
            noContent.setVisibility(View.VISIBLE);
        } else {
            layoutManager = new LinearLayoutManager(this);
            tripList.setLayoutManager(layoutManager);
            tripListAdapter = new TripListAdapter(this, trips);
            tripList.setAdapter(tripListAdapter);

            tripList.addOnItemTouchListener(new RecyclerTouchListener(this, tripList, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    Intent intent = new Intent();
                    intent.putExtra("trip_id", trips.get(position).getId());
                    intent.putExtra("trip_name", trips.get(position).getName());
                    setResult(0, intent);
                    finish();

                }

                @Override
                public void onLongClick(View view, int position) {

                    //Delete or Rename

                }
            }));
        }
    }
}
