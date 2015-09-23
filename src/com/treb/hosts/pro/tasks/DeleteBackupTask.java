package com.treb.hosts.pro.tasks;

import android.os.AsyncTask;

import com.treb.hosts.pro.backup.BackupFile;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;

import java.io.File;

public class DeleteBackupTask extends AsyncTask<BackupFile, Void, Void> {
    private final EventBus bus = EventBus.getInstance();

    @Override
    protected Void doInBackground(BackupFile... params) {
        if (params == null || params.length != 1)
            return null;
        try {
            BackupFile backupFile = params[0];
            File file = new File(backupFile.getPath());
            if (file.exists())
                file.delete();
        } catch (Exception ex) {
            bus.fireEvent(Event.ERROR, "Error deleting backup file");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        bus.fireEvent(Event.REFRESH_BACKUPS);

    }
}
