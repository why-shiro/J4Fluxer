package com.j4fluxer.entities.user;

/**
 * Represents a global Fluxer user account.
 */
public interface User {
    /** @return The unique ID of the user. */
    String getId();

    /** @return The username of the user. */
    String getUsername();

    /** @return The 4-digit discriminator (e.g., "0001"). */
    String getDiscriminator();

    /** @return Whether this user is a bot account. */
    boolean isBot();

    /** @return The avatar hash, or {@code null} if the user has no avatar. */
    String getAvatarHash();

    /** @return The direct URL to the user's avatar image. */
    String getAvatarUrl();
}