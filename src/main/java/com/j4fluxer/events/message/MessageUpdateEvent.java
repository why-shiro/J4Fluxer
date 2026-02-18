package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.message.MessageImpl;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

/**
 * Represents an event that is triggered when an existing message is updated or edited.
 * <p>This typically occurs when a user edits their message content, or when the message
 * metadata (such as embeds or pins) is updated by the system.</p>
 */
public class MessageUpdateEvent extends Event {

    /** The unique ID of the updated message. */
    private final String messageId;

    /** The ID of the channel where the message update occurred. */
    private final String channelId;

    /** The ID of the guild where the message update occurred. */
    private final String guildId;

    /** The {@link Message} object containing the updated information. */
    private final Message message;

    /**
     * Constructs a new {@code MessageUpdateEvent}.
     *
     * @param api  The {@link Fluxer} API instance.
     * @param data The JSON data received from the gateway containing the updated message payload.
     */
    public MessageUpdateEvent(Fluxer api, JsonNode data) {
        super(api);
        this.messageId = data.get("id").asText();
        this.channelId = data.get("channel_id").asText();
        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;

        // Note: Initializes the message implementation using the internal requester.
        this.message = new MessageImpl(data, ((FluxerImpl) api).getRequester());
    }

    /**
     * Returns the unique ID of the updated message.
     *
     * @return The message ID as a {@link String}.
     */
    public String getMessageId() { return messageId; }

    /**
     * Returns the ID of the channel where this event occurred.
     *
     * @return The channel ID as a {@link String}.
     */
    public String getChannelId() { return channelId; }

    /**
     * Returns the ID of the guild where this event occurred.
     *
     * @return The guild ID, or {@code null} if the update happened in a private channel.
     */
    public String getGuildId() { return guildId; }

    /**
     * Returns the {@link Message} object representing the message after the update.
     *
     * @return The updated {@link Message} object.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Retrieves the {@link Guild} object where the message was updated.
     *
     * @return The {@link Guild} object, or {@code null} if the guild ID is not present
     *         or the guild is not in the cache.
     */
    public Guild getGuild() {
        if (guildId == null) return null;
        return api.getGuildById(guildId);
    }

    /**
     * Retrieves the {@link TextChannel} where the message was updated.
     *
     * @return The {@link TextChannel} object, or {@code null} if the channel
     *         cannot be found or the guild is unavailable.
     */
    public TextChannel getChannel() {
        Guild guild = getGuild();
        if (guild != null) {
            return guild.getTextChannelById(channelId);
        }
        return null;
    }
}