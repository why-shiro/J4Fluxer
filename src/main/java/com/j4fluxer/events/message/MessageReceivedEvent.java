package com.j4fluxer.events.message;

import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.TextChannelImpl;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

/**
 * Fired when a new message is sent in a text channel.
 */
public class MessageReceivedEvent extends Event {
    private final Message message;

    public MessageReceivedEvent(Fluxer api, Message message) {
        super(api);
        this.message = message;
    }

    /** @return The {@link Message} object that was sent. */
    public Message getMessage() {
        return message;
    }

    /** @return The plain text content of the message. */
    public String getContent() {
        return message.getContent();
    }

    /**
     * Returns the channel where the message was sent.
     * <p>
     * Note: This returns a lazily-initialized channel object configured for this specific guild.
     *
     * @return The {@link TextChannel}, or {@code null} if sent in DMs.
     */
    public TextChannel getChannel() {
        if (message.getGuildId() == null) return null;

        Guild guild = api.getGuildById(message.getGuildId());

        if (guild != null) {
            return guild.getTextChannelById(message.getChannelId());
        }

        FluxerImpl core = (FluxerImpl) api;
        Guild tempGuild = new GuildImpl(message.getGuildId(), core.getRequester());
        return new TextChannelImpl(message.getChannelId(), tempGuild, core.getRequester());
    }
}