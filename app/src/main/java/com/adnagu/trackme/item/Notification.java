package com.adnagu.trackme.item;

/**
 * Created by wmramazan on 28.05.2017.
 */

public class Notification {

    private int id;
    private byte type;
    private String data;
    private long date;

    public Notification(int id, byte type, String data, long date) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
