package com.j4fluxer.events.guild.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.member.MemberImpl;
import com.j4fluxer.entities.user.UserImpl;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

/**
 * Fired when a new user joins a guild.
 */
public class GuildMemberJoinEvent extends Event {
    private final String guildId;
    private final String userId;
    private final String username;
    private final Member member;

    public GuildMemberJoinEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();

        JsonNode userNode = data.get("user");
        this.userId = userNode.get("id").asText();
        this.username = userNode.get("username").asText();

        FluxerImpl core = (FluxerImpl) api;

        Guild guild = api.getGuildById(guildId);
        if (guild == null) {
            guild = new GuildImpl(guildId, core.getRequester());
        }

        this.member = new MemberImpl(new UserImpl(userNode, core.getRequester()), data, guild, core.getRequester());
    }

    /** @return The ID of the guild the user joined. */
    public String getGuildId() { return guildId; }

    /** @return The ID of the user who joined. */
    public String getUserId() { return userId; }

    /** @return The username of the user who joined. */
    public String getUsername() { return username; }

    /**
     * Retrieves the member object for the user who joined.
     * @return The {@link Member} object.
     */
    public Member getMember() { return member; }

    /** @return The {@link Guild} the user joined. */
    public Guild getGuild() {
        return api.getGuildById(guildId);
    }
}