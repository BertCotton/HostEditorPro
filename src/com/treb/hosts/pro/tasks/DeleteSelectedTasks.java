package com.treb.hosts.pro.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;

import java.util.LinkedList;

public class DeleteSelectedTasks extends AsyncTask<Integer, Void, Void> {

    private final LinkedList<HostsEntry> sourceList;
    private final EventBus bus;

    public DeleteSelectedTasks(LinkedList<HostsEntry> sourceList, EventBus bus) {
        this.sourceList = sourceList;
        this.bus = bus;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        if (null == params || params.length == 0)
            return null;
        try {
            for (Integer index : params) {
                sourceList.remove(index.intValue());
            }
        } catch (Exception ex) {
            bus.fireEvent(Event.ERROR, "Error deleting selected entries");
            Log.e(Constants.LOG_NAME, "Error deleting selected entries", ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        bus.fireEvent(Event.SAVE_HOST_LIST);
    }

}
