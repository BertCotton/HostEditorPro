package com.treb.hosts.pro.file;

import android.util.Log;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.data.HostsEntry;
import com.treb.hosts.pro.events.Event;
import com.treb.hosts.pro.events.EventBus;

import java.io.DataOutputStream;
import java.util.LinkedList;

/**
 * @author bert
 */
public class HostWriter {

    private static final EventBus bus = EventBus.getInstance();

    public static Boolean save(LinkedList<HostsEntry> hostEntries)
            throws UnableToMountSystemException {
        try {
	    java.lang.StringBuilder hostsBuilder = new java.lang.StringBuilder();
	    for (HostsEntry hostEntry : hostEntries ) {
		hostsBuilder.append(hostEntry.toHostsEntryLine()).append("\n");
	    }
            String[] mountLocation = SystemMount.getMountLocation();

            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("mount -o rw,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");
            os.writeBytes("echo '' > /system/etc/hosts\n");
	    os.writeBytes("echo '" + hostsBuilder.toString() + "' >> /system/etc/hosts\n");
        
            os.writeBytes("mount -o ro,remount -t " + mountLocation[1] + " " + mountLocation[0] + " /system\n");
            os.writeBytes("exit\n");
            os.flush();

            p.waitFor();

            if (p.exitValue() != 255)
                return Boolean.TRUE;

        } catch (Exception ex) {
            Log.e(Constants.LOG_NAME, ex.getMessage(), ex);
            bus.fireEvent(Event.FAIL_TO_SAVE);
        }

        return Boolean.FALSE;

    }
}
