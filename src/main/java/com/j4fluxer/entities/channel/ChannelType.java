package com.j4fluxer.entities.channel;

/**
 * Represents the various types of channels available within the Fluxer platform.
 * <p>Each type is associated with a unique integer key used by the Fluxer API
 * to identify the channel's purpose and functionality.</p>
 */
public enum ChannelType {

    /** A standard text-based channel within a guild. */
    TEXT(0),

    /** A direct message channel between two users. */
    DM(1),

    /** A voice and video communication channel within a guild. */
    VOICE(2),

    /** A private message channel involving multiple users. */
    GROUP_DM(3),

    /** An organizational category used to group other guild channels. */
    CATEGORY(4),

    /** A placeholder for channel types that are not yet supported or recognized by the library. */
    UNKNOWN(-1);

    /** The raw integer value representing the channel type in the Fluxer API. */
    private final int key;

    /**
     * Internal constructor for {@code ChannelType}.
     *
     * @param key The integer key associated with the type.
     */
    ChannelType(int key) {
        this.key = key;
    }

    /**
     * Returns the raw integer key for this channel type.
     *
     * @return The API key as an {@code int}.
     */
    public int getKey() {
        return key;
    }

    /**
     * Resolves a {@code ChannelType} from its associated integer key.
     *
     * @param key The raw key received from the Fluxer API.
     * @return The matching {@link ChannelType}, or {@link #UNKNOWN} if the key is not recognized.
     */
    public static ChannelType fromKey(int key) {
        for (ChannelType type : values()) {
            if (type.key == key) return type;
        }
        return UNKNOWN;
    }
}