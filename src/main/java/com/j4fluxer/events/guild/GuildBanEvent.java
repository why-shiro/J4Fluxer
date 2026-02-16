package com.j4fluxer.events.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a user is either banned or unbanned from a guild.
 */
public class GuildBanEvent extends Event {
    private final String guildId;
    private final String userId;
    private final boolean banned;

    public GuildBanEvent(Fluxer api, JsonNode data, boolean banned) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.userId = data.get("user").get("id").asText();
        this.banned = banned;
    }

    /** @return The ID of the guild where the ban action took place. */
    public String getGuildId() { return guildId; }

    /** @return The ID of the user who was banned/unbanned. */
    public String getUserId() { return userId; }

    /** @return {@code true} if the user was banned, {@code false} if unbanned. */
    public boolean isBanned() { return banned; }
}