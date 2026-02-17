package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class MessageDeleteEvent extends Event {
    private final String messageId;
    private final String channelId;
    private final String guildId;

    public MessageDeleteEvent(Fluxer api, JsonNode data) {
        super(api);
        this.messageId = data.get("id").asText();
        this.channelId = data.get("channel_id").asText();
        this.guildId = data.has("guild_id") ? data.get("guild_id").asText() : null;
    }

    public String getMessageId() { return messageId; }
    public String getChannelId() { return channelId; }
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
        return null;
    }
}