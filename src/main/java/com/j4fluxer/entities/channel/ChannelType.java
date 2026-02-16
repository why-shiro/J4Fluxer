package com.j4fluxer.entities.channel;

public enum ChannelType {
    TEXT(0),
    DM(1),
    VOICE(2),
    GROUP_DM(3),
    CATEGORY(4),
    UNKNOWN(-1);

    private final int key;

    ChannelType(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public static ChannelType fromKey(int key) {
        for (ChannelType type : values()) {
            if (type.key == key) return type;
        }
        return UNKNOWN;
    }
}