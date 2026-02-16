package com.j4fluxer.events.guild.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a guild member's metadata is updated (e.g., nickname change).
 */
public class GuildMemberUpdateEvent extends Event {
    private final String guildId;
    private final String userId;
    private final String nick;

    public GuildMemberUpdateEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.userId = data.get("user").get("id").asText();
        this.nick = data.has("nick") && !data.get("nick").isNull() ? data.get("nick").asText() : null;
    }

    /** @return The ID of the guild. */
    public String getGuildId() { return guildId; }

    /** @return The ID of the member. */
    public String getUserId() { return userId; }

    /** @return The new nickname, or {@code null} if no nickname is set. */
    public String getNick() { return nick; }
}