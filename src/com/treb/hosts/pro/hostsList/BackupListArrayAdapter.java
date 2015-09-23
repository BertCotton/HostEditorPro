package com.treb.hosts.pro.hostsList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.treb.hosts.pro.R;
import com.treb.hosts.pro.backup.BackupFile;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;
import com.treb.hosts.pro.tasks.DeleteBackupTask;
import com.treb.hosts.pro.tasks.RestoreTask;

import java.text.DateFormat;
import java.util.LinkedList;

public class BackupListArrayAdapter extends ArrayAdapter<BackupFile> {
    private static class ViewHolder {
        private TextView backupName;
        private TextView backupDate;
    }

    private LinkedList<BackupFile> backupFiles;
    private final Activity context;
    private final DateFormat df = DateFormat.getDateTimeInstance();

    public BackupListArrayAdapter(Activity context, LinkedList<BackupFile> backupFiles) {
        super(context, R.layout.backup_file_row);
        this.context = context;
        this.backupFiles = backupFiles;
    }

    public void setList(LinkedList<BackupFile> backupFiles) {
        this.clear();
        this.addAll(backupFiles);
        this.notifyDataSetChanged();
        this.backupFiles = backupFiles;
    }

    public LinkedList<BackupFile> getList() {
        return backupFiles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (null == rowView) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.backup_file_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.backupName = (TextView) rowView.findViewById(R.id.backup_row_filename);
            viewHolder.backupDate = (TextView) rowView.findViewById(R.id.backup_row_date);

            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();

        final BackupFile backupFile = backupFiles.get(position);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder holder = (ViewHolder) v.getTag();
                ViewHolderDialog viewHolderDialog = new ViewHolderDialog(getContext(), backupFile);
                viewHolderDialog.show();
            }
        });
        holder.backupName.setText(backupFile.getFileName());
        holder.backupDate.setText(df.format(backupFile.getSavedDate()));
        return rowView;

    }

    private class ViewHolderDialog extends Dialog {
        private final EventBus bus = EventBus.getInstance();

        public ViewHolderDialog(Context context, final BackupFile backupFile) {
            super(context);
            setContentView(R.layout.backup_click_dialog);
            setTitle("File Backup");

            Button restoreButton = (Button) findViewById(R.id.backup_dialog_restore);
            Button deleteButton = (Button) findViewById(R.id.backup_dialog_delete);
            Button cancelButton = (Button) findViewById(R.id.backup_dialog_cancel);

            restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.fireEvent(Event.RESTORING_BACKUP);
                    new RestoreTask().execute(backupFile);
                    ViewHolderDialog.this.hide();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.fireEvent(Event.DELETING_BACKUP);
                    new DeleteBackupTask().execute(backupFile);
                    ViewHolderDialog.this.hide();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolderDialog.this.hide();
                }
            });
        }


    }
}
