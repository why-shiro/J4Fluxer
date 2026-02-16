package com.j4fluxer.events.guild;
import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class RoleDeleteEvent extends Event {
    private final String guildId;
    private final String roleId;
    public RoleDeleteEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.roleId = data.get("role_id").asText();
    }
    public String getRoleId() { return roleId; }
}