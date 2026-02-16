package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

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

        if (data.has("guild_id")) {
            this.guildId = data.get("guild_id").asText();
        } else {
            this.guildId = null;
        }
    }

    public String getUserId() { return userId; }
    public String getChannelId() { return channelId; }
    public String getMessageId() { return messageId; }
    public String getGuildId() { return guildId; }
    public String getEmojiName() { return emojiName; }

    public TextChannel getChannel() {
        if (guildId != null) {
            Guild guild = api.getGuildById(guildId);
            if (guild != null) {
                return guild.getTextChannelById(channelId);
            }
        }
        return null;
    }

    public Guild getGuild() {
        if (guildId != null) {
            return api.getGuildById(guildId);
        }
        return null;
    }
}