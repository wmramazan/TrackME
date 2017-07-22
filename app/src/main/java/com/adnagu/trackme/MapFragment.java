package com.adnagu.trackme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adnagu.trackme.item.Location;
import com.adnagu.trackme.item.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by wmramazan on 27.05.2017.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback {

    View view;
    GoogleMap map;
    TextView tripName;
    Button activitiesButton, placesButton;
    ArrayList<Place> places;
    ArrayList<Location> locations;
    String trip_name;
    boolean isActivities = true;
    int trip_id = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // TODO: 13.06.2017 Use savedInstance for camera position 

        view = inflater.inflate(R.layout.fragment_map, container, false);

        tripName = (TextView) view.findViewById(R.id.map_trip_name);
        activitiesButton = (Button) view.findViewById(R.id.map_activities_button);
        placesButton = (Button) view.findViewById(R.id.map_places_button);

        if(trip_id == 0)
            tripName.setText(getString(R.string.last_trip));
        else
            tripName.setText(trip_name);


        View.OnClickListener toggle = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtons();
            }
        };

        activitiesButton.setOnClickListener(toggle);
        placesButton.setOnClickListener(toggle);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        // TODO: 12.06.2017 Add current location as a marker with TrackME icon
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.trackme_blue_icon))
        // TODO: 12.06.2017 "Show on the map" button for TripListAdapter and PlaceListAdapter

        showActivities();

    }

    public void showActivities() {
        map.clear();

        locations = TrackME.db.getLocations(trip_id);
        Log.d("ADN", "locations.size : " + locations.size());

        if(locations.size() != 0) {

            for(Location location : locations) {

                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(location.getSpeed() < 1 ? BitmapDescriptorFactory.HUE_BLUE : (location.getSpeed() > 10 ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED)))
                        .snippet(location.toString())
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(DateUtils.getRelativeTimeSpanString(location.getDate(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString())
                );

            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude()), 15));

        }
    }

    public void showPlaces() {
        map.clear();

        places = TrackME.db.getPlaces(trip_id);
        Log.d("ADN", "places.size : " + places.size());

        if(places.size() == 0)
            Toast.makeText(getContext(), R.string.no_places, Toast.LENGTH_LONG).show();
        else {

            for(Place place : places) {

                Log.d("ADN", "place: " + place.getLatitude() + " " + place.getLongitude());

                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(getColorForPlace(place.getVisit())))
                        .snippet(place.toString())
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .title(place.getName())
                );

            }

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude()), 15));

        }

    }

    public float getColorForPlace(int visit) {

        if(visit < 5)
            return BitmapDescriptorFactory.HUE_AZURE;
        else if(visit < 10)
            return BitmapDescriptorFactory.HUE_BLUE;
        else if(visit < 20)
            return BitmapDescriptorFactory.HUE_CYAN;
        else if(visit < 50)
            return BitmapDescriptorFactory.HUE_GREEN;
        else if(visit < 75)
            return BitmapDescriptorFactory.HUE_MAGENTA;
        else if(visit < 100)
            return BitmapDescriptorFactory.HUE_ORANGE;
        else
            return BitmapDescriptorFactory.HUE_VIOLET;

    }

    public void toggleButtons() {
        if(isActivities) {
            activitiesButton.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.blue_button_background));
            placesButton.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_background));
            showPlaces();
        } else {
            activitiesButton.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_background));
            placesButton.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.blue_button_background));
            showActivities();
        }
        isActivities = !isActivities;
    }

    public void setTrip_id(int trip_id) {
        this.trip_id = trip_id;
    }

    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }
}
