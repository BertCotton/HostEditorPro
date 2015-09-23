package com.treb.hosts.pro.file;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.treb.hosts.pro.Constants;
import com.treb.hosts.pro.backup.BackupFile;
import com.treb.hosts.pro.data.HostsEntry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class BackupFileReader {

    public static LinkedList<BackupFile> getBackupFiles() {
        LinkedList<BackupFile> backupFiles = new LinkedList<BackupFile>();
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostsEditor");
        if (dir.isDirectory()) {

            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".hosts");
                }
            };
            String[] children = dir.list(filter);
            for (String file : children) {
                File f = new File(dir.getAbsolutePath() + "/" + file);
                backupFiles.add(new BackupFile(f.getName(), f.getAbsolutePath(), new Date(f.lastModified())));
            }
        }

        return backupFiles;
    }

    public static void importFileFromFile(File file) {
        if (file.exists()) {
            String baseName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostsEditor/"
                    + stripExtension(file.getName());
            File newFile = new File(baseName + ".hosts");
            while (newFile.exists())
                newFile = new File(getNewFileName(baseName) + ".hosts");
            copyFile(file, newFile);
        }

    }

    private static void copyFile(File source, File destination) {
        InputStream in;
        try {
            in = new FileInputStream(source);

            OutputStream out = new FileOutputStream(destination);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception e) {
            Log.e(Constants.LOG_NAME, "Error Copying files: " + e.getMessage());
        }
    }

    public static void importFileFromURI(Uri uri) {
        if (uri != null) {
            String path = uri.toString();
            if (path.toLowerCase().startsWith("file://")) {
                importFileFromFile(new File(uri.getPath()));
            }
        }
    }

    public static void importFileFromString(String filePath) {
        importFileFromFile(new File(filePath));
    }

    private static String getNewFileName(String baseName) {
        Calendar cal = Calendar.getInstance();
        return baseName + "_" + cal.get(Calendar.MILLISECOND);
    }

    public static LinkedList<HostsEntry> loadFromFile(File file) throws ParserConfigurationException, SAXException,
            IOException {
        final LinkedList<HostsEntry> hostEntries = new LinkedList<HostsEntry>();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        DefaultHandler handler = new DefaultHandler() {

            HostsEntry hostsEntry = null;
            LinkedList<String> entryList = new LinkedList<String>();
            boolean inItem = false;
            String tempVal;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                tempVal = "";
                if (qName.equalsIgnoreCase("HostsEntry")) {
                    hostsEntry = new HostsEntry(hostEntries.size() - 1);
                    inItem = true;
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (qName.equalsIgnoreCase("HostsEntry")) {
                    hostsEntry.setHost(entryList);
                    entryList.clear();
                    hostEntries.add(hostsEntry);
                    inItem = false;
                } else if (inItem && qName.equalsIgnoreCase("IpAddress")) {
                    hostsEntry.setIpAddress(tempVal);
                } else if (inItem && qName.equalsIgnoreCase("Host") && tempVal != null) {
                    entryList.add(tempVal);
                }
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                tempVal = new String(ch, start, length);
            }
        };

        saxParser.parse(file, handler);
        return hostEntries;
    }

    private static String stripExtension(String fileName) {
        String[] fileNameArray = fileName.split("[.]");
        if (fileNameArray.length == 1)
            return fileNameArray[0];
        else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileNameArray.length - 1; i++) {
                if (i > 0)
                    sb.append(".");
                sb.append(fileNameArray[i]);
            }
            return sb.toString();
        }
    }

}
