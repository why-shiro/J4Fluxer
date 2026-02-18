package com.j4fluxer.entities.channel;

import com.j4fluxer.entities.message.Message;
import com.j4fluxer.internal.requests.RestAction;

/**
 * Represents a standard text-based channel within a Fluxer guild.
 *
 * <p>Text channels are the primary medium for communication, allowing users to
 * send and receive messages, manage channel topics, and configure safety settings
 * like NSFW filters and slowmode.</p>
 */
public interface TextChannel extends GuildChannel {

    /**
     * Retrieves the topic or description of this channel.
     *
     * @return The channel topic as a {@link String}, or {@code null} if no topic is set.
     */
    String getTopic();

    /**
     * Checks if the channel is marked as NSFW (Not Safe For Work).
     *
     * <p>NSFW channels are typically used for age-restricted or sensitive content
     * within the Fluxer platform.</p>
     *
     * @return {@code true} if the channel is NSFW, otherwise {@code false}.
     */
    boolean isNSFW();

    /**
     * Sends a plain text message to this channel.
     *
     * @param content The text content of the message to be sent.
     * @return A {@link RestAction} that, when executed, provides the sent {@link Message} object.
     */
    RestAction<Message> sendMessage(String content);

    /**
     * Updates the topic or description of this channel.
     *
     * @param topic The new topic string for the channel.
     * @return A {@link RestAction} representing the update operation.
     */
    RestAction<Void> setTopic(String topic);

    /**
     * Sets whether this channel should be marked as NSFW.
     *
     * @param nsfw {@code true} to enable NSFW status, {@code false} to disable it.
     * @return A {@link RestAction} representing the update operation.
     */
    RestAction<Void> setNSFW(boolean nsfw);

    /**
     * Sets the slowmode interval for this channel.
     *
     * <p>Slowmode limits how often users can send messages in the channel.
     * Setting this to 0 disables slowmode.</p>
     *
     * @param seconds The amount of time in seconds that users must wait between sending messages.
     * @return A {@link RestAction} representing the update operation.
     */
    RestAction<Void> setSlowmode(int seconds);
}