package com.treb.hosts.pro.data;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author bert
 */
public class HostsEntry implements Comparable<HostsEntry>, Serializable {
    private Integer entryId;
    private String comments;
    private String ipAddress;
    private String hostString;
    private LinkedList<String> hosts;
    private boolean selected;

    public HostsEntry() {
        this(null);
    }

    public HostsEntry(Integer entryId) {
        this.ipAddress = "";
        this.entryId = entryId;
        hosts = new LinkedList<String>();
        hostString = "";
    }

    public void addHost(String host) {
        this.hosts.add(host);
        hostString += host + "\n";
    }

    public void setHost(LinkedList<String> entryList) {
        this.hosts.clear();
        this.hosts.addAll(entryList);
        this.hostString = "";
    }

    public void clearHosts() {
        hosts.clear();
        hostString = "";
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LinkedList<String> getHosts() {
        return hosts;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getHostString() {
        return hostString;

    }

    public void toggle() {
        if (ipAddress.startsWith("#"))
            ipAddress = ipAddress.replace("#", "");
        else
            this.ipAddress = "#" + ipAddress;

    }

    public String getComment() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isCommentOnly() {
        return comments != null && comments.length() > 0;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    @Override
    public int compareTo(HostsEntry other) {
        return entryId.compareTo(other.entryId);
    }

    @Override
    public int hashCode() {
        return entryId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        HostsEntry that = (HostsEntry) o;

        return this.entryId == that.entryId;
    }

    public String toHostsEntryLine() {
        StringBuilder sb = new StringBuilder();
        if (null != ipAddress)
            sb.append(ipAddress).append(" ");
        if (null != hosts) {
            for (String host : hosts)
                sb.append(host).append(" ");
        }
        if (null != comments)
            sb.append(comments).append(" ");
        return sb.toString();
    }

    @Override
    public String toString() {
        return ipAddress + " - " + hostString;
    }


}
