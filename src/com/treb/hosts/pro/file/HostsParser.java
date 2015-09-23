package com.treb.hosts.pro.file;

import com.treb.hosts.pro.data.HostsEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * @author bert
 */
public class HostsParser {

    private static final Pattern ValidEntryPattern = Pattern.compile("^#*[0-9]+.*");
    private static final Pattern LocalHostPattern = Pattern.compile(".*127\\.0\\.0\\.1.*");

    public static LinkedList<HostsEntry> parseHostFile(boolean ignoreLocalHostEntries, boolean ignoreComments)
            throws UnableToMountSystemException, IOException {

        FileReader fReader = null;
        BufferedReader br = null;
        try {
            fReader = new FileReader("/system/etc/hosts");
            br = new BufferedReader(fReader);
            String line = null;
            int count = 0;

            LinkedList<HostsEntry> hostEntries = new LinkedList<HostsEntry>();
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0)
                    continue;
                String[] hostEntry = line.split(" ");
                if (hostEntry == null || hostEntry.length == 0) {
                    continue;
                }
                String firstEntry = hostEntry[0].trim();
                // this could be a comment

                if (ignoreLocalHostEntries && LocalHostPattern.matcher(firstEntry).matches())
                    continue;
                boolean isCommentOnly = !ValidEntryPattern.matcher(firstEntry).matches();

                if (ignoreComments && isCommentOnly)
                    continue;

                HostsEntry newEntry = new HostsEntry(count++);
                if (isCommentOnly) {
                    newEntry.setComments(line);
                } else {
                    newEntry.setIpAddress(firstEntry);
                    if (hostEntry.length > 1) {
                        for (int i = 1; i < hostEntry.length; i++) {
                            String entry = hostEntry[i];
                            if (entry != null && entry.length() > 0)
                                newEntry.addHost(entry);
                        }
                    }
                }
                hostEntries.add(newEntry);
            }
            return hostEntries;
        } finally {
            br.close();
            fReader.close();
        }

    }
}
