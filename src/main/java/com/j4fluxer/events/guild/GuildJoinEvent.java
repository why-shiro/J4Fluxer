package com.j4fluxer.events.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

public class GuildJoinEvent extends Event {
    private final Guild guild;

    public GuildJoinEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guild = new GuildImpl(data, ((FluxerImpl)api).getRequester());
    }

    public Guild getGuild() { return guild; }
}