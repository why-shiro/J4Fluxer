package com.j4fluxer.entities;

/**
 * Represents the various presence states a user or bot can have on the Fluxer platform.
 * <p>These statuses determine how the user's availability is displayed to others
 * and how the Fluxer Gateway handles notifications for that session.</p>
 */
public enum OnlineStatus {

    /** The user is active and connected to Fluxer. */
    ONLINE("online"),

    /** The user is connected but has been inactive for a period of time. */
    IDLE("idle"),

    /**
     * The user is in "Do Not Disturb" mode.
     * Typically, this state mutes client-side notifications.
     */
    DND("dnd"),

    /**
     * The user appears as offline to others but maintains a full connection
     * to the Fluxer platform.
     */
    INVISIBLE("invisible"),

    /** The user is not connected to the Fluxer Gateway. */
    OFFLINE("offline");

    /** The internal string key used for communication with the Fluxer Gateway. */
    private final String key;

    /**
     * Internal constructor for {@code OnlineStatus}.
     *
     * @param key The status key string.
     */
    OnlineStatus(String key) {
        this.key = key;
    }

    /**
     * Returns the raw string key associated with this status.
     * <p>This key is used in the presence update payloads sent to the Fluxer Gateway.</p>
     *
     * @return The status key as a {@link String}.
     */
    public String getKey() {
        return key;
    }
}