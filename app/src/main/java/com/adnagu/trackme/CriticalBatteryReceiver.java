package com.adnagu.trackme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wmramazan on 29.05.2017.
 */

public class CriticalBatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(TrackME.pref.getBoolean("low_battery", false)) {

            context.startService(new Intent(context, LocationService.class).setAction("STOP"));
            TrackME.db.insertNotification((byte) 2, context.getString(R.string.low_battery_title));

        }

    }
}
