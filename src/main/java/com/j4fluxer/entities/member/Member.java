package com.j4fluxer.entities.member;

import com.j4fluxer.entities.channel.PrivateChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

/**
 * Represents a {@link User} specifically within the context of a {@link Guild}.
 * <p>
 * This interface provides guild-specific actions such as managing roles, kicking, banning,
 * timing out, and changing the nickname of the member.
 */
public interface Member {

    /**
     * Retrieves the underlying global {@link User} account.
     *
     * @return The user object.
     */
    User getUser();

    /**
     * Retrieves a list of IDs for the roles assigned to this member in the guild.
     *
     * @return An unmodifiable list of role IDs.
     */
    List<String> getRoleIds();

    /**
     * Retrieves the {@link Guild} this member belongs to.
     *
     * @return The guild object.
     */
    Guild getGuild();

    /**
     * Opens a Private Channel (DM) with this member.
     * <p>This is a shortcut for {@code getUser().openPrivateChannel()}.</p>
     *
     * @return A {@link RestAction} resolving to the {@link PrivateChannel}.
     */
    RestAction<PrivateChannel> openPrivateChannel();

    // --- ROLES ---

    /**
     * Adds a role to this member.
     *
     * @param roleId The ID of the role to add.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> addRole(String roleId);

    /**
     * Removes a role from this member.
     *
     * @param roleId The ID of the role to remove.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> removeRole(String roleId);

    /**
     * Updates the member's roles to match the provided list.
     * <p>Any roles currently assigned but not in the list will be removed.</p>
     *
     * @param roleIds The new list of role IDs.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> modifyRoles(List<String> roleIds);

    // --- MODERATION ---

    /**
     * Kicks this member from the guild.
     *
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> kick();

    /**
     * Bans this member from the guild.
     *
     * @param reason The reason for the ban.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> ban(String reason);

    /**
     * Bans this member from the guild with detailed options.
     *
     * @param deleteMessageDays Number of days of message history to delete (0-7).
     * @param durationSeconds   Duration of the ban in seconds.
     * @param reason            The reason for the ban.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> ban(int deleteMessageDays, long durationSeconds, String reason);

    /**
     * Puts this member in a timeout (communication disabled) for a specific duration.
     *
     * @param durationSeconds The duration in seconds.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> timeout(long durationSeconds);

    /**
     * Removes the timeout from this member.
     *
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> removeTimeout();

    /**
     * Changes the nickname of this member in the guild.
     *
     * @param nickname The new nickname, or {@code null} to reset it.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> modifyNickname(String nickname);
}