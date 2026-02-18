package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.member.MemberImpl;
import com.j4fluxer.entities.user.UserImpl;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Represents an event that is triggered when a user removes a reaction from a message.
 * This event provides information about the user, the message, and the emoji that was removed.
 */
public class MessageReactionRemoveEvent extends Event {

    /** The ID of the user who removed the reaction. */
    private final String userId;

    /** The ID of the channel where the reaction was removed. */
    private final String channelId;

    /** The ID of the message from which the reaction was removed. */
    private final String messageId;

    /** The ID of the guild where the reaction was removed. */
    private final String guildId;

    /** The name of the emoji that was removed. */
    private final String emojiName;

    /** The member object of the user who removed the reaction, if available. */
    private final Member member;

    /**
     * Constructs a new {@code MessageReactionRemoveEvent}.
     *
     * @param api  The {@link Fluxer} API instance.
     * @param data The JSON data received from the gateway containing the reaction removal details.
     */
    public MessageReactionRemoveEvent(Fluxer api, JsonNode data) {
        super(api);
        this.userId = data.get("user_id").asText();
        this.channelId = data.get("channel_id").asText();
        this.messageId = data.get("message_id").asText();
        this.emojiName = data.get("emoji").get("name").asText();

        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;

        if (data.has("member")) {
            JsonNode memberNode = data.get("member");
            UserImpl user = new UserImpl(memberNode.get("user"));
            this.member = new MemberImpl(user, memberNode);
        } else {
            this.member = null;
        }
    }

    /**
     * Returns the unique ID of the user who removed the reaction.
     *
     * @return The user ID as a {@link String}.
     */
    public String getUserId() { return userId; }

    /**
     * Returns the ID of the channel where this event occurred.
     *
     * @return The channel ID as a {@link String}.
     */
    public String getChannelId() { return channelId; }

    /**
     * Returns the ID of the message from which the reaction was removed.
     *
     * @return The message ID as a {@link String}.
     */
    public String getMessageId() { return messageId; }

    /**
     * Returns the ID of the guild where this event occurred.
     *
     * @return The guild ID, or {@code null} if the reaction was removed in a private channel.
     */
    public String getGuildId() { return guildId; }

    /**
     * Returns the name of the emoji that was removed.
     *
     * @return The emoji name as a {@link String}.
     */
    public String getEmojiName() { return emojiName; }

    /**
     * Retrieves the {@link Member} object representing the user who removed the reaction.
     *
     * @return The {@link Member} object, or {@code null} if the member data is not available
     *         (e.g., in a DM context or if not provided by the gateway).
     */
    public Member getMember() { return member; }

    /**
     * Retrieves the {@link TextChannel} where the reaction was removed.
     *
     * @return The {@link TextChannel} object, or {@code null} if the guild is not found
     *         or the channel is unavailable.
     */
    public TextChannel getChannel() {
        if (guildId != null) {
            Guild guild = api.getGuildById(guildId);
            if (guild != null) return guild.getTextChannelById(channelId);
        }
        return null;
    }

    /**
     * Retrieves the {@link Guild} where the reaction was removed.
     *
     * @return The {@link Guild} object, or {@code null} if the event occurred
     *         outside of a guild or the guild is not in the cache.
     */
    public Guild getGuild() {
        return guildId != null ? api.getGuildById(guildId) : null;
    }
}