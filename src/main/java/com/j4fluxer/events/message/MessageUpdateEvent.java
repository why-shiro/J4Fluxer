package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class MessageUpdateEvent extends Event {
    private final String messageId;
    private final String channelId;
    private final String guildId; // Eklendi
    private final String content;

    public MessageUpdateEvent(Fluxer api, JsonNode data) {
        super(api);
        this.messageId = data.get("id").asText();
        this.channelId = data.get("channel_id").asText();
        this.content = data.has("content") ? data.get("content").asText() : null;

        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;
    }

    public String getMessageId() { return messageId; }
    public String getChannelId() { return channelId; }
    public String getContent() { return content; }
    public String getGuildId() { return guildId; }


    public Guild getGuild() {
        if (guildId == null) return null;
        return api.getGuildById(guildId);
    }

    public TextChannel getChannel() {
        Guild guild = getGuild();
        if (guild != null) {
            return guild.getTextChannelById(channelId);
        }
        // Update here in 0.1.3
        return null;
    }
}