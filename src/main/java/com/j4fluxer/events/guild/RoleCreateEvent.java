package com.j4fluxer.events.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a new role is created in a guild.
 */
public class RoleCreateEvent extends Event {
    private final String guildId;
    private final String roleName;

    public RoleCreateEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.roleName = data.get("role").get("name").asText();
    }

    /** @return The ID of the guild where the role was created. */
    public String getGuildId() { return guildId; }

    /** @return The name of the newly created role. */
    public String getRoleName() { return roleName; }
}