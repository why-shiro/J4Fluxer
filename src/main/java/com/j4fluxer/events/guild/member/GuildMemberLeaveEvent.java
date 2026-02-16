package com.j4fluxer.events.guild.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a user leaves a guild, is kicked, or is banned.
 */
public class GuildMemberLeaveEvent extends Event {
    private final String guildId;
    private final String userId;

    public GuildMemberLeaveEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.userId = data.get("user").get("id").asText();
    }

    /** @return The ID of the guild the user left. */
    public String getGuildId() { return guildId; }

    /** @return The ID of the user who left. */
    public String getUserId() { return userId; }
}