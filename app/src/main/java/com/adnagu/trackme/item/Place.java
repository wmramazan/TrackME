package com.adnagu.trackme.item;

/**
 * Created by wmramazan on 21.05.2017.
 */

public class Place {

    int id, visit, time;
    long date, last_visit_date;
    String name, address;
    double latitude, longitude;

    public Place(int id, String name, String address, long date, long last_visit_date, int visit, int time) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.date = date;
        this.last_visit_date = last_visit_date;
        this.visit = visit;
        this.time = time;
    }

    public Place(int id, String name, String address, long date, long last_visit_date, int visit, int time, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.date = date;
        this.last_visit_date = last_visit_date;
        this.visit = visit;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLast_visit_date() {
        return last_visit_date;
    }

    public void setLast_visit_date(long last_visit_date) {
        this.last_visit_date = last_visit_date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", visit=" + visit +
                ", time=" + time +
                ", date=" + date +
                ", last_visit_date=" + last_visit_date +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
