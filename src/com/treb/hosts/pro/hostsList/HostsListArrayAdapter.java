package com.treb.hosts.pro.hostsList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.treb.hosts.pro.R;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HostsListArrayAdapter extends ArrayAdapter<HostsEntry> {
    private final Activity context;
    private final EventBus bus;

    private static class ViewHolder {
        public int index;
        public CheckBox selected;
        public TextView ipAddress;
        public TextView hostEntry;
        public TextView comments;
    }

    private LinkedList<HostsEntry> hostEntries;
    private final List<Integer> selectedItems = new ArrayList<Integer>();

    public HostsListArrayAdapter(Activity context, LinkedList<HostsEntry> hostEntries, EventBus bus) {
        super(context, R.layout.host_entry_activity_row, hostEntries);
        this.context = context;
        this.hostEntries = hostEntries;
        this.bus = bus;

    }

    public void setList(LinkedList<HostsEntry> hostEntries) {
        this.clear();
        this.addAll(hostEntries);
        this.hostEntries = hostEntries;
        this.notifyDataSetChanged();

    }

    @Override
    public void add(HostsEntry object) {
        object.setEntryId(this.hostEntries.size());
        this.hostEntries.add(object);
        setList(this.hostEntries);
    }

    public LinkedList<HostsEntry> getList() {
        return hostEntries;
    }

    public List<Integer> getSelectedIndexes() {
        return selectedItems;
    }

    public void clearSelectedIndexes() {
        for (Integer index : selectedItems)
            if(hostEntries.size() < index)
                hostEntries.get(index).setSelected(false);
        selectedItems.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (null == rowView) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.host_entry_activity_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.selected = (CheckBox) rowView.findViewById(R.id.cbSelected);

            viewHolder.ipAddress = (TextView) rowView.findViewById(R.id.txtIP);
            viewHolder.hostEntry = (TextView) rowView.findViewById(R.id.txtEntries);
            viewHolder.comments = (TextView) rowView.findViewById(R.id.txtComments);
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        if(hostEntries.size() <= position)
            return rowView;
        final HostsEntry hostsEntry = hostEntries.get(position);
        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                bus.fireEvent(Event.UPDATE_ENTRY, hostsEntry);
                return true;
            }
        });
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean selected) {
                if (selected)
                    selectedItems.add(hostsEntry.getEntryId());
                else if (selectedItems.size() > hostsEntry.getEntryId())
                    selectedItems.remove(hostsEntry.getEntryId());

            }
        });
        holder.index = hostsEntry.getEntryId();
        holder.ipAddress.setText(hostsEntry.getIpAddress());
        holder.hostEntry.setText(hostsEntry.getHostString());
        if (hostsEntry.isCommentOnly()) {
            holder.ipAddress.setVisibility(View.GONE);
            holder.hostEntry.setVisibility(View.GONE);
        } else {
            holder.ipAddress.setVisibility(View.VISIBLE);
            holder.hostEntry.setVisibility(View.VISIBLE);
        }
        holder.selected.setChecked(hostsEntry.isSelected());
        holder.comments.setText(hostsEntry.getComment());

        return rowView;

    }
}
