package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.PermissionOverwrite;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;

import java.util.List;

public class VoiceChannelImpl extends AbstractChannel implements VoiceChannel {

    public VoiceChannelImpl(JsonNode json, Guild guild, Requester requester) {
        super(json, guild, requester);
    }

    @Override
    public ChannelType getType() { return ChannelType.VOICE; }

    @Override
    public int getBitrate() { return json.has("bitrate") ? json.get("bitrate").asInt() : 64000; }

    @Override
    public int getUserLimit() { return json.has("user_limit") ? json.get("user_limit").asInt() : 0; }

    @Override
    public void connect() {
        System.out.println("Ses bağlantısı henüz implemente edilmedi (Gateway lazım).");
    }

    @Override
    public List<PermissionOverwrite> getPermissionOverwrites() {
        return List.of();
    }
}