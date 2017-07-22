package com.adnagu.trackme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.adnagu.trackme.item.Location;
import com.adnagu.trackme.item.Notification;
import com.adnagu.trackme.item.Place;
import com.adnagu.trackme.item.Trip;

import java.util.ArrayList;

/**
 * Created by wmramazan on 21.05.2017.
 */

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "TrackME";
    public static final int DATABASE_VERSION = 1;
    SQLiteDatabase db;
    ContentValues values;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE location (" +
                "id INTEGER PRIMARY KEY," +
                "lat DOUBLE," +
                "long DOUBLE," +
                "alt DOUBLE," +
                "date BIGINT," +
                "speed FLOAT," +
                "trip_id INTEGER," +
                "place_id INTEGER," +
                "FOREIGN KEY(trip_id) REFERENCES trip(id) ON DELETE CASCADE," +
                "FOREIGN KEY(place_id) REFERENCES place(id))");
        db.execSQL("CREATE TABLE trip (" +
                "id INTEGER PRIMARY KEY," +
                "start_location INTEGER," +
                "end_location INTEGER," +
                "name TEXT," +
                "date BIGINT," +
                "distance FLOAT," +
                "max_speed FLOAT," +
                "avg_speed FLOAT," +
                "min_lat DOUBLE," +
                "max_lat DOUBLE," +
                "FOREIGN KEY(start_location) REFERENCES location(id)," +
                "FOREIGN KEY(end_location) REFERENCES location(id))");
        db.execSQL("CREATE TABLE place (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "address TEXT," +
                "date BIGINT," +
                "last_visit BIGINT," +
                "visit INTEGER," +
                "time INTEGER," +
                "trip_id INTEGER)");
        db.execSQL("CREATE TABLE notification (" +
                "id INTEGER PRIMARY KEY," +
                "type INTEGER," +
                "data TEXT," +
                "date BIGINT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS location");
        db.execSQL("DROP TABLE IF EXISTS trip");
        db.execSQL("DROP TABLE IF EXISTS place");
        db.execSQL("DROP TABLE IF EXISTS notification");
        onCreate(db);
    }

    public void resetTables() {
        db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS location");
        db.execSQL("DROP TABLE IF EXISTS trip");
        db.execSQL("DROP TABLE IF EXISTS place");
        db.execSQL("DROP TABLE IF EXISTS notification");
        onCreate(db);
    }

    public int insertLocation(double latitude, double longitude, double altitude, long date, float speed, int trip_id) {
        db = this.getWritableDatabase();
        values = new ContentValues();
        values.put("lat", latitude);
        values.put("long", longitude);
        values.put("alt", altitude);
        values.put("date", date);
        values.put("speed", speed);
        values.put("trip_id", trip_id);

        return (int) db.insert("location", null, values);
    }

    public int insertTrip(String name) {
        db = this.getWritableDatabase();
        values = new ContentValues();
        values.put("name", name);
        values.put("date", System.currentTimeMillis());

        return (int) db.insert("trip", null, values);
    }

    public void updateTrip(int id, float distance) {
        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " +
                "MIN(id), " +
                "MAX(id), " +
                "MAX(speed), " +
                "AVG(speed), " +
                "MIN(lat), " +
                "MAX(lat) " +
                "FROM location WHERE trip_id = " + id, null);

        if(cursor.moveToFirst()) {
            int start_location_id = cursor.getInt(0);
            int end_location_id = cursor.getInt(1);
            float max_speed = cursor.getFloat(2);
            float avg_speed = cursor.getFloat(3);
            double min_latitude = cursor.getDouble(4);
            double max_latitude = cursor.getDouble(5);

            Log.d("ADN", "updateTrip: " + start_location_id + " - " + end_location_id);

            db = this.getWritableDatabase();

            values = new ContentValues();
            values.put("start_location", start_location_id);
            values.put("end_location", end_location_id);
            values.put("distance", distance);
            values.put("max_speed", max_speed);
            values.put("avg_speed", avg_speed);
            values.put("min_lat", min_latitude);
            values.put("max_lat", max_latitude);

            db.update("trip", values, "id = " + id, null);
        } else {
            deleteTrip(id);
        }
    }

    public void deleteTrip(int id) {
        db = this.getWritableDatabase();
        db.delete("trip", "id = " + id, null);
    }

    public void insertPlace(String name, String address, int start_location_id, int place_freq, int trip_id) {
        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, visit, time, trip_id FROM place WHERE name = '" + name + "'", null);
        int place_id;

        db = this.getWritableDatabase();
        values = new ContentValues();
        values.put("last_visit", System.currentTimeMillis());

        if(cursor.moveToFirst()) {

            place_id = cursor.getInt(0);
            if(trip_id != cursor.getInt(3)) {
                values.put("visit", cursor.getInt(1) + 1);
                values.put("trip_id", trip_id);
            }
            values.put("time", cursor.getInt(2) + 1);
            db.update("place", values, "id = " + place_id, null);

        } else {

            values.put("name", name);
            values.put("address", address);
            values.put("date", System.currentTimeMillis());
            values.put("visit", 1);
            values.put("time", 1);
            values.put("trip_id", trip_id);

            place_id = (int) db.insert("place", null, values);
            
        }

        db.execSQL("UPDATE location SET place_id = " + place_id + " WHERE id IN ( SELECT id FROM location WHERE id >= " + start_location_id + " LIMIT " + place_freq + " )");
    }
    
    public void updatePlaceName(int id, String name) {
        db = this.getWritableDatabase();

        values = new ContentValues();
        values.put("name", name);

        db.update("place", values, "id = " + id, null);
    }

    public void insertNotification(byte type, String data) {
        db = this.getWritableDatabase();
        values = new ContentValues();
        values.put("type", type);
        values.put("data", data);
        values.put("date", System.currentTimeMillis());

        db.insert("notification", null, values);
    }

    public ArrayList<Notification> getNotifications() {
        ArrayList<Notification> notifications = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notification ORDER BY id DESC", null);

        while (cursor.moveToNext())
            notifications.add(new Notification(cursor.getInt(0), (byte) cursor.getInt(1), cursor.getString(2), cursor.getLong(3)));

        return notifications;
    }

    public ArrayList<Location> getLocations(int trip_id) {
        ArrayList<Location> locations = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor;
        if(trip_id == 0)
            cursor = db.rawQuery("SELECT * FROM location WHERE trip_id = ( SELECT id FROM trip ORDER BY id DESC LIMIT 1 )", null);
        else
            cursor = db.rawQuery("SELECT * FROM location WHERE trip_id = " + trip_id, null);

        while (cursor.moveToNext())
            locations.add(new Location(
                    cursor.getInt(0),
                    cursor.getDouble(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3),
                    cursor.getLong(4),
                    cursor.getFloat(5),
                    cursor.getInt(6),
                    cursor.getInt(7)
            ));

        return locations;
    }

    public ArrayList<Place> getPlaces() {
        ArrayList<Place> places = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM place ORDER BY id DESC", null);

        while (cursor.moveToNext())
            places.add(new Place(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getInt(5),
                    cursor.getInt(6)
            ));

        return places;
    }

    public ArrayList<Place> getPlaces(long start_date, long end_date) {
        ArrayList<Place> places = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT place.* FROM location, place WHERE location.place_id = place.id AND location.date >= " + start_date + " AND location.date <= " + end_date + " GROUP BY place.id ORDER BY time DESC", null);

        while (cursor.moveToNext())
            places.add(new Place(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getInt(5),
                    cursor.getInt(6)
            ));

        return places;
    }

    public ArrayList<Place> getPlaces(int trip_id) {
        ArrayList<Place> places = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor;

        if(trip_id == 0)
            cursor = db.rawQuery("SELECT place.*, location.lat, location.long FROM place, location WHERE place.id = location.place_id AND location.trip_id = ( SELECT id FROM trip ORDER BY id DESC LIMIT 1 ) GROUP BY place.id ORDER BY visit DESC", null);
        else
            cursor = db.rawQuery("SELECT place.*, location.lat, location.long FROM place, location WHERE place.id = location.place_id AND location.trip_id = " + trip_id + " GROUP BY place.id ORDER BY visit DESC", null);

        while (cursor.moveToNext())
            places.add(new Place(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getInt(5),
                    cursor.getInt(6),
                    cursor.getDouble(8),
                    cursor.getDouble(9)
            ));

        return places;
    }

    public ArrayList<Place> getTopPlacesBasedOnTime() {
        ArrayList<Place> places = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM place ORDER BY time DESC LIMIT 5", null);

        while (cursor.moveToNext())
            places.add(new Place(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getLong(4),
                    cursor.getInt(5),
                    cursor.getInt(6)
            ));

        return places;
    }

    public ArrayList<Trip> getTrips() {
        ArrayList<Trip> trips = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM trip ORDER BY id DESC", null);

        while (cursor.moveToNext())
            trips.add(new Trip(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    cursor.getFloat(5),
                    cursor.getFloat(6),
                    cursor.getFloat(7),
                    cursor.getDouble(8),
                    cursor.getDouble(9)
            ));

        return trips;
    }

    public int getNumberOfTrips() {
        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM trip", null);
        if(cursor.moveToFirst())
            return cursor.getInt(0);
        else
            return 0;
    }

    public int[] getNumbersOfActivities() {

        int[] numbers = null;
        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " +
                "( SELECT COUNT(*) FROM location WHERE speed < 1 ), " +
                "( SELECT COUNT(*) FROM location WHERE speed >= 1 and speed < 10 ), " +
                "( SELECT COUNT(*) FROM location WHERE speed >= 10 )"
                , null);
        if(cursor.moveToFirst()) {
            numbers = new int[3];
            numbers[0] = cursor.getInt(0);
            numbers[1] = cursor.getInt(1);
            numbers[2] = cursor.getInt(2);
        }

        return numbers;

    }

    public Trip getLastTrip() {
        Trip trip = null;
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM trip ORDER BY id DESC LIMIT 1", null);

        if(cursor.moveToFirst()) {
            trip = new Trip(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    cursor.getFloat(5),
                    cursor.getFloat(6),
                    cursor.getFloat(7),
                    cursor.getDouble(8),
                    cursor.getDouble(9)
            );
        }

        return trip;
    }

    /*
    public ArrayList<ChannelItem> getSuggestedChannels() {
        ArrayList<ChannelItem> channels = new ArrayList<ChannelItem>();

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM suggested_channel", null);

        if (cursor.moveToFirst()) {
            do {
                channels.add(new ChannelItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), "Desc.", cursor.getString(3), 0, false));
            } while (cursor.moveToNext());
        }

        return channels;
    }
     */

    /*
    public boolean isLiked(int id) {
        db = this.getReadableDatabase();
        return db.rawQuery("SELECT id FROM like WHERE like.id = "+id, null).moveToFirst();
    }
    */

    /*
    public void deleteLike(int id) {
        db = this.getWritableDatabase();
        db.delete("like", "id" + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
     */



}
