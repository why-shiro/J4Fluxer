package com.j4fluxer.events.guild.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a new user joins a guild.
 */
public class GuildMemberJoinEvent extends Event {
    private final String guildId;
    private final String userId;
    private final String username;

    public GuildMemberJoinEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.userId = data.get("user").get("id").asText();
        this.username = data.get("user").get("username").asText();
    }

    /** @return The ID of the guild the user joined. */
    public String getGuildId() { return guildId; }

    /** @return The ID of the user who joined. */
    public String getUserId() { return userId; }

    /** @return The username of the user who joined. */
    public String getUsername() { return username; }
}