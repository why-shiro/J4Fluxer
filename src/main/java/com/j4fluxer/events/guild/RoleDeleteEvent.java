package com.j4fluxer.events.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a role is deleted from a guild.
 */
public class RoleDeleteEvent extends Event {
    private final String guildId;
    private final String roleId;

    public RoleDeleteEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.roleId = data.get("role_id").asText();
    }

    /** @return The ID of the guild where the role was deleted. */
    public String getGuildId() { return guildId; }

    /** @return The ID of the role that was removed. */
    public String getRoleId() { return roleId; }
}