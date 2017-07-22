package com.adnagu.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    BottomNavigationView navigation;
    DashboardFragment dashboardFragment;
    MapFragment mapFragment;
    NotificationsFragment notificationsFragment;
    FragmentManager fragmentManager;
    BroadcastReceiver receiver;
    Intent intent;
    IntentFilter intentFilter;
    double latitude, longitude;
    boolean replaceWithMapFragment = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    if(null == dashboardFragment) dashboardFragment = new DashboardFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_container, dashboardFragment).commit();
                    break;
                case R.id.navigation_map:
                    if(null == mapFragment) mapFragment = new MapFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_container, mapFragment).commit();
                    break;
                case R.id.navigation_notifications:
                    if(null == notificationsFragment) notificationsFragment = new NotificationsFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_container, notificationsFragment).commit();
                    break;
            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 6.06.2017 Remove unused resources and imports

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.trackme_logo);
        actionBar.setElevation(3);

        dashboardFragment = new DashboardFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, dashboardFragment).commit();
        navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(null != data) {
            //Log.d("ADN", "selected trip_id: " + data.getIntExtra("trip_id", 0));

            mapFragment = new MapFragment();
            mapFragment.setTrip_id(data.getIntExtra("trip_id", 0));
            mapFragment.setTrip_name(data.getStringExtra("trip_name"));
            replaceWithMapFragment = true;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(null == receiver) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if(intent.getAction().equals("stop")) {
                        dashboardFragment.ready();
                    } else {
                        latitude = intent.getExtras().getDouble("latitude");
                        longitude = intent.getExtras().getDouble("longitude");
                        Log.d("ADN", "Broadcast Recevier: " + latitude + " - " + longitude);
                    }

                }
            };
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("current_location");
        intentFilter.addAction("stop");
        registerReceiver(receiver, intentFilter);

        if(replaceWithMapFragment) {
            navigation.setSelectedItemId(R.id.navigation_map);
            replaceWithMapFragment = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != receiver) unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_trips:
                intent = new Intent(this, TripsActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.action_preferences:
                intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            dashboardFragment.startTracking();
        }

    }

    public void goToPlacesActivity(View view) {
        intent = new Intent(this, PlacesActivity.class);
        startActivity(intent);
    }
}
