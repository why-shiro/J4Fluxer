package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import java.util.ArrayList;
import java.util.List;

public class MessageBulkDeleteEvent extends Event {
    private final List<String> messageIds = new ArrayList<>();
    private final String channelId;
    private final String guildId;

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

    public List<String> getMessageIds() { return messageIds; }
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