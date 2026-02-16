package com.j4fluxer.entities;

public enum OnlineStatus {
    ONLINE("online"),       // Yeşil
    IDLE("idle"),           // Sarı (Hilal)
    DND("dnd"),             // Kırmızı (Rahatsız Etmeyin)
    INVISIBLE("invisible"), // Gri (Görünmez)
    OFFLINE("offline");

    private final String key;

    OnlineStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}