package com.syncpeer.syncpeerapp.videocall.webrtc.mediators;

import java.util.ArrayList;
import java.util.List;

public class RenegotiationManager {
    Boolean isRenegotiationNeeded;
    List<RenegotiationMediator> registeredComponents;

    public RenegotiationManager() {
        registeredComponents = new ArrayList<>();
    }

    public void setRenegotiationStatus(Boolean status) {
        this.isRenegotiationNeeded = status;
        notifyClasses();
    }

    public void registerComponent(RenegotiationMediator component) {
        this.registeredComponents.add(component);
    }

    public Boolean getRenegotiationNeeded() {
        return isRenegotiationNeeded;
    }

    private synchronized void notifyClasses() {
        for (RenegotiationMediator component : registeredComponents) {
            component.updateStatus(this.isRenegotiationNeeded);
        }
    }

}
