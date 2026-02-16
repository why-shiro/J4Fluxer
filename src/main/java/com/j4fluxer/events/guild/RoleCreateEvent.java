package com.j4fluxer.events.guild;
import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class RoleCreateEvent extends Event {
    private final String guildId;
    private final String roleName;
    public RoleCreateEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.roleName = data.get("role").get("name").asText();
    }
    public String getRoleName() { return roleName; }
}