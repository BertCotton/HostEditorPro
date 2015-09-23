package com.treb.hosts.pro.events;

import android.os.AsyncTask;

import java.util.LinkedList;

public class EventBus {
    private final LinkedList<EventListener> listeners;

    private static EventBus instance;

    private EventBus() {
        listeners = new LinkedList<EventListener>();
    }

    public synchronized static EventBus getInstance() {
        if (null == instance)
            instance = new EventBus();
        return instance;
    }

    public void register(EventListener listener) {
        listeners.add(listener);
    }

    public void fireEvent(Event event) {
        fireEvent(event, null);
    }

    public void fireEvent(final Event event, final Object value) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                for (EventListener listener : listeners)
                    listener.notify(event, value);
            }
        });
    }
}
