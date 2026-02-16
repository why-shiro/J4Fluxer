package com.j4fluxer.entities.channel;

import com.j4fluxer.internal.requests.RestAction;

/**
 * The base interface for all types of Fluxer channels.
 */
public interface Channel {
    /**
     * @return The unique ID of this channel.
     */
    String getId();

    /**
     * @return The name of this channel.
     */
    String getName();

    /**
     * @return The {@link ChannelType} of this channel.
     */
    ChannelType getType();

    /**
     * Deletes this channel from the guild.
     *
     * @return A {@link RestAction} that resolves to {@code Void}.
     */
    RestAction<Void> delete();
}