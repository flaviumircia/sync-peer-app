package com.syncpeer.syncpeerapp.videocall.webrtc.mediators;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DestinationEmailMediator {
    private final List<Component> registeredComponents; // List of registered components
    private String destinationEmailShared;

    public DestinationEmailMediator() {
        registeredComponents = new ArrayList<>();
    }

    public void registerComponent(Component component) {
        registeredComponents.add(component);
    }

    public String getDestinationEmailShared() {
        return this.destinationEmailShared;
    }

    public void setDestinationEmailShared(String value) {
        this.destinationEmailShared = value;
        notifyClasses();
    }

    private synchronized void notifyClasses() {
        for (Component component : registeredComponents) {
            component.update(destinationEmailShared);
            Log.d("Component" + component.getClass(), "notifyClasses: Update destinationEmail to: " + destinationEmailShared);
        }
    }

}
