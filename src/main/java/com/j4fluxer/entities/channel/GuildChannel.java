package com.j4fluxer.entities.channel;

import com.j4fluxer.entities.Permission;
import com.j4fluxer.entities.PermissionOverwrite;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.RestAction;

import java.util.EnumSet;
import java.util.List;

/**
 * Represents a channel that belongs to a {@link Guild}.
 */
public interface GuildChannel extends Channel {
    /**
     * @return The {@link Guild} this channel belongs to.
     */
    Guild getGuild();

    /**
     * @return The ID of the parent category, or {@code null} if not in a category.
     */
    String getParentId();

    /**
     * @return The position of this channel in the sidebar (ascending).
     */
    int getPosition();

    /**
     * @return A list of {@link PermissionOverwrite PermissionOverwrites} applied to this channel.
     */
    List<PermissionOverwrite> getPermissionOverwrites();

    /**
     * Adds or updates a permission override for a role or member in this channel.
     *
     * @param targetId The ID of the role or member.
     * @param type     The type of target (0 for Role, 1 for Member).
     * @param allow    The set of {@link Permission Permissions} to allow.
     * @param deny     The set of {@link Permission Permissions} to deny.
     * @return A {@link RestAction} that resolves to {@code Void}.
     */
    RestAction<Void> upsertPermissionOverride(String targetId, int type, EnumSet<Permission> allow, EnumSet<Permission> deny);

    /**
     * Deletes a permission override for the specified target in this channel.
     *
     * @param targetId The ID of the role or member.
     * @return A {@link RestAction} that resolves to {@code Void}.
     */
    RestAction<Void> deletePermissionOverride(String targetId);
}