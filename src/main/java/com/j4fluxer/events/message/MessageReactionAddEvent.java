package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.member.MemberImpl;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.entities.user.UserImpl;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a user adds a reaction emoji to a message.
 */
public class MessageReactionAddEvent extends Event {
    private final String userId;
    private final String channelId;
    private final String messageId;
    private final String guildId;
    private final String emojiName;
    private final Member member;

    /**
     * Constructs a new MessageReactionAddEvent.
     *
     * @param api  The API instance.
     * @param data The JSON payload from the gateway.
     */
    public MessageReactionAddEvent(Fluxer api, JsonNode data) {
        super(api);
        this.userId = data.get("user_id").asText();
        this.channelId = data.get("channel_id").asText();
        this.messageId = data.get("message_id").asText();
        this.emojiName = data.get("emoji").get("name").asText();
        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;

        if (data.has("member")) {
            JsonNode memberNode = data.get("member");
            User user = new UserImpl(memberNode.get("user"));
            this.member = new MemberImpl(user, memberNode);
        } else {
            this.member = null;
        }
    }

    /**
     * Returns the ID of the user who reacted.
     * @return The user ID.
     */
    public String getUserId() { return userId; }

    /**
     * Returns the name of the emoji used (e.g., "🎉").
     * @return The emoji name.
     */
    public String getEmojiName() { return emojiName; }

    /**
     * Returns the ID of the message that was reacted to.
     * @return The message ID.
     */
    public String getMessageId() { return messageId; }

    /**
     * Retrieves the member who added the reaction.
     *
     * @return The {@link Member}, or {@code null} if not in a guild.
     */
    public Member getMember() { return member; }

    /**
     * Retrieves the user who added the reaction.
     *
     * @return The {@link User} object.
     */
    public User getUser() {
        if (member != null) return member.getUser();
        // Future implementation: Fetch user from cache/API if member is null
        return null;
    }

    /**
     * Retrieves the text channel where the reaction happened.
     *
     * @return The {@link TextChannel}, or {@code null} if not found.
     */
    public TextChannel getChannel() {
        if (guildId != null) {
            Guild guild = api.getGuildById(guildId);
            if (guild != null) return guild.getTextChannelById(channelId);
        }
        return null;
    }

    /**
     * Retrieves the guild where the reaction happened.
     *
     * @return The {@link Guild}, or {@code null} if in DM.
     */
    public Guild getGuild() {
        return guildId != null ? api.getGuildById(guildId) : null;
    }
}