package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.PermissionOverwrite;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;

import java.util.Collections;
import java.util.List;

public class CategoryImpl extends AbstractChannel implements Category {

    public CategoryImpl(JsonNode json, Guild guild, Requester requester) {
        super(json, guild, requester);
    }

    @Override
    public ChannelType getType() { return ChannelType.CATEGORY; }

    @Override
    public List<GuildChannel> getChannels() {
        return Collections.emptyList();
    }

    @Override
    public List<PermissionOverwrite> getPermissionOverwrites() {
        return List.of();
    }
}