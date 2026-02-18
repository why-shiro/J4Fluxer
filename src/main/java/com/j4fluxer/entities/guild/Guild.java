package com.j4fluxer.entities.guild;

import com.j4fluxer.entities.channel.*;
import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.user.UserProfile;
import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

/**
 * Represents a Fluxer Guild, commonly referred to as a Server.
 *
 * <p>A Guild is a collection of channels, members, and roles. It serves as the
 * primary container for communication and management within the Fluxer ecosystem.</p>
 */
public interface Guild {

    // --- Basic Info ---

    /**
     * Returns the unique identifier of the guild.
     *
     * @return The guild ID as a {@link String}.
     */
    String getId();

    /**
     * Returns the name of the guild.
     *
     * @return The guild name.
     */
    String getName();

    /**
     * Returns the ID of the user who owns the guild.
     *
     * @return The owner's user ID.
     */
    String getOwnerId();

    // --- Channel Retrieval ---

    /**
     * Retrieves a channel (Text, Voice, or Category) by its ID from the internal cache.
     *
     * <p>This is useful when the specific type of the channel is unknown.</p>
     *
     * @param id The ID of the channel to retrieve.
     * @return The {@link GuildChannel} object, or {@code null} if not found in the cache.
     */
    GuildChannel getGuildChannelById(String id);

    /**
     * Retrieves a {@link TextChannel} by its ID from the internal cache.
     *
     * @param id The ID of the text channel.
     * @return The {@link TextChannel} object, or {@code null} if not found or if the channel is not a text channel.
     */
    TextChannel getTextChannelById(String id);

    /**
     * Retrieves a {@link Category} by its ID from the internal cache.
     *
     * @param id The ID of the category.
     * @return The {@link Category} object, or {@code null} if not found or if the channel is not a category.
     */
    Category getCategoryById(String id);

    /**
     * Retrieves a {@link VoiceChannel} by its ID from the internal cache.
     *
     * @param id The ID of the voice channel.
     * @return The {@link VoiceChannel} object, or {@code null} if not found.
     */
    VoiceChannel getVoiceChannelById(String id);

    /**
     * Fetches the complete list of channels belonging to this guild from the Fluxer API.
     *
     * @return A {@link RestAction} providing a list of {@link Channel} objects.
     */
    RestAction<List<Channel>> retrieveChannels();


    // --- Channel Creation ---

    /**
     * Creates a new {@link Category} in this guild.
     *
     * @param name The name of the new category.
     * @return A {@link RestAction} providing the newly created {@link Category}.
     */
    RestAction<Category> createCategory(String name);

    /**
     * Creates a new {@link TextChannel} at the top level of the guild (not inside a category).
     *
     * @param name The name of the new text channel.
     * @return A {@link RestAction} providing the newly created {@link TextChannel}.
     */
    RestAction<TextChannel> createTextChannel(String name);

    /**
     * Creates a new {@link TextChannel} inside a specific {@link Category}.
     *
     * @param name     The name of the new text channel.
     * @param parentId The ID of the parent category.
     * @return A {@link RestAction} providing the newly created {@link TextChannel}.
     */
    RestAction<TextChannel> createTextChannel(String name, String parentId);

    /**
     * Creates a new {@link VoiceChannel} at the top level of the guild.
     *
     * @param name The name of the new voice channel.
     * @return A {@link RestAction} providing the newly created {@link VoiceChannel}.
     */
    RestAction<VoiceChannel> createVoiceChannel(String name);

    /**
     * Creates a new {@link VoiceChannel} inside a specific {@link Category}.
     *
     * @param name     The name of the new voice channel.
     * @param parentId The ID of the parent category.
     * @return A {@link RestAction} providing the newly created {@link VoiceChannel}.
     */
    RestAction<VoiceChannel> createVoiceChannel(String name, String parentId);

    // --- Roles & Members ---

    /**
     * Retrieves a {@link Role} from the guild by its ID.
     *
     * @param id The ID of the role.
     * @return The {@link Role} object, or {@code null} if not found.
     */
    Role getRoleById(String id);

