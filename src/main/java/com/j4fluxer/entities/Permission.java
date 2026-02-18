package com.j4fluxer.entities;

import java.util.EnumSet;

/**
 * Represents the access and action rights available on the Fluxer platform.
 *
 * <p>Permissions are managed as a bitmask (bitfield), where each permission corresponds
 * to a specific bit in a {@code long} value. These are used to determine what actions
 * a user or role can perform within a Guild or a specific Channel.</p>
 */
public enum Permission {

    // ==========================================
    // General Permissions
    // ==========================================

    /** Allows creation of instant invites. */
    CREATE_INSTANT_INVITE(1L << 0),

    /** Allows management and editing of channels. */
    MANAGE_CHANNELS(1L << 4),

    /** Allows users to add new reactions to messages. */
    ADD_REACTIONS(1L << 6),

    /** Allows viewing of a channel, which includes reading messages in text channels. */
    VIEW_CHANNEL(1L << 10),

    // ==========================================
    // Text Permissions
    // ==========================================

    /** Allows for sending messages in a channel. */
    SEND_MESSAGES(1L << 11),

    /** Allows for sending of text-to-speech messages. */
    SEND_TTS_MESSAGES(1L << 12),

    /** Allows for deletion of other users' messages or pinning messages. */
    MANAGE_MESSAGES(1L << 13),

    /** Links sent by users with this permission will be auto-embedded. */
    EMBED_LINKS(1L << 14),

    /** Allows for uploading images or files. */
    ATTACH_FILES(1L << 15),

    /** Allows for reading of a channel's message history. */
    READ_MESSAGE_HISTORY(1L << 16),

    /** Allows for using the @everyone tag to notify all users in a channel. */
    MENTION_EVERYONE(1L << 17),

    /** Allows the usage of emojis from other guilds. */
    USE_EXTERNAL_EMOJIS(1L << 18),

    /** Allows the usage of stickers from other guilds. */
    USE_EXTERNAL_STICKERS(1L << 37),

    // ==========================================
    // Voice Permissions
    // ==========================================

    /** Allows a user to be heard more easily than others. */
    PRIORITY_SPEAKER(1L << 8),

    /** Allows the user to go live (stream). */
    STREAM(1L << 9),

    /** Allows for joining of a voice channel. */
    CONNECT(1L << 20),

    /** Allows for speaking in a voice channel. */
    SPEAK(1L << 21),

    /** Allows for muting other members in a voice channel. */
    MUTE_MEMBERS(1L << 22),

    /** Allows for deafening other members in a voice channel. */
    DEAFEN_MEMBERS(1L << 23),

    /** Allows for moving members between voice channels. */
    MOVE_MEMBERS(1L << 24),

    /** Allows for using voice-activity-detection (VAD) instead of push-to-talk. */
    USE_VAD(1L << 25),

    /** Allows for requesting the floor in stage-like channels. */
    REQUEST_TO_SPEAK(1L << 32),

    // ==========================================
    // Management & Moderation Permissions
    // ==========================================

    /** Allows for kicking members from the guild. */
    KICK_MEMBERS(1L << 1),

    /** Allows for banning members from the guild. */
    BAN_MEMBERS(1L << 2),

    /**
     * Grants all permissions and bypasses every channel permission overwrite.
     * <p><b>Extreme caution:</b> This is a highly dangerous permission to grant.</p>
     */
    ADMINISTRATOR(1L << 3),

    /** Allows management and editing of the guild. */
    MANAGE_GUILD(1L << 5),

    /** Allows for viewing the guild's audit logs. */
    VIEW_AUDIT_LOG(1L << 7),

    /** Allows for creation, editing, and deletion of roles. */
    MANAGE_ROLES(1L << 28),

    /** Allows for management and editing of webhooks. */
    MANAGE_WEBHOOKS(1L << 29),

    /** Allows for management and editing of custom emojis and stickers. */
    MANAGE_EMOJIS_AND_STICKERS(1L << 30),

    /** A placeholder for permissions that are not yet recognized by the library. */
    UNKNOWN(-1);

    /** The raw bitmask value of the permission. */
    private final long rawValue;

    /**
     * Internal constructor for {@code Permission}.
     *
     * @param rawValue The bitmask value.
     */
    Permission(long rawValue) {
        this.rawValue = rawValue;
    }

    /**
     * Returns the raw bitmask value representing this permission.
     *
     * @return The permission value as a {@code long}.
     */
    public long getRawValue() {
        return rawValue;
    }

    /**
     * Converts a raw bitfield (bitmask) from the Fluxer API into a set of {@code Permission} constants.
     *
     * @param bitfield The raw numeric value received from Fluxer.
     * @return An {@link EnumSet} containing all permissions present in the bitfield.
     */
    public static EnumSet<Permission> getPermissions(long bitfield) {
        EnumSet<Permission> perms = EnumSet.noneOf(Permission.class);
        for (Permission p : Permission.values()) {
            if (p != UNKNOWN && (bitfield & p.rawValue) == p.rawValue) {
                perms.add(p);
            }
        }
        return perms;
    }

    /**
     * Compiles a set of {@code Permission} constants into a single raw long bitmask.
     * <p>This is useful for sending permission updates back to the Fluxer API.</p>
     *
     * @param permissions The set of permissions to compile.
     * @return The resulting raw bitmask value.
     */
    public static long getRaw(EnumSet<Permission> permissions) {
        long raw = 0;
        for (Permission p : permissions) {
            if (p != UNKNOWN) raw |= p.rawValue;
        }
        return raw;
    }
}