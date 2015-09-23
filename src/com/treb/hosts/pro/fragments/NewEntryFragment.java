package com.treb.hosts.pro.fragments;

import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.treb.hosts.pro.R;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;


public class NewEntryFragment extends Fragment {

    private HostsEntry hostEntry;
    private final EventBus bus = EventBus.getInstance();
    public static final String ENTRY_KEY = "NewEntryBundleKey";

    private EditText ipAddressText;
    private EditText hostsAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_entry, container, false);
        Button saveButton = (Button) view.findViewById(R.id.newentry_save_button);
        Button cancelButton = (Button) view.findViewById(R.id.newentry_cancel_button);

        ipAddressText = (EditText) view.findViewById(R.id.newEntry_ipaddress_input);
        hostsAddress = (EditText) view.findViewById(R.id.newentry_hosts_input);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveEntryTask(hostEntry).execute();
                hostEntry = null;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEntry();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        if(hostEntry != null)
        {
            ipAddressText.setText(hostEntry.getIpAddress());
            hostsAddress.setText(hostEntry.getHostString());
        }
        else
        {
            ipAddressText.setText("");
            hostsAddress.setText("");
        }
        super.onResume();
    }

    public void setHostEntry(HostsEntry hostEntry)
    {
        this.hostEntry = hostEntry;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(ENTRY_KEY)) {
            Object bundledObject = savedInstanceState.get(ENTRY_KEY);
            if (bundledObject instanceof HostsEntry) {
                hostEntry = (HostsEntry) bundledObject;
            }

        }
        if (hostEntry == null)
            hostEntry = new HostsEntry();
    }

    private class SaveEntryTask extends AsyncTask<Void, Void, Void> {

        private final HostsEntry hostEntry;

        public SaveEntryTask(HostsEntry hostEntry)
        {
            this.hostEntry = hostEntry;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                this.hostEntry.setIpAddress(ipAddressText.getText().toString());
                String hostEntries = hostsAddress.getText().toString();
                hostEntry.clearHosts();
                if (hostEntries != null) {
                    for (String entry : hostEntries.split("\n")) {
                        hostEntry.addHost(entry);
                    }
                }

            } catch (Exception ex) {
                bus.fireEvent(Event.ERROR_SAVING_LIST);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(this.hostEntry == null)
                bus.fireEvent(Event.FAIL_TO_SAVE);
            if(this.hostEntry.getEntryId() == null)
                bus.fireEvent(Event.NEW_ENTRY_SAVE, this.hostEntry);
            else
                bus.fireEvent(Event.EXISTING_ENTRY_SAVE);
        }
    }

    private void cancelEntry() {
        hostEntry = null;
        bus.fireEvent(Event.NEW_ENTRY_CANCEL);
    }
}
