package com.j4fluxer.entities.user;

import com.j4fluxer.entities.channel.PrivateChannel;
import com.j4fluxer.internal.requests.RestAction;

/**
 * Represents a global Fluxer user account.
 * <p>
 * This interface provides access to user details like username, avatar, and discriminator,
 * as well as actions such as opening a Direct Message (DM) channel.
 */
public interface User {

    /**
     * Retrieves the unique ID of the user.
     *
     * @return The unique ID of the user.
     */
    String getId();

    /**
     * Retrieves the username of the user.
     *
     * @return The username of the user.
     */
    String getUsername();

    /**
     * Retrieves the 4-digit discriminator of the user (e.g., "0001").
     *
     * @return The discriminator string.
     */
    String getDiscriminator();

    /**
     * Checks if this user is a bot account.
     *
     * @return {@code true} if the user is a bot, {@code false} otherwise.
     */
    boolean isBot();

    /**
     * Retrieves the avatar hash of the user.
     *
     * @return The avatar hash, or {@code null} if the user has no avatar.
     */
    String getAvatarHash();

    /**
     * Retrieves the direct URL to the user's avatar image.
     *
     * @return The avatar URL string, or {@code null} if no avatar is set.
     */
    String getAvatarUrl();

    /**
     * Opens a Private Channel (DM) with this user.
     * <p>
     * If a channel already exists between the current bot and this user,
     * it retrieves the existing channel instead of creating a new one.
     *
     * @return A {@link RestAction} that resolves to a {@link PrivateChannel}.
     * @throws IllegalStateException If this User object was created without API access.
     */
    RestAction<PrivateChannel> openPrivateChannel();
}