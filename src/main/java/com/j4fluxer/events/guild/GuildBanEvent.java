package com.j4fluxer.events.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

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

    public String getGuildId() { return guildId; }
    public String getUserId() { return userId; }
    public boolean isBanned() { return banned; }
}