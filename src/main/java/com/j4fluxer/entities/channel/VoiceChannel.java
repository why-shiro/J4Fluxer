package com.j4fluxer.entities.channel;

/**
 * Represents a voice channel where users can connect and speak.
 */
public interface VoiceChannel extends GuildChannel {
    /**
     * @return The bitrate (in bits) used by this voice channel.
     */
    int getBitrate();

    /**
     * @return The maximum number of users allowed in this channel, or 0 for unlimited.
     */
    int getUserLimit();

    /**
     * Attempts to connect the bot to this voice channel.
     */
    void connect();
}