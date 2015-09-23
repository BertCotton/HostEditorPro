package com.treb.hosts.pro.backup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.file.BackupFileReader;
import com.treb.hosts.pro.file.HostWriter;

import java.io.File;
import java.util.LinkedList;

public class BackupDialog extends Dialog {

    private BackupFile backupFile;

    public BackupDialog(Context context, BackupFile backupFile) {
        super(context);

        this.backupFile = backupFile;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manage Backup");


    }

    private static class RenameButtonClick implements View.OnClickListener {

        AlertDialog.Builder alertBuilder;

        public RenameButtonClick(final BackupDialog backupDialog, Context context, final BackupFile backupFile) {
            final EditText input = new EditText(context);
            input.setText(backupFile.getFileName());
            alertBuilder = new AlertDialog.Builder(context).setTitle("Backup Rename").setView(input)
                    .setPositiveButton("Ok", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newName = input.getText().toString();
                            File file = new File(backupFile.getPath());
                            File newFile = new File(file.getParent() + "/" + newName);
                            if (file.exists() && !newFile.exists())
                                file.renameTo(newFile);
                            backupDialog.dismiss();

                        }
                    }).setNegativeButton("Cancel", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }

        @Override
        public void onClick(View v) {
            alertBuilder.show();
        }

    }

    private static class DeleteFileButtonClick implements View.OnClickListener {

        private BackupFile backupFile;
        private BackupDialog backupDialog;

        public DeleteFileButtonClick(BackupDialog backupDialog, Context context, BackupFile backupFile) {
            this.backupFile = backupFile;
            this.backupDialog = backupDialog;
        }

        @Override
        public void onClick(View v) {
            File file = new File(backupFile.getPath());
            file.delete();
            backupDialog.dismiss();
        }
    }

    private static class ShareFileButtonClick implements View.OnClickListener {

        private BackupFile backupFile;
        private BackupDialog backupDialog;
        private Context context;

        public ShareFileButtonClick(BackupDialog backupDialog, Context context, BackupFile backupFile) {
            this.backupFile = backupFile;
            this.backupDialog = backupDialog;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            File file = new File(backupFile.getPath());
            if (!file.isFile() || !file.exists()) {
                Toast.makeText(context, "Error attaching file, it doesn't exist", Toast.LENGTH_SHORT);
                return;
            }
            Uri attachment = Uri.fromFile(file);

            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.putExtra(Intent.EXTRA_SUBJECT, "Hosts File - " + backupFile.getFileName());
            intent.setType("text/xml");
            intent.putExtra(Intent.EXTRA_STREAM, attachment);

            context.startActivity(Intent.createChooser(intent, "Send Hosts File"));

            backupDialog.dismiss();

        }
    }

    private static class LoadButtonClick implements View.OnClickListener {

        private BackupFile backupFile;
        private BackupDialog backupDialog;

        public LoadButtonClick(BackupDialog backupDialog, BackupFile backupFile) {
            this.backupDialog = backupDialog;
            this.backupFile = backupFile;
        }

        @Override
        public void onClick(View v) {
            try {
                LinkedList<HostsEntry> entries = BackupFileReader.loadFromFile(new File(backupFile.getPath()));
                HostWriter.save(entries);
            } catch (Exception ex) {
                Log.e(Constants.LOG_NAME, "Error loading backup", ex);
            }
            backupDialog.dismiss();
        }
    }


}
