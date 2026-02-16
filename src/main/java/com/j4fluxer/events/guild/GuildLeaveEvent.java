package com.j4fluxer.events.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when the bot leaves a guild, is kicked, or the guild is deleted.
 */
public class GuildLeaveEvent extends Event {
    private final String guildId;

    public GuildLeaveEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("id").asText();
    }

    /** @return The ID of the guild the bot left. */
    public String getGuildId() { return guildId; }
}