    /**
     * Returns a list of all roles configured in this guild.
     *
     * @return A {@link List} of {@link Role} objects.
     */
    List<Role> getRoles();

    /**
     * Retrieves a {@link Member} from the guild's local cache.
     *
     * @param userId The ID of the member (same as User ID).
     * @return The {@link Member} object, or {@code null} if not currently cached.
     */
    Member getMemberById(String userId);

    /**
     * Manually adds a member to the guild's cache.
     * <p>This is used internally by events.</p>
     *
     * @param member The member object to cache.
     */
    void cacheMember(Member member);

    /**
     * Fetches the detailed profile of a member from the Fluxer API.
     *
     * @param userId The ID of the user whose profile is to be retrieved.
     * @return A {@link RestAction} providing the {@link UserProfile}.
     */
    RestAction<UserProfile> retrieveMemberProfile(String userId);

    /**
     * Fetches a Member object from the Fluxer API.
     * <p>Use this if the member is not found in the local cache.</p>
     *
     * @param userId The ID of the user.
     * @return A {@link RestAction} providing the {@link Member}.
     */
    RestAction<Member> retrieveMember(String userId);

    /**
     * Assigns a specific role to a guild member.
     *
     * @param userId The ID of the member.
     * @param roleId The ID of the role to assign.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> addRoleToMember(String userId, String roleId);

    /**
     * Removes a specific role from a guild member.
     *
     * @param userId The ID of the member.
     * @param roleId The ID of the role to remove.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> removeRoleFromMember(String userId, String roleId);

    // --- Moderation ---

    /**
     * Kicks a member from the guild.
     *
     * @param userId The ID of the member to kick.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> kickMember(String userId);

    /**
     * Bans a user from the guild with a specified reason.
     *
     * @param userId The ID of the user to ban.
     * @param reason The reason for the ban, visible in audit logs.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> banMember(String userId, String reason);

    /**
     * Bans a user from the guild with detailed parameters.
     *
     * @param userId             The ID of the user to ban.
     * @param deleteMessageDays  Number of days of message history to delete (0-7).
     * @param durationSeconds    Duration of the ban in seconds (use 0 for permanent).
     * @param reason             The reason for the ban.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> banMember(String userId, int deleteMessageDays, long durationSeconds, String reason);

    /**
     * Removes a ban from a user in this guild.
     *
     * @param userId The ID of the user to unban.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> unbanMember(String userId);

    /**
     * Applies a timeout (mute/restriction) to a guild member.
     *
     * @param userId          The ID of the member to timeout.
     * @param durationSeconds The duration of the timeout in seconds.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> timeoutMember(String userId, long durationSeconds);

    /**
     * Removes an active timeout from a guild member.
     *
     * @param userId The ID of the member.
     * @return A {@link RestAction} representing the operation.
     */
    RestAction<Void> removeTimeout(String userId);

    // --- Guild Settings ---

    /**
     * Updates the name of the guild.
     *
     * @param name The new name for the guild.
     * @return A {@link RestAction} representing the update.
     */
    RestAction<Void> setName(String name);

    /**
     * Sets the channel where members are moved when they are AFK (Away From Keyboard).
     *
     * @param channelId The ID of the AFK voice channel.
     * @return A {@link RestAction} representing the update.
     */
    RestAction<Void> setAfkChannelId(String channelId);

    /**
     * Sets the timeout duration before a member is moved to the AFK channel.
     *
     * @param seconds Timeout in seconds.
     * @return A {@link RestAction} representing the update.
     */
    RestAction<Void> setAfkTimeout(int seconds);

    /**
     * Sets the channel where system messages (like welcome messages) are sent.
     *
     * @param channelId The ID of the system channel.
     * @return A {@link RestAction} representing the update.
     */
    RestAction<Void> setSystemChannelId(String channelId);

    /**
     * Configures the default notification level for new members joining the guild.
     *
     * @param level The notification level (e.g., 0 for all messages, 1 for mentions only).
     * @return A {@link RestAction} representing the update.
     */
    RestAction<Void> setDefaultNotificationLevel(int level);
}