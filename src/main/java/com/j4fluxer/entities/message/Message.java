package com.j4fluxer.entities.message;

import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

/**
 * Represents a message sent in a Fluxer channel.
 */
public interface Message {
    /** @return The unique ID of the message. */
    String getId();

    /** @return The plain text content of the message. */
    String getContent();

    /** @return The ID of the channel where this message was sent. */
    String getChannelId();

    /** @return The {@link User} who authored the message. */
    User getAuthor();

    /** @return The {@link Member} who authored the message, or {@code null} if not in a guild. */
    Member getMember();

    /** @return The ID of the guild where this message was sent, or {@code null} if sent in a DM. */
    String getGuildId();

    /** @return A list of users mentioned in this message. */
    List<User> getMentions();

    /**
     * Edits the content of this message.
     * @param newContent The new text.
     * @return A {@link RestAction} that resolves to the updated {@link Message}.
     */
    RestAction<Message> edit(String newContent);

    /**
     * Deletes this message.
     * @return A {@link RestAction} that resolves to {@code Void}.
     */
    RestAction<Void> delete();

    /**
     * Adds a reaction emoji to this message.
     * @param emoji Unicode emoji or custom emoji format (name:id).
     * @return A {@link RestAction} that resolves to {@code Void}.
     */
    RestAction<Void> addReaction(String emoji);

    /**
     * Removes your reaction from this message.
     * @param emoji Unicode emoji or custom emoji format (name:id).
     * @return A {@link RestAction} that resolves to {@code Void}.
     */
    RestAction<Void> removeReaction(String emoji);
}