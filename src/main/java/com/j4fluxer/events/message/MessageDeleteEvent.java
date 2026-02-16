package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a single message is deleted from a channel.
 * <p>
 * Note: Since the message is already deleted, only its ID and location data are provided.
 */
public class MessageDeleteEvent extends Event {
    private final String messageId;
    private final String channelId;
    private final String guildId;

    /**
     * Internal constructor for MessageDeleteEvent.
     *
     * @param api  The core {@link Fluxer} instance.
     * @param data The JSON data from the gateway.
     */
    public MessageDeleteEvent(Fluxer api, JsonNode data) {
        super(api);
        this.messageId = data.get("id").asText();
        this.channelId = data.get("channel_id").asText();
        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;
    }

    /** @return The ID of the deleted message. */
    public String getMessageId() { return messageId; }

    /** @return The ID of the channel where the message was deleted. */
    public String getChannelId() { return channelId; }

    /** @return The ID of the guild, or {@code null} if it was a DM. */
    public String getGuildId() { return guildId; }
}