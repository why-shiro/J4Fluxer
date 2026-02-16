package com.j4fluxer.events.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class GuildLeaveEvent extends Event {
    private final String guildId;

    public GuildLeaveEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("id").asText();
    }

    public String getGuildId() { return guildId; }
}