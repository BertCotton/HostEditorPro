package com.treb.hosts.pro;

public class HostsListState {
    private boolean showComments;
    private boolean showLocalhostEntries;

    public boolean isShowComments() {
        return showComments;
    }

    public void setShowComments(boolean showComments) {
        this.showComments = showComments;
    }

    public boolean isShowLocalhostEntries() {
        return showLocalhostEntries;
    }

    public void setShowLocalhostEntries(boolean showLocalhostEntries) {
        this.showLocalhostEntries = showLocalhostEntries;
    }
}
