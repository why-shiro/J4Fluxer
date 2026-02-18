package com.j4fluxer.entities.message;

import com.j4fluxer.entities.channel.GuildChannel;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.VoiceChannel;
import com.j4fluxer.entities.guild.Role;
import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

/**
 * Represents a message sent within a Fluxer channel.
 *
 * <p>A message contains text content, author information, and metadata about mentions.
 * It also provides methods to perform actions such as editing, deleting, and managing reactions.</p>
 */
public interface Message {

    /**
     * Returns the unique identifier of this message.
     *
     * @return The message ID as a {@link String}.
     */
    String getId();

    /**
     * Returns the plain text content of the message.
     *
     * @return The message content.
     */
    String getContent();

    /**
     * Returns the ID of the channel where this message was sent.
     *
     * @return The channel ID.
     */
    String getChannelId();

    /**
     * Returns the global {@link User} who authored this message.
     *
     * @return The {@link User} instance of the author.
     */
    User getAuthor();

    /**
     * Returns the {@link Member} who authored the message in the context of a guild.
     *
     * @return The {@link Member} object, or {@code null} if the message was not sent
     *         within a guild (e.g., in a Direct Message).
     */
    Member getMember();

    /**
     * Returns the ID of the guild where this message was sent.
     *
     * @return The guild ID, or {@code null} if the message was sent in a private channel.
     */
    String getGuildId();

    /**
     * Returns a list of users explicitly mentioned in this message.
     *
     * @return A {@link List} of mentioned {@link User}s.
     */
    List<User> getMentions();

    /**
     * Edits the existing content of this message.
     *
     * @param newContent The new text content for the message.
     * @return A {@link RestAction} that, when executed, provides the updated {@link Message}.
     */
    RestAction<Message> edit(String newContent);

    /**
     * Deletes this message from the Fluxer platform.
     *
     * @return A {@link RestAction} representing the deletion operation.
     */
    RestAction<Void> delete();

    /**
     * Adds a reaction emoji to this message.
     *
     * @param emoji The unicode emoji or custom emoji format (e.g., "name:id").
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> addReaction(String emoji);

    /**
     * Returns a list of all guild channels mentioned in this message.
     *
     * @return A {@link List} of {@link GuildChannel} mentions.
     */
    List<GuildChannel> getMentionedChannels();

    /**
     * Returns a list of text channels specifically mentioned in this message.
     *
     * @return A {@link List} of {@link TextChannel} mentions.
     */
    List<TextChannel> getMentionedTextChannels();

    /**
     * Returns a list of voice channels specifically mentioned in this message.
     *
     * @return A {@link List} of {@link VoiceChannel} mentions.
     */
    List<VoiceChannel> getMentionedVoiceChannels();

    /**
     * Returns a list of roles mentioned in this message.
     *
     * @return A {@link List} of {@link Role} mentions.
     */
    List<Role> getMentionedRoles();

    /**
     * Removes the current bot's reaction from this message.
     *
     * @param emoji The emoji to remove.
     * @return A {@link RestAction} representing the removal.
     */
    RestAction<Void> removeReaction(String emoji);

    /**
     * Removes a specific user's reaction from this message.
     *
     * @param user  The {@link User} whose reaction should be removed.
     * @param emoji The emoji to remove.
     * @return A {@link RestAction} representing the removal.
     */
    RestAction<Void> removeReaction(User user, String emoji);

    /**
     * Removes a reaction from this message based on a user's ID.
     *
     * @param userId The ID of the user whose reaction should be removed.
     * @param emoji  The emoji to remove.
     * @return A {@link RestAction} representing the removal.
     */
    RestAction<Void> removeReaction(String userId, String emoji);
}