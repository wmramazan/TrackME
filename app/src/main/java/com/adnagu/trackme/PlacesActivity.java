package com.adnagu.trackme;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adnagu.trackme.adapter.PlaceListAdapter;
import com.adnagu.trackme.item.Place;

import java.util.ArrayList;
import java.util.Calendar;

public class PlacesActivity extends AppCompatActivity {

    RecyclerView placeList;
    PlaceListAdapter placeListAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<Place> places;
    LinearLayout noContent;
    DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    Calendar calendar;
    long start_date, end_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        placeList = (RecyclerView) findViewById(R.id.list_item);
        noContent = (LinearLayout) findViewById(R.id.list_item_noContent);

        // TODO: 11.06.2017 UI -> Delete places

        places = TrackME.db.getPlaces();

        if(places.size() == 0) {
            ImageView icon = (ImageView) findViewById(R.id.list_item_noContent_icon);
            icon.setImageResource(R.drawable.place);
            TextView text = (TextView) findViewById(R.id.list_item_noContent_text) ;
            text.setText(R.string.no_places);
            noContent.setVisibility(View.VISIBLE);
        } else {
            layoutManager = new LinearLayoutManager(this);
            placeList.setLayoutManager(layoutManager);
            placeListAdapter = new PlaceListAdapter(this, places);
            placeList.setAdapter(placeListAdapter);
        }

        calendar = Calendar.getInstance();

        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                start_date = calendar.getTimeInMillis();
                endDatePickerDialog.getDatePicker().setMinDate(start_date);
                endDatePickerDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        startDatePickerDialog.setMessage(getString(R.string.start_date));
        startDatePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                end_date = calendar.getTimeInMillis();
                places = TrackME.db.getPlaces(start_date, end_date);
                //Log.d("ADN", "Places: " + places.size());
                placeListAdapter.setPlaces(places);
                calendar = Calendar.getInstance();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog.setMessage(getString(R.string.end_date));
        endDatePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.places, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter_places:

                startDatePickerDialog.show();

                break;
            default:
                break;
        }

        return true;
    }
}
