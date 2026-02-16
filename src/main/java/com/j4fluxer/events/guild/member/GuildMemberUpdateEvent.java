package com.j4fluxer.events.guild.member;
import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

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
    public String getNick() { return nick; }
}