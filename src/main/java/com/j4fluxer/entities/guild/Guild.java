package com.j4fluxer.entities.guild;

import com.j4fluxer.entities.channel.Channel;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.VoiceChannel;
import com.j4fluxer.entities.user.UserProfile;
import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

/**
 * Represents a Fluxer Guild (Server).
 */
public interface Guild {

    /**
     * @return The unique ID of this guild.
     */
    String getId();

    /**
     * @return The name of this guild.
     */
    String getName();

    /**
     * @return The ID of the guild owner.
     */
    String getOwnerId();

    /**
     * Retrieves a TextChannel by its ID without making an API call.
     *
     * @param id The channel ID.
     * @return A {@link TextChannel} instance.
     */
    TextChannel getTextChannelById(String id);

    /**
     * Retrieves a Role by its ID from the cache.
     *
     * @param id The role ID.
     * @return The {@link Role} object, or null if not found.
     */
    Role getRoleById(String id);

    /**
     * @return A list of all cached roles in this guild.
     */
    List<Role> getRoles();

    /**
     * Fetches the list of channels from the API.
     *
     * @return A RestAction that resolves to a list of channels.
     */
    RestAction<List<Channel>> retrieveChannels();

    /**
     * Fetches the full profile of a member in this guild.
     *
     * @param userId The ID of the user.
     * @return A RestAction that resolves to {@link UserProfile}.
     */
    RestAction<UserProfile> retrieveMemberProfile(String userId);

    /**
     * Creates a new Text Channel in this guild.
     *
     * @param name The name of the channel.
     * @return A RestAction that resolves to the created {@link TextChannel}.
     */
    RestAction<TextChannel> createTextChannel(String name);

    /**
     * Creates a new Voice Channel in this guild.
     *
     * @param name The name of the channel.
     * @return A RestAction that resolves to the created {@link VoiceChannel}.
     */
    RestAction<VoiceChannel> createVoiceChannel(String name);

    /**
     * Kicks a member from the guild.
     *
     * @param userId The ID of the user to kick.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> kickMember(String userId);

    /**
     * Bans a member from the guild indefinitely.
     *
     * @param userId The ID of the user to ban.
     * @param reason The reason for the ban.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> banMember(String userId, String reason);

    /**
     * Bans a member with advanced options (Tempban and Message Deletion).
     *
     * @param userId             The ID of the user to ban.
     * @param deleteMessageDays  Number of days to delete past messages (0-7).
     * @param durationSeconds    Duration of the ban in seconds (0 for infinite).
     * @param reason             The reason for the ban.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> banMember(String userId, int deleteMessageDays, long durationSeconds, String reason);

    /**
     * Unbans a user.
     *
     * @param userId The ID of the user to unban.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> unbanMember(String userId);

    /**
     * Times out (mutes) a member for a specific duration.
     *
     * @param userId          The ID of the user.
     * @param durationSeconds Duration in seconds.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> timeoutMember(String userId, long durationSeconds);

    /**
     * Removes the timeout (unmutes) a member.
     *
     * @param userId The ID of the user.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> removeTimeout(String userId);

    /**
     * Adds a role to a member.
     *
     * @param userId The ID of the user.
     * @param roleId The ID of the role to add.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> addRoleToMember(String userId, String roleId);

    /**
     * Removes a role from a member.
     *
     * @param userId The ID of the user.
     * @param roleId The ID of the role to remove.
     * @return A RestAction that resolves to Void.
     */
    RestAction<Void> removeRoleFromMember(String userId, String roleId);
}