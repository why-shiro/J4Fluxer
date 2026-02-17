package com.j4fluxer.entities.channel;

import com.j4fluxer.entities.message.Message;
import com.j4fluxer.internal.requests.RestAction;

/**
 * Represents a standard text channel where messages can be sent and received.
 */
public interface TextChannel extends GuildChannel {
    /**
     * @return The topic/description of the channel, or {@code null} if not set.
     */
    String getTopic();

    /**
     * @return Whether the channel is marked as NSFW (Not Safe For Work).
     */
    boolean isNSFW();

    /**
     * Sends a plain text message to this channel.
     *
     * @param content The content of the message.
     * @return A {@link RestAction} that resolves to the sent {@link Message}.
     */
    RestAction<Message> sendMessage(String content);

    RestAction<Void> setTopic(String topic);

    RestAction<Void> setNSFW(boolean nsfw);

    RestAction<Void> setSlowmode(int seconds);
}
