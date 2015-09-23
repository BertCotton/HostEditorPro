package com.treb.hosts.pro.tasks;

import android.os.AsyncTask;

import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;

import java.util.LinkedList;

public class ToggleSelectedTask extends AsyncTask<Integer, Void, Void> {

    private final LinkedList<HostsEntry> sourceList;
    private final EventBus bus;

    public ToggleSelectedTask(LinkedList<HostsEntry> sourceList, EventBus bus) {
        this.sourceList = sourceList;
        this.bus = bus;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        if (null == params || params.length == 0)
            return null;
        try {

            for (Integer entryId : params) {
                sourceList.get(entryId).toggle();
            }
        } catch (Exception ex) {
            bus.fireEvent(Event.ERROR, "Error toggling hosts entry");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        bus.fireEvent(Event.SAVE_HOST_LIST);
    }

}
