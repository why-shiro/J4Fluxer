package com.j4fluxer.events.message;

import com.j4fluxer.entities.channel.PrivateChannel;
import com.j4fluxer.entities.channel.PrivateChannelImpl;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

/**
 * Fired when a message is received in a Direct Message (DM) channel.
 */
public class PrivateMessageReceivedEvent extends MessageReceivedEvent {

    public PrivateMessageReceivedEvent(Fluxer api, Message message) {
        super(api, message);
    }

    /**
     * Always returns null as DMs are not associated with a Guild.
     */
    @Override
    public Guild getGuild() {
        return null;
    }

    /**
     * Retrieves the Private Channel where this message was sent.
     * @return The PrivateChannel instance.
     */
    public PrivateChannel getChannel() {
        return new PrivateChannelImpl(message.getChannelId(), message.getAuthor(), ((FluxerImpl) api).getRequester());
    }
}