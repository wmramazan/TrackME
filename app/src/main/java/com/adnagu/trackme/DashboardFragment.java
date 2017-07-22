package com.adnagu.trackme;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adnagu.trackme.item.Place;
import com.adnagu.trackme.item.Trip;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by wmramazan on 27.05.2017.
 */

public class DashboardFragment extends Fragment {

    View view;
    PieChartView activitiesPieChart;
    PieChartData activitiesData;
    Button trackButton;
    TextView lastTripName, lastTripDetails, topPlaces;
    Trip lastTrip;
    ArrayList<Place> places;
    AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        activitiesPieChart = (PieChartView) view.findViewById(R.id.dashboard_piechart_activities);
        trackButton = (Button) view.findViewById(R.id.dashboard_track_button);
        lastTripName = (TextView) view.findViewById(R.id.dashboard_last_trip_name);
        lastTripDetails = (TextView) view.findViewById(R.id.dashboard_last_trip_details);
        topPlaces = (TextView) view.findViewById(R.id.dashboard_top_places);

        lastTrip = TrackME.db.getLastTrip();

        if(null == lastTrip) {
            lastTripName.setText(getString(R.string.go_on_a_trip));
            lastTripDetails.setVisibility(View.GONE);
        } else {
            lastTripName.setText(lastTrip.getName());
            lastTripDetails.setText(Html.fromHtml(
                    "<u>" + getString(R.string.maximum_speed) + "</u><br>" +
                            "<i>" + lastTrip.getMax_speed() + " m/s</i><br>" +
                            "<u>" + getString(R.string.average_speed) + "</u><br>" +
                            "<i>" + lastTrip.getAvg_speed() + " m/s</i><br>" +
                            "<u>" + getString(R.string.distance) + "</u><br>" +
                            "<i>" + lastTrip.getDistance() + " " + getString(R.string.meters) + "</i>"
            ));

            activitiesPieChart.setOnValueTouchListener(new PieChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int arcIndex, SliceValue value) {

                    // TODO: 12.06.2017 Go to MapFragment
                /*for(SliceValue val : activitiesData.getValues())
                    val.setTarget(10);
                activitiesPieChart.startDataAnimation();*/
                }

                @Override
                public void onValueDeselected() {

                }
            });

            activitiesPieChart.setValueSelectionEnabled(true);

            List<SliceValue> activitiesValues = new ArrayList<>();
            int[] numbersOfActivities = TrackME.db.getNumbersOfActivities();

            // TODO: 10.06.2017 Store old numbers for SliceValue

            activitiesValues.add(new SliceValue(1, ChartUtils.COLOR_BLUE).setLabel(getString(R.string.walking)).setTarget(numbersOfActivities[0]));
            activitiesValues.add(new SliceValue(1, ChartUtils.COLOR_RED).setLabel(getString(R.string.stationary)).setTarget(numbersOfActivities[1]));
            activitiesValues.add(new SliceValue(1, ChartUtils.COLOR_GREEN).setLabel(getString(R.string.vehicle)).setTarget(numbersOfActivities[2]));

            activitiesData = new PieChartData(activitiesValues);
            activitiesData.setHasLabels(true);
            activitiesPieChart.setPieChartData(activitiesData);
            activitiesPieChart.startDataAnimation();
        }

        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TrackME.pref.getBoolean("is_tracking", false)) {
                    getContext().startService(new Intent(getContext(), LocationService.class).setAction("STOP"));
                    ready();
                } else {
                    if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 0);

                    } else {
                        startTracking();
                        watching();
                    }
                }

            }
        });

        places = TrackME.db.getTopPlacesBasedOnTime();

        // TODO: 11.06.2017 Add RecyclerView with RecyclerViewHeader

        if(places.size() != 0) {
            String top_places = "";
            for(Place place : places)
                top_places += place.getName() + "\n";
            topPlaces.setText(top_places);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(TrackME.pref.getBoolean("is_tracking", false))
            watching();
        else
            ready();
    }

    public void watching() {
        trackButton.setText(getString(R.string.tracking_watching));
        trackButton.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.pressed_button_background));
    }

    public void ready() {
        trackButton.setText(getString(R.string.tracking_ready));
        trackButton.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_background));
    }

    public void startTracking() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_trip, null);
        final EditText tripName = (EditText) dialogView.findViewById(R.id.dialog_trip_name);

        // TODO: 2.06.2017 First Click Dialog (Information GPS, Place Detection, ..)

        if(internetConnection()) {

            dialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.dialog_trip_title))
                    .setView(dialogView)
                    .setIcon(R.drawable.ic_map_black_24dp)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            startLocationService(getString(R.string.default_trip_name, TrackME.db.getNumberOfTrips() + 1));

                        }
                    })
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String trip_name = tripName.getText().toString().trim();

                    if(tripName.length() >= 2 && tripName.length() <= 100) {
                        startLocationService(trip_name);
                        dialog.dismiss();
                    } else Toast.makeText(v.getContext(), R.string.invalid_trip_name, Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            dialog = new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.internet_connection))
                    .setMessage(getString(R.string.internet_connection_dialog_text))
                    .setIcon(R.drawable.warning)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            dialog.show();
        }
    }

    public void startLocationService(String tripName) {
        Intent intent = new Intent(getContext(), LocationService.class);
        intent.putExtra("trip_name", tripName);
        getContext().startService(intent);
    }

    public boolean internetConnection() {
        ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo[] inf = connectivity.getAllNetworkInfo();
            if (inf != null)
                for (int i = 0; i < inf.length; i++)
                    if (inf[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;

        }
        return false;
    }

}
