package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Represents an event triggered when a single message is deleted in a channel.
 */
public class MessageDeleteEvent extends Event {

    /**
     * The unique ID of the message that was deleted.
     */
    private final String messageId;

    /**
     * The ID of the channel where the message was deleted.
     */
    private final String channelId;

    /**
     * The ID of the guild where the message was deleted.
     */
    private final String guildId;

    /**
     * Constructs a new {@code MessageDeleteEvent}.
     *
     * @param api  The {@link Fluxer} API instance.
     * @param data The JSON data received from the gateway containing the event payload.
     */
    public MessageDeleteEvent(Fluxer api, JsonNode data) {
        super(api);
        this.messageId = data.get("id").asText();
        this.channelId = data.get("channel_id").asText();
        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;
    }

    /**
     * Returns the unique ID of the deleted message.
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
     * @return The guild ID, or {@code null} if the event occurred in a private message (DM).
     */
    public String getGuildId() { return guildId; }

    /**
     * Retrieves the {@link Guild} object associated with this event.
     *
     * @return The {@link Guild} object, or {@code null} if the guild ID is missing
     *         or the guild is not found in the cache.
     */
    public Guild getGuild() {
        if (guildId == null) return null;
        return api.getGuildById(guildId);
    }

    /**
     * Retrieves the {@link TextChannel} object where the message was deleted.
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