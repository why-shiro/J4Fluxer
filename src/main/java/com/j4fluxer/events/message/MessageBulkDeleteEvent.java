package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event that is triggered when multiple messages are deleted at once in a channel.
 * This typically occurs during a bulk delete operation (e.g., via a bot command or moderation tool).
 */
public class MessageBulkDeleteEvent extends Event {

    /**
     * A list of IDs for the messages that were deleted.
     */
    private final List<String> messageIds = new ArrayList<>();

    /**
     * The ID of the channel where the messages were deleted.
     */
    private final String channelId;

    /**
     * The ID of the guild where the messages were deleted.
     */
    private final String guildId;

    /**
     * Constructs a new {@code MessageBulkDeleteEvent}.
     *
     * @param api  The {@link Fluxer} API instance.
     * @param data The JSON data received from the gateway containing the event payload.
     */
    public MessageBulkDeleteEvent(Fluxer api, JsonNode data) {
        super(api);
        this.channelId = data.get("channel_id").asText();
        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;

        if (data.has("ids")) {
            for (JsonNode idNode : data.get("ids")) {
                messageIds.add(idNode.asText());
            }
        }
    }

    /**
     * Returns a list of the unique IDs of the deleted messages.
     *
     * @return A {@link List} of message ID strings.
     */
    public List<String> getMessageIds() { return messageIds; }

    /**
     * Returns the ID of the channel where this event occurred.
     *
     * @return The channel ID as a {@link String}.
     */
    public String getChannelId() { return channelId; }

    /**
     * Returns the ID of the guild where this event occurred.
     *
     * @return The guild ID, or {@code null} if the event did not occur within a guild.
     */
    public String getGuildId() { return guildId; }

    /**
     * Retrieves the {@link Guild} object associated with this event.
     *
     * @return The {@link Guild} object, or {@code null} if the guild ID is not present
     *         or the guild cannot be found in the cache.
     */
    public Guild getGuild() {
        if (guildId == null) return null;
        return api.getGuildById(guildId);
    }

    /**
     * Retrieves the {@link TextChannel} object where the messages were deleted.
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