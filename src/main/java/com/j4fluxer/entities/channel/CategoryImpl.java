package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;

import java.util.Collections;
import java.util.List;

public class CategoryImpl extends AbstractChannel implements Category {

    public CategoryImpl(JsonNode json, Guild guild, Requester requester) {
        super(json, guild, requester);
    }

    public CategoryImpl(String id, Guild guild, Requester requester) {
        super(id, guild, requester);
    }

    @Override
    public ChannelType getType() {
        return ChannelType.CATEGORY;
    }

    @Override
    public List<GuildChannel> getChannels() {
        return Collections.emptyList();
    }
    @Override
    public RestAction<TextChannel> createTextChannel(String name) {
        return getGuild().createTextChannel(name, this.id);
    }

    @Override
    public RestAction<VoiceChannel> createVoiceChannel(String name) {
        return getGuild().createVoiceChannel(name, this.id);
    }

}