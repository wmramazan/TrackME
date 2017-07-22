package com.adnagu.trackme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.adnagu.trackme.adapter.NotificationListAdapter;
import com.adnagu.trackme.item.Notification;

import java.util.ArrayList;

/**
 * Created by wmramazan on 27.05.2017.
 */

public class NotificationsFragment extends Fragment {

    View view;
    RecyclerView notificationList;
    ArrayList<Notification> notifications;
    NotificationListAdapter notificationListAdapter;
    LinearLayout noContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_item, container, false);

        noContent = (LinearLayout) view.findViewById(R.id.list_item_noContent);
        notificationList = (RecyclerView) view.findViewById(R.id.list_item);

        // TODO: 11.06.2017 Clear logs, add notifications

        notifications = TrackME.db.getNotifications();

        if(notifications.size() == 0)
            noContent.setVisibility(View.VISIBLE);
        else {
            notificationList.setLayoutManager(new LinearLayoutManager(getContext()));
            notificationList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
            notificationListAdapter = new NotificationListAdapter(getContext(), notifications);
            notificationList.setAdapter(notificationListAdapter);
        }

        return view;
    }

}
