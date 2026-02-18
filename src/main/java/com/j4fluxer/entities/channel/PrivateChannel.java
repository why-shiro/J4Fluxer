package com.j4fluxer.entities.channel;

import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.RestAction;

/**
 * Represents a Direct Message (DM) channel between the bot and a user.
 */
public interface PrivateChannel extends Channel {

    /**
     * Returns the user who is the recipient of this DM.
     * @return The target {@link User}.
     */
    User getUser();

    /**
     * Sends a message to this DM channel.
     *
     * @param content The text content.
     * @return A {@link RestAction} resolving to the sent {@link Message}.
     */
    RestAction<Message> sendMessage(String content);
}