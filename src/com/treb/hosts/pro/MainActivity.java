package com.treb.hosts.pro;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;
import com.treb.hosts.pro.events.EventListener;
import com.treb.hosts.pro.file.FileSizeChecker;
import com.treb.hosts.pro.fragments.BackupsListFragment;
import com.treb.hosts.pro.fragments.HostsListFragment;
import com.treb.hosts.pro.fragments.NewEntryFragment;
import com.treb.hosts.pro.fragments.SearchFragment;
import com.treb.hosts.pro.tasks.BackupTask;
import com.treb.hosts.pro.tasks.DeleteSelectedTasks;
import com.treb.hosts.pro.tasks.SaveHostsTask;
import com.treb.hosts.pro.tasks.SearchTask;
import com.treb.hosts.pro.tasks.ToggleSelectedTask;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private FragmentManager fragmentManager;
    private HostsListFragment hostsListFragment;
    private BackupsListFragment backupsListFragment;
    private NewEntryFragment newEntryFragment;
    private SearchFragment searchFragment;
    private final EventBus bus = EventBus.getInstance();
    private ProgressDialog progressDialog = null;
    private boolean largeFile = false;
    private MENU menuDisplay = MENU.MAIN;



    private enum MENU {MAIN, BACKUP}

    ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hostsListFragment = new HostsListFragment();
        backupsListFragment = new BackupsListFragment();
        newEntryFragment = new NewEntryFragment();
        searchFragment = new SearchFragment();

        long fileSize = FileSizeChecker.getFileSize();
        largeFile = fileSize > 1024;
        Log.i(Constants.LOG_NAME, "Hosts File is Large: " + fileSize + " bytes.");
        if (largeFile)
            new AlertDialog.Builder(this).setMessage(R.string.large_file_alert).setCancelable(false).setPositiveButton(R.string.ok, null).show();

        setContentView(R.layout.tab_navigation);

        fragmentManager = getSupportFragmentManager();

        if(findViewById(R.id.tab_mainLayout) != null)
        {
            if(savedInstanceState != null)
                return;
        }

        getSupportFragmentManager().beginTransaction().add(R.id.tab_mainLayout, hostsListFragment).commit();
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab hostsEntriesTab = actionBar.newTab();
        hostsEntriesTab.setText("Hosts Entries");
        hostsEntriesTab.setTabListener(this);
        actionBar.addTab(hostsEntriesTab);

        ActionBar.Tab backupsTab = actionBar.newTab();
        backupsTab.setText("Backups");
        backupsTab.setTabListener(this);
        actionBar.addTab(backupsTab);

        bus.register(new MessageListener());


    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        switch (tab.getPosition()) {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.tab_mainLayout, hostsListFragment).commit();
                menuDisplay = MENU.MAIN;
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.tab_mainLayout, backupsListFragment).commit();
                menuDisplay = MENU.BACKUP;
                break;
        }
    }

    private void showSavingMessage() {
        clearProgressDialog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String savingDialog = largeFile ? "This may take a while with the large file." : null;
                progressDialog = ProgressDialog.show(MainActivity.this, "Saving", savingDialog, false, false);
            }
        });
    }

    private void showBackingUpMessage() {
        clearProgressDialog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String savingDialog = largeFile ? "This may take a while with the large file." : null;
                progressDialog = ProgressDialog.show(MainActivity.this, "Backing Up...", savingDialog, false, false);
            }
        });
    }


    private void showLoadingMessage() {
        clearProgressDialog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String savingDialog = largeFile ? "This may take a while with the large file." : null;
                progressDialog = ProgressDialog.show(MainActivity.this, "Loading", savingDialog, false, false);
            }
        });
    }

    private void showSearchingMessage() {
        clearProgressDialog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String savingDialog = largeFile ? "This may take a while with the large file." : null;
                progressDialog = ProgressDialog.show(MainActivity.this, "Searching", savingDialog, false, false);
            }
        });
    }
    private void clearProgressDialog() {
        if (null != progressDialog)
            progressDialog.dismiss();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (menuDisplay) {
            case MAIN:
                menu.findItem(R.id.menu_backup).setVisible(false);
                menu.findItem(R.id.menu_delete).setVisible(true);
                menu.findItem(R.id.menu_new_entry).setVisible(true);
                menu.findItem(R.id.menu_search).setVisible(true);
                menu.findItem(R.id.menu_toggle).setVisible(true);
                menu.findItem(R.id.menu_refresh).setVisible(false);
                break;
            case BACKUP:
                menu.findItem(R.id.menu_refresh).setVisible(true);
                menu.findItem(R.id.menu_backup).setVisible(true);
                menu.findItem(R.id.menu_delete).setVisible(false);
                menu.findItem(R.id.menu_new_entry).setVisible(false);
                menu.findItem(R.id.menu_toggle).setVisible(false);
                menu.findItem(R.id.menu_search).setVisible(false);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_entry:
                fragmentManager.beginTransaction().add(R.id.tab_mainLayout, newEntryFragment).addToBackStack(null).show(newEntryFragment).commit();
                break;
            case R.id.menu_delete:
                new DeleteSelectedTasks(hostsListFragment.getItems(), bus).execute(hostsListFragment.getSelectedIndexes());
                break;
            case R.id.menu_toggle:
                new ToggleSelectedTask(hostsListFragment.getItems(), bus).execute(hostsListFragment.getSelectedIndexes());
                break;
            case R.id.menu_search:
                fragmentManager.beginTransaction().add(R.id.tab_mainLayout, searchFragment).addToBackStack(null).show(searchFragment).commit();
                break;
            case R.id.menu_backup:
                showBackingUpMessage();
                new BackupTask().execute(hostsListFragment.getItems());
                break;
            case R.id.menu_refresh:
                backupsListFragment.loadList();
                break;
        }
        return true;
    }

    private class MessageListener implements EventListener {
        @Override
        public void notify(final Event event, final Object value) {
            switch (event) {
                case LOADING_FILE:
                    showLoadingMessage();
                    break;
                case LOADING_FINISHED:
                    clearProgressDialog();
                    break;
                case SAVE_HOST_LIST:
                    showSavingMessage();
                    new SaveHostsTask(bus).execute(hostsListFragment.getItems());
                    break;
                case SAVE_COMPLETE:
                    clearProgressDialog();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hostsListFragment.clearSelectedIndexes();
                            Toast.makeText(MainActivity.this, R.string.save_complete, Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case NEW_ENTRY_CANCEL:
                    fragmentManager.beginTransaction().remove(newEntryFragment).commit();
                    break;
                case SEARCH_CANCEL:
                    fragmentManager.beginTransaction().remove(searchFragment).commit();
                    break;
                case SEARCH_GO:
                    showSearchingMessage();
                    if (value instanceof String)
                        new SearchTask(hostsListFragment.getItems(), bus, hostsListFragment.getListView().getFirstVisiblePosition()+1,true).execute((String)value);
                    fragmentManager.beginTransaction().remove(searchFragment).commit();

                    break;
                case SCROLL_TO_INDEX:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearProgressDialog();
                            hostsListFragment.setSelection((Integer)value);
                        }
                    });
                    break;
                case NOTHING_FOUND:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearProgressDialog();
                                Toast.makeText(MainActivity.this, R.string.search_not_found, Toast.LENGTH_LONG).show();
                            }
                        });
                break;
                case NEW_ENTRY_SAVE:
                    fragmentManager.beginTransaction().remove(newEntryFragment).commit();
                    if (value instanceof HostsEntry) {
                        final HostsEntry entry = (HostsEntry) value;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSavingMessage();
                                hostsListFragment.addEntry(entry);
                            }});
                    }
                    break;
                case EXISTING_ENTRY_SAVE:
                    fragmentManager.beginTransaction().remove(newEntryFragment).commit();
                    bus.fireEvent(Event.SAVE_HOST_LIST);
                    break;
               case FAIL_TO_SAVE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearProgressDialog();
                            Toast.makeText(MainActivity.this, "Error saving file.  Check that you have granted Superuser permissions to Hosts Editor Pro", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case BACKUP_COMPLETE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearProgressDialog();
                            Toast.makeText(MainActivity.this, "Backup Complete", Toast.LENGTH_LONG).show();
                            bus.fireEvent(Event.REFRESH_BACKUPS);
                        }
                    });
                    break;
                case RESTORING_BACKUP:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearProgressDialog();
                            progressDialog = ProgressDialog.show(MainActivity.this, "Restoring", "Restoring Backup File", false, false);
                        }
                    });
                    break;
                case RESTORE_COMPLETE:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearProgressDialog();
                            Toast.makeText(MainActivity.this, "Restore Complete.", Toast.LENGTH_LONG).show();
                            bus.fireEvent(Event.REFRESH_LIST);
                        }
                    });
                    break;
                case DELETING_BACKUP:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearProgressDialog();
                            progressDialog = ProgressDialog.show(MainActivity.this, "Deleting", "Deleting Backup File", false, false);
                        }
                    });
                    break;
                case ERROR:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearProgressDialog();
                            Toast.makeText(MainActivity.this, value.toString() , Toast.LENGTH_LONG).show();
                        }
                    });
                case UPDATE_ENTRY:

                    if(value instanceof HostsEntry)
                        newEntryFragment.setHostEntry((HostsEntry)value);

                    fragmentManager.beginTransaction().add(R.id.tab_mainLayout, newEntryFragment).addToBackStack(null).show(newEntryFragment).commit();
                    break;
            }


        }
    }
}
