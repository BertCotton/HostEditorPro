package com.treb.hosts.pro.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.backup.BackupFile;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;
import com.treb.hosts.pro.file.BackupFileReader;
import com.treb.hosts.pro.file.HostWriter;

import java.io.File;
import java.util.LinkedList;

public class RestoreTask extends AsyncTask<BackupFile, Void, Void> {
    private EventBus bus = EventBus.getInstance();

    @Override
    protected Void doInBackground(BackupFile... params) {
        if (params == null || params.length != 1)
            return null;

        try {
            BackupFile backupFile = params[0];
            LinkedList<HostsEntry> restoredEntries = new BackupFileReader().loadFromFile(new File(backupFile.getPath()));
            HostWriter.save(restoredEntries);
            return null;
        } catch (Exception ex) {
            bus.fireEvent(Event.ERROR, "Error restoring file backup.");
            Log.e(Constants.LOG_NAME, "Error restoring backup file.", ex);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        bus.fireEvent(Event.RESTORE_COMPLETE);
    }
}
