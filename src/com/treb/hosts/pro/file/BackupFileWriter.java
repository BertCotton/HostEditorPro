package com.treb.hosts.pro.file;

import android.os.Environment;

import com.treb.hosts.pro.data.HostsEntry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class BackupFileWriter {

    public static void saveFile(LinkedList<HostsEntry> entries) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String dateFormat = (cal.get(Calendar.MONTH) + 1) + "" + cal.get(Calendar.DAY_OF_MONTH) + ""
                + cal.get(Calendar.YEAR);
        try {
            File baseDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostsEditor");
            if (!baseDir.exists())
                baseDir.mkdirs();

            String baseFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hostsEditor/" + "/hosts_"
                    + dateFormat;
            File file = new File(baseFileName + ".hosts");
            if (file.exists()) {
                do {
                    file = new File(getNewFileName(baseFileName) + ".hosts");
                } while (file.exists());
            }
            file.createNewFile();

            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootNode = doc.createElement("HostsEntries");
            doc.appendChild(rootNode);

            for (HostsEntry hostEntry : entries) {
                Element hostEntryNode = doc.createElement("HostsEntry");
                Element addressNode = doc.createElement("IpAddress");
                addressNode.setTextContent(hostEntry.getIpAddress());
                hostEntryNode.appendChild(addressNode);

                Element entryNodes = doc.createElement("Hosts");
                for (String entry : hostEntry.getHosts()) {
                    Element entryNode = doc.createElement("Host");
                    entryNode.setTextContent(entry);
                    entryNodes.appendChild(entryNode);
                }
                hostEntryNode.appendChild(entryNodes);
                rootNode.appendChild(hostEntryNode);
            }
            // set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            // create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(sw.toString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static String getNewFileName(String baseName) {
        Calendar cal = Calendar.getInstance();
        return baseName + "_" + cal.get(Calendar.MILLISECOND);
    }
}
