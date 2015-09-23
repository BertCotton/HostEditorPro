package com.treb.hosts.pro.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;
import com.treb.hosts.pro.events.EventListener;
import com.treb.hosts.pro.file.HostsParser;
import com.treb.hosts.pro.hostsList.HostsListArrayAdapter;
import com.treb.hosts.pro.tasks.SaveHostsTask;

import java.util.LinkedList;

public class HostsListFragment extends ListFragment implements EventListener {
    private HostsListArrayAdapter arrayAdapter;
    private EventBus bus = EventBus.getInstance();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setFastScrollEnabled(true);
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

    public LinkedList<HostsEntry> getItems() {
        return arrayAdapter.getList();
    }

    public Integer[] getSelectedIndexes() {
        return arrayAdapter.getSelectedIndexes().toArray(new Integer[arrayAdapter.getSelectedIndexes().size()]);
    }

    public void clearSelectedIndexes() {
        arrayAdapter.clearSelectedIndexes();
    }

    public void saveList() {
        new SaveHostsTask(bus).execute(arrayAdapter.getList());
    }

    public void addEntry(HostsEntry entry){

        arrayAdapter.add(entry);
        saveList();
    }

    public void loadList() {
        if (null == arrayAdapter) {
            arrayAdapter = new HostsListArrayAdapter(getActivity(), new LinkedList<HostsEntry>(), bus);
        }
        new HostsLoader().execute(Pair.create(false, false));
    }

    @Override
    public void notify(Event event, Object value) {
        if (event == Event.REFRESH_LIST) {
            if (isVisible())
                loadList();
            else {
                arrayAdapter = null;
                bus.fireEvent(Event.LOADING_FINISHED);
            }
        }

    }

    private class HostsLoader extends AsyncTask<Pair<Boolean, Boolean>, Void, LinkedList<HostsEntry>> {


        @Override
        protected LinkedList<HostsEntry> doInBackground(Pair<Boolean, Boolean>... params) {
            Pair<Boolean, Boolean> loadOptions = params[0];
            try {
                return HostsParser.parseHostFile(loadOptions.first, loadOptions.second);
            } catch (Exception ex) {
                Log.d("Hosts", "Error parsing hosts file. " + ex.getMessage());
                bus.fireEvent(Event.ERROR, "Error parsing hosts file");
                return new LinkedList<HostsEntry>();
            }
        }

        @Override
        protected void onPostExecute(LinkedList<HostsEntry> hostsEntries) {
            arrayAdapter.setList(hostsEntries);
            setListShown(true);
            bus.fireEvent(Event.LOADING_FINISHED);
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
