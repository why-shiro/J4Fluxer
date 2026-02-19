package com.j4fluxer.events.message;

import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.TextChannelImpl;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.entities.member.Member; // Import Eklendi
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

/**
 * Fired when a message is received in a text channel within a Guild.
 */
public class GuildMessageReceivedEvent extends MessageReceivedEvent {

    public GuildMessageReceivedEvent(Fluxer api, Message message) {
        super(api, message);
        this.setGuildId(message.getGuildId());
    }

    /**
     * Retrieves the Guild where this message was sent.
     * @return The Guild instance (never null).
     */
    @Override
    public Guild getGuild() {
        return api.getGuildById(message.getGuildId());
    }

    /**
     * Retrieves the Member who sent the message.
     * <p>Since this event occurs in a guild, the member object is guaranteed to be present.</p>
     *
     * @return The {@link Member} object of the author.
     */
    public Member getMember() {
        return message.getMember();
    }

    /**
     * Retrieves the TextChannel where this message was sent.
     * @return The TextChannel instance.
     */
    public TextChannel getChannel() {
        Guild guild = getGuild();
        if (guild != null) {
            return guild.getTextChannelById(message.getChannelId());
        }
        FluxerImpl core = (FluxerImpl) api;
        return new TextChannelImpl(message.getChannelId(), new GuildImpl(message.getGuildId(), core.getRequester()), core.getRequester());
    }
}