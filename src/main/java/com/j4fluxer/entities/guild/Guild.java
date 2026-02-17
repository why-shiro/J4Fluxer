package com.j4fluxer.entities.guild;

import com.j4fluxer.entities.channel.*;
import com.j4fluxer.entities.user.UserProfile;
import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

/**
 * Represents a Fluxer Guild (Server).
 */
public interface Guild {

    // --- Basic Info ---
    String getId();
    String getName();
    String getOwnerId();

    // --- Channel Retrieval ---

    /**
     * Retrieves any channel (Text, Voice, or Category) by its ID.
     * Useful when you have an ID but don't know the specific channel type.
     */
    GuildChannel getGuildChannelById(String id);

    /** Retrieves a TextChannel by its ID. */
    TextChannel getTextChannelById(String id);

    /** Retrieves a Category by its ID. */
    Category getCategoryById(String id);

    /** Fetches the full list of channels from the API. */
    RestAction<List<Channel>> retrieveChannels();

    // --- Channel Creation ---

    /** Creates a new Category. */
    RestAction<Category> createCategory(String name);

    /** Creates a Text Channel (no category). */
    RestAction<TextChannel> createTextChannel(String name);

    /** Creates a Text Channel inside a specific category. */
    RestAction<TextChannel> createTextChannel(String name, String parentId);

    /** Creates a Voice Channel (no category). */
    RestAction<VoiceChannel> createVoiceChannel(String name);

    /** Creates a Voice Channel inside a specific category. */
    RestAction<VoiceChannel> createVoiceChannel(String name, String parentId);

    // --- Roles & Members ---

    Role getRoleById(String id);
    List<Role> getRoles();

    RestAction<UserProfile> retrieveMemberProfile(String userId);

    RestAction<Void> addRoleToMember(String userId, String roleId);
    RestAction<Void> removeRoleFromMember(String userId, String roleId);

    // --- Moderation ---

    RestAction<Void> kickMember(String userId);

    RestAction<Void> banMember(String userId, String reason);
    RestAction<Void> banMember(String userId, int deleteMessageDays, long durationSeconds, String reason);
    RestAction<Void> unbanMember(String userId);

    RestAction<Void> timeoutMember(String userId, long durationSeconds);
    RestAction<Void> removeTimeout(String userId);

    // --- Guild Settings

    RestAction<Void> setName(String name);
    RestAction<Void> setAfkChannelId(String channelId);
    RestAction<Void> setAfkTimeout(int seconds);
    RestAction<Void> setSystemChannelId(String channelId);
    RestAction<Void> setDefaultNotificationLevel(int level);
}