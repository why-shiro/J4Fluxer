package com.j4fluxer.entities.channel;

public interface VoiceChannel extends GuildChannel {
    int getBitrate();
    int getUserLimit();

    void connect();
}