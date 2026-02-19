package com.j4fluxer.events.message;

import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a new message is received (either in a Guild or DM).
 * <p>
 * This is an abstract base class. You should listen for either
 * {@link GuildMessageReceivedEvent} or {@link PrivateMessageReceivedEvent}.
 */
public abstract class MessageReceivedEvent extends Event {

    protected final Message message;

    public MessageReceivedEvent(Fluxer api, Message message) {
        super(api);
        this.message = message;
    }

    /**
     * Retrieves the message object that triggered this event.
     *
     * @return The {@link Message} object.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Retrieves the user who sent the message.
     *
     * @return The {@link User} object of the author.
     */
    public User getAuthor() {
        return message.getAuthor();
    }

    /**
     * Retrieves the raw text content of the message.
     *
     * @return The plain text content.
     */
    public String getContent() {
        return message.getContent();
    }
}