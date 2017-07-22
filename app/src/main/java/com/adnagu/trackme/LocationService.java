package com.adnagu.trackme;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by wmramazan on 30.05.2017.
 */

public class LocationService extends Service implements LocationListener {

    private LocationManager locationManager;
    private Geocoder geocoder;
    private Intent intent;

    private Location location;
    private int location_id, trip_id, place_freq, number_of_locations = 0;
    private float distance = 0;
    private String trip_name, place_name;
    private boolean show_details;
    private Notification notification;
    private NotificationCompat.Builder notificationBuilder;
    private PendingIntent pendingIntent;
    private String tracking_status;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d("ADN", "onCreate");

        place_freq = Integer.parseInt(TrackME.pref.getString("place_freq", "5"));
        show_details = TrackME.pref.getBoolean("show_details", true);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // TODO: 6.06.2017 pref_location_provider: new option -> best_provider
        /*
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationManager.getBestProvider(criteria, true);
         */

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(
                TrackME.pref.getString("location_provider", "0").equals("0") ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER,
                Integer.parseInt(TrackME.pref.getString("location_freq", "3")) * 1000,
                0,
                this);

        geocoder = new Geocoder(LocationService.this, Locale.getDefault());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("ADN", "onStartCommand: " + intent);

        if(null == intent.getAction()) {

            TrackME.editor.putBoolean("is_tracking", true).apply();

            trip_name = intent.getStringExtra("trip_name");
            trip_id = TrackME.db.insertTrip(trip_name);
            Log.d("ADN", "trip_id: " + trip_id);
            tracking();

        } else {
            
            TrackME.editor.putBoolean("is_tracking", false).apply();
            sendBroadcast(new Intent("stop"));

            if(number_of_locations == 0) {
                TrackME.db.deleteTrip(trip_id);
                Toast.makeText(this, R.string.trip_will_be_deleted, Toast.LENGTH_LONG).show();
            } else
                TrackME.db.updateTrip(trip_id, distance);
            stopSelf();

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(null != this.location)
            distance += location.distanceTo(this.location);
        this.location = location;

        location_id = TrackME.db.insertLocation(
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                System.currentTimeMillis(),
                location.getSpeed(),
                trip_id
        );

        if(number_of_locations++ % place_freq == 0)
            new PlaceAsyncTask().execute(
                    location.getLatitude(),
                    location.getLongitude(),
                    (double) location_id
            );

        if(show_details)
            updateTracking();

        String log = "onLocationChanged: " + location;
        Log.d("ADN", log);
        TrackME.db.insertNotification((byte) 1, log);

        intent = new Intent("current_location");
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        sendBroadcast(intent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        String log = "onStatusChanged: " + provider;
        Log.d("ADN", log);
        TrackME.db.insertNotification((byte) 1, log);
        
    }

    @Override
    public void onProviderEnabled(String provider) {

        String log = "onProviderEnabled: " + provider;
        Log.d("ADN", log);
        TrackME.db.insertNotification((byte) 1, log);
        tracking();
        
    }

    @Override
    public void onProviderDisabled(String provider) {

        updateTrackingOnProviderDisabled();

        Toast.makeText(this, R.string.enable_location_provider, Toast.LENGTH_SHORT).show();

        intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != locationManager) locationManager.removeUpdates(this);
    }

    public void buildNotification() {
        notification = notificationBuilder.build();
        startForeground(1, notification);
    }
    
    public void setIntentForMainActivity() {
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    
    public void tracking() {
        tracking_status = show_details ? getString(R.string.tracking_searching) : getString(R.string.tracking_watching);

        // TODO: 2.06.2017 Add Pause Action

        setIntentForMainActivity();
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.trackme_notification_icon)
                .setAutoCancel(false)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setLights(ContextCompat.getColor(this, R.color.colorPrimaryDark), 3000, 3000)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(tracking_status))
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setContentTitle(trip_name)
                .setContentText(tracking_status)
                .setContentIntent(pendingIntent)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_stop_black_24dp,
                        getString(R.string.stop_tracking),
                        PendingIntent.getService(this, 0, new Intent(this, LocationService.class).setAction("STOP"), PendingIntent.FLAG_CANCEL_CURRENT)
                ));

        buildNotification();
        
    }

    public void updateTracking() {

        tracking_status = getString(R.string.tracking_status, null == place_name ? getString(R.string.unknown_place) : place_name, distance, String.valueOf(location.getLatitude()) + " - " + String.valueOf(location.getLongitude()), location.getSpeed());
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(tracking_status))
                .setSmallIcon(R.drawable.trackme_notification_icon)
                .setContentText(tracking_status);

        buildNotification();

    }

    public void updateTrackingOnProviderDisabled() {

        tracking_status = getString(R.string.tracking_disabled);
        notificationBuilder.setSmallIcon(R.drawable.trackme_notification_pause_icon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(tracking_status))
                .setContentText(tracking_status)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_map_black_24dp,
                        getString(R.string.location_service),
                        PendingIntent.getActivity(this, 0, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_CANCEL_CURRENT)
                ));

        buildNotification();

    }

    class PlaceAsyncTask extends AsyncTask<Double, Void, Void> {

        @Override
        protected Void doInBackground(Double ... args) {
            Address address = null;

            try {
                address = geocoder.getFromLocation(
                        args[0],
                        args[1],
                        1
                ).get(0);
            } catch (NullPointerException nullPointerException) {
                nullPointerException.printStackTrace();
            } catch (IOException exception) {
                // TODO: 2.06.2017 Alert for network connection
                exception.printStackTrace();
            }

            if(null != address) {
                place_name = address.getFeatureName().length() > 4 ? address.getFeatureName() : address.getAddressLine(0);
                String str_address = "";
                for(int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                    str_address += address.getAddressLine(i) + ", ";
                str_address.substring(0, str_address.length() - 2);
                TrackME.db.insertNotification((byte) 1, address.toString());
                TrackME.db.insertPlace(
                        place_name,
                        str_address,
                        args[2].intValue(),
                        place_freq,
                        trip_id
                );
                Log.d("ADN", "Address: " + address);
            }

            return null;
        }

    }
}