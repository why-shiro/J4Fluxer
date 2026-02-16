package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
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

    public MessageReactionAddEvent(Fluxer api, JsonNode data) {
        super(api);
        this.userId = data.get("user_id").asText();
        this.channelId = data.get("channel_id").asText();
        this.messageId = data.get("message_id").asText();
        this.emojiName = data.get("emoji").get("name").asText();
        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;
    }

    /** @return The ID of the user who reacted. */
    public String getUserId() { return userId; }

    /** @return The name of the emoji used (e.g., "🎉"). */
    public String getEmojiName() { return emojiName; }

    /** @return The ID of the message that was reacted to. */
    public String getMessageId() { return messageId; }

    /** @return The {@link TextChannel} where the reaction happened. */
    public TextChannel getChannel() {
        if (guildId != null) {
            Guild guild = api.getGuildById(guildId);
            if (guild != null) return guild.getTextChannelById(channelId);
        }
        return null;
    }

    /** @return The {@link Guild} where the reaction happened. */
    public Guild getGuild() {
        return guildId != null ? api.getGuildById(guildId) : null;
    }
}