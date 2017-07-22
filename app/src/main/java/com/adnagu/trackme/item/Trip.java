package com.adnagu.trackme.item;

/**
 * Created by wmramazan on 21.05.2017.
 */

public class Trip {

    int id, start_location_id, end_location_id;
    String name;
    float distance, max_speed, avg_speed;
    double min_lat, max_lat;
    long date;

    public Trip(int id, int start_location_id, int end_location_id, String name, long date, float distance, float max_speed, float avg_speed, double min_lat, double max_lat) {
        this.id = id;
        this.start_location_id = start_location_id;
        this.end_location_id = end_location_id;
        this.name = name;
        this.date = date;
        this.distance = distance;
        this.max_speed = max_speed;
        this.avg_speed = avg_speed;
        this.min_lat = min_lat;
        this.max_lat = max_lat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStart_location_id() {
        return start_location_id;
    }

    public void setStart_location_id(int start_location_id) {
        this.start_location_id = start_location_id;
    }

    public int getEnd_location_id() {
        return end_location_id;
    }

    public void setEnd_location_id(int end_location_id) {
        this.end_location_id = end_location_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMax_speed() {
        return max_speed;
    }

    public void setMax_speed(float max_speed) {
        this.max_speed = max_speed;
    }

    public float getAvg_speed() {
        return avg_speed;
    }

    public void setAvg_speed(float avg_speed) {
        this.avg_speed = avg_speed;
    }

    public double getMin_lat() {
        return min_lat;
    }

    public void setMin_lat(double min_lat) {
        this.min_lat = min_lat;
    }

    public double getMax_lat() {
        return max_lat;
    }

    public void setMax_lat(double max_lat) {
        this.max_lat = max_lat;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", start_location_id=" + start_location_id +
                ", end_location_id=" + end_location_id +
                ", name='" + name + '\'' +
                ", distance=" + distance +
                ", max_speed=" + max_speed +
                ", avg_speed=" + avg_speed +
                ", min_lat=" + min_lat +
                ", max_lat=" + max_lat +
                ", date=" + date +
                '}';
    }
}
