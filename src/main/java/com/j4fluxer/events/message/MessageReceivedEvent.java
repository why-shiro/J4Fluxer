package com.j4fluxer.events.message;

import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.TextChannelImpl;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

public class MessageReceivedEvent extends Event {
    private final Message message;

    public MessageReceivedEvent(Fluxer api, Message message) {
        super(api);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public String getContent() {
        return message.getContent();
    }

    public TextChannel getChannel() {
        if (message.getGuildId() == null) return null;

        FluxerImpl core = (FluxerImpl) api;

        Guild tempGuild = new GuildImpl(message.getGuildId(), core.getRequester());

        return new TextChannelImpl(message.getChannelId(), tempGuild, core.getRequester());
    }
}