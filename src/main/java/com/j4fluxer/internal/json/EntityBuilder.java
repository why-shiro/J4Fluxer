package com.j4fluxer.internal.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.*;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;

public class EntityBuilder {
    private final Requester requester;

    public EntityBuilder(Requester requester) {
        this.requester = requester;
    }

    public Channel createChannel(JsonNode json, Guild guild) {
        int typeId = json.has("type") ? json.get("type").asInt() : -1;
        ChannelType type = ChannelType.fromKey(typeId);

        switch (type) {
            case TEXT:
                return new TextChannelImpl(json, guild, requester);
            case VOICE:
                return new VoiceChannelImpl(json, guild, requester);
            case CATEGORY:
                return new CategoryImpl(json, guild, requester);
            default:
                System.err.println("Unknown category: " + typeId);
                return null;
        }
    }
}