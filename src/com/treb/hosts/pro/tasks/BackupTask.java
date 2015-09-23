package com.treb.hosts.pro.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;
import com.treb.hosts.pro.file.BackupFileWriter;

import java.util.LinkedList;

public class BackupTask extends AsyncTask<LinkedList<HostsEntry>, Void, Void> {
    private EventBus bus = EventBus.getInstance();

    @Override
    protected Void doInBackground(LinkedList<HostsEntry>... params) {
        if (params == null || params.length != 1)
            return null;
        try {

            LinkedList<HostsEntry> entries = params[0];
            BackupFileWriter.saveFile(entries);
        } catch (Exception ex) {
            bus.fireEvent(Event.ERROR, "Error backing up file.");
            Log.e(Constants.LOG_NAME, "Error backing up file", ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        bus.fireEvent(Event.BACKUP_COMPLETE);
    }
}
