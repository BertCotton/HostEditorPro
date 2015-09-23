package com.treb.hosts.pro.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;

import java.util.LinkedList;

public class SearchTask extends AsyncTask<String, Void, Integer> {
    private final LinkedList<HostsEntry> sourceList;
    private final EventBus bus;
    private final int startingIndex;
    private final boolean forwardSearch;

    public SearchTask(LinkedList<HostsEntry> sourceList, EventBus bus, int startingIndex, boolean forwardSearch) {
        this.sourceList = sourceList;
        this.bus = bus;
        if(startingIndex < 0)
            startingIndex = 0;
        this.startingIndex = startingIndex;
        this.forwardSearch = forwardSearch;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            String searchTerm = strings[0].toLowerCase();
            if (forwardSearch)
                return doForwardSearch(searchTerm);
            else
                return doReverseSearch(searchTerm);
        } catch (Exception ex) {
            bus.fireEvent(Event.ERROR, "Error searching through list");
            Log.e(Constants.LOG_NAME, "Error searching through list", ex);
        }
        return null;
    }

    private Integer doForwardSearch(String searchTerm) {

        for (int i = startingIndex; i < sourceList.size(); i++) {
            if (sourceList.get(i).getHostString().toLowerCase().contains(searchTerm))
                return i;
        }
         for (int i = 0; i < startingIndex; i++) {
                if (sourceList.get(i).getHostString().toLowerCase().contains(searchTerm))
                    return i;
         }


        return null;
    }

    private Integer doReverseSearch(String searchTerm) {
        for (int i = startingIndex; i >= 0; i--) {
            if (sourceList.get(i).getHostString().toLowerCase().contains(searchTerm))
                return i;
        }
        for (int i = sourceList.size(); i >= startingIndex; i--) {
            if (sourceList.get(i).getHostString().toLowerCase().contains(searchTerm))
                return i;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer foundIndex) {
        if (foundIndex == null)
            bus.fireEvent(Event.NOTHING_FOUND);
        else
            bus.fireEvent(Event.SCROLL_TO_INDEX, foundIndex);

    }
}
