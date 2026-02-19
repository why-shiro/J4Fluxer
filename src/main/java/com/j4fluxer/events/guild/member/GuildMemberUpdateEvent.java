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
 * Fired when a guild member's metadata is updated (e.g., nickname change, role update).
 */
public class GuildMemberUpdateEvent extends Event {
    private final String guildId;
    private final String userId;
    private final String nick;
    private final Member member;

    public GuildMemberUpdateEvent(Fluxer api, JsonNode data) {
        super(api);
        this.guildId = data.get("guild_id").asText();
        this.userId = data.get("user").get("id").asText();
        this.nick = data.has("nick") && !data.get("nick").isNull() ? data.get("nick").asText() : null;

        FluxerImpl core = (FluxerImpl) api;

        Guild guild = api.getGuildById(guildId);
        if (guild == null) {
            guild = new GuildImpl(guildId, core.getRequester());
        }

        JsonNode userNode = data.get("user");
        this.member = new MemberImpl(new UserImpl(userNode, core.getRequester()), data, guild, core.getRequester());
    }

    /** @return The ID of the guild. */
    public String getGuildId() { return guildId; }

    /** @return The ID of the member. */
    public String getUserId() { return userId; }

    /** @return The new nickname, or {@code null} if no nickname is set. */
    public String getNick() { return nick; }

    /**
     * Retrieves the Guild where the update happened.
     * @return The {@link Guild} object.
     */
    public Guild getGuild() {
        return api.getGuildById(guildId);
    }

    /**
     * Retrieves the Member who was updated.
     * @return The {@link Member} object.
     */
    public Member getMember() {
        return member;
    }
}