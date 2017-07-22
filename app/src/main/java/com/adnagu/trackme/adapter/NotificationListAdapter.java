package com.adnagu.trackme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adnagu.trackme.R;
import com.adnagu.trackme.item.Notification;

import java.util.ArrayList;

/**
 * Created by wmramazan on 28.05.2017.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder> {

    private Context context;
    private ArrayList<Notification> notifications;
    private Notification notification;

    public NotificationListAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public NotificationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(NotificationListViewHolder holder, int position) {
        notification = notifications.get(position);

        holder.text.setText(notification.getData());

        /*
        switch (notification.getType()) {
            case 1:
                break;
            case 2:
                break;
            default:
                //holder.icon.setImageResource(R.drawable.trackme_logo);
                holder.text.setText(notification.getData());
        }
        */

        holder.date.setText(DateUtils.getRelativeTimeSpanString(notification.getDate(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
    }

    @Override
    public int getItemCount() {
        return (null != notifications ? notifications.size() : 0);
    }

    public static class NotificationListViewHolder extends RecyclerView.ViewHolder {

        protected TextView text, date;
        protected ImageView icon;

        public NotificationListViewHolder(View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.notification_text);
            this.date = (TextView) itemView.findViewById(R.id.notification_date);
            this.icon = (ImageView) itemView.findViewById(R.id.notification_icon);
        }

    }
}
