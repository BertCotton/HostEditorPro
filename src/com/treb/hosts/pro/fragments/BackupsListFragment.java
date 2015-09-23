package com.treb.hosts.pro.fragments;

import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.treb.hosts.pro.backup.BackupFile;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;
import com.treb.hosts.pro.events.EventListener;
import com.treb.hosts.pro.file.BackupFileReader;
import com.treb.hosts.pro.hostsList.BackupListArrayAdapter;

import java.util.LinkedList;

public class BackupsListFragment extends ListFragment implements EventListener {

    private BackupListArrayAdapter arrayAdapter;
    private EventBus bus = EventBus.getInstance();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bus.register(this);
        if (null == arrayAdapter) {
            bus.fireEvent(Event.LOADING_FILE);
            setListShown(false);
            loadList();
            setListAdapter(arrayAdapter);
            setRetainInstance(true);
        } else {
            setListShown(true);
            bus.fireEvent(Event.LOADING_FINISHED);
        }
    }

    @Override
    public void notify(Event event, Object value) {
        if (event == Event.REFRESH_BACKUPS) {
            if (isVisible())
                loadList();
            else {
                arrayAdapter = null;
                bus.fireEvent(Event.LOADING_FINISHED);
            }
        }
    }

    public void loadList() {
        if (null == arrayAdapter) {
            arrayAdapter = new BackupListArrayAdapter(getActivity(), new LinkedList<BackupFile>());
        }
        new BackupLoader().execute();
    }

    private class BackupLoader extends AsyncTask<Void, Void, LinkedList<BackupFile>> {
        @Override
        protected LinkedList<BackupFile> doInBackground(Void... params) {
            try {
                return BackupFileReader.getBackupFiles();
            } catch (Exception ex) {
                bus.fireEvent(Event.ERROR, "Error parsing backup files");
                Log.d("Hosts", "Error parsing backup files. " + ex.getMessage());
            }
            return new LinkedList<BackupFile>();
        }

        @Override
        protected void onPostExecute(LinkedList<BackupFile> backupFiles) {
            arrayAdapter.setList(backupFiles);
            setListShown(true);
            bus.fireEvent(Event.LOADING_FINISHED);
        }
    }
}
