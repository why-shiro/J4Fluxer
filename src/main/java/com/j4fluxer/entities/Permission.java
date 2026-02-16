package com.j4fluxer.entities;

import java.util.EnumSet;

public enum Permission {
    // ==========================================
    // General
    // ==========================================
    CREATE_INSTANT_INVITE(1L << 0),
    MANAGE_CHANNELS(1L << 4),
    ADD_REACTIONS(1L << 6),
    VIEW_CHANNEL(1L << 10),

    // ==========================================
    // Text
    // ==========================================
    SEND_MESSAGES(1L << 11),
    SEND_TTS_MESSAGES(1L << 12),
    MANAGE_MESSAGES(1L << 13),
    EMBED_LINKS(1L << 14),
    ATTACH_FILES(1L << 15),
    READ_MESSAGE_HISTORY(1L << 16),
    MENTION_EVERYONE(1L << 17),
    USE_EXTERNAL_EMOJIS(1L << 18),
    USE_EXTERNAL_STICKERS(1L << 37),

    // ==========================================
    // 3. Voice
    // ==========================================
    PRIORITY_SPEAKER(1L << 8),
    STREAM(1L << 9),
    CONNECT(1L << 20),
    SPEAK(1L << 21),
    MUTE_MEMBERS(1L << 22),
    DEAFEN_MEMBERS(1L << 23),
    MOVE_MEMBERS(1L << 24),
    USE_VAD(1L << 25),
    REQUEST_TO_SPEAK(1L << 32),

    // ==========================================
    // 4. Management
    // ==========================================
    KICK_MEMBERS(1L << 1),
    BAN_MEMBERS(1L << 2),
    ADMINISTRATOR(1L << 3),
    MANAGE_GUILD(1L << 5),
    VIEW_AUDIT_LOG(1L << 7),
    MANAGE_ROLES(1L << 28),
    MANAGE_WEBHOOKS(1L << 29),
    MANAGE_EMOJIS_AND_STICKERS(1L << 30),

    UNKNOWN(-1);

    private final long rawValue;

    Permission(long rawValue) {
        this.rawValue = rawValue;
    }

    public long getRawValue() {
        return rawValue;
    }


    public static EnumSet<Permission> getPermissions(long bitfield) {
        EnumSet<Permission> perms = EnumSet.noneOf(Permission.class);
        for (Permission p : Permission.values()) {
            if (p != UNKNOWN && (bitfield & p.rawValue) == p.rawValue) {
                perms.add(p);
            }
        }
        return perms;
    }

    public static long getRaw(EnumSet<Permission> permissions) {
        long raw = 0;
        for (Permission p : permissions) {
            if (p != UNKNOWN) raw |= p.rawValue;
        }
        return raw;
    }
}