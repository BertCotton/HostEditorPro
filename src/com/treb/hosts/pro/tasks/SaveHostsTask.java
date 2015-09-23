package com.treb.hosts.pro.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;
import com.treb.hosts.pro.file.HostWriter;
import com.treb.hosts.pro.file.UnableToMountSystemException;

import java.util.LinkedList;

public class SaveHostsTask extends AsyncTask<LinkedList<HostsEntry>, Void, Boolean> {
    private final EventBus bus;

    public SaveHostsTask(EventBus bus) {

        this.bus = bus;
    }

    @Override
    protected Boolean doInBackground(LinkedList<HostsEntry>... hostEntries) {
        if (null == hostEntries || hostEntries.length != 1 || null == hostEntries[0])
            return null;
        try {

            try {
                HostWriter.save(hostEntries[0]);
                return true;
            } catch (UnableToMountSystemException ex) {
                bus.fireEvent(Event.ERROR_SAVING_LIST);
                Log.e(Constants.LOG_NAME, "ERROR SAVING HOSTS: " + ex.getMessage());
            }

        } catch (Exception ex) {
            bus.fireEvent(Event.ERROR, "Error saving hosts list");
            Log.e(Constants.LOG_NAME, "Error saving hosts lists", ex);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            bus.fireEvent(Event.SAVE_COMPLETE);
            bus.fireEvent(Event.REFRESH_LIST);
        }
    }

}
