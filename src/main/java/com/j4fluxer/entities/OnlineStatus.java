package com.j4fluxer.entities;

public enum OnlineStatus {
    ONLINE("online"),
    IDLE("idle"),
    DND("dnd"),
    INVISIBLE("invisible"),
    OFFLINE("offline");

    private final String key;

    OnlineStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}