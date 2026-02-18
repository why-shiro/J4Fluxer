package com.j4fluxer.fluxer;

import com.j4fluxer.entities.OnlineStatus;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.RestAction;

/**
 * The core interface of the J4Fluxer (Java for Fluxer API).
 * <p>
 * This interface acts as the main entry point to interact with the Fluxer API,
 * access the cache, and manage the bot's state.
 */
public interface Fluxer {

    /**
     * Registers one or more event listeners to the bot.
     * <p>
     * These listeners will receive events such as {@code MessageReceivedEvent}.
     *
     * @param listenersToAdd The listeners to register (must implement {@code EventListener} or extend {@code ListenerAdapter}).
     */
    void addEventListener(Object... listenersToAdd);

    /**
     * Retrieves a Guild (Server) by its unique ID.
     * <p>
     * This method first checks the internal cache. If the guild is not cached,
     * it attempts to fetch it from the API.
     *
     * @param id The ID of the guild.
     * @return The {@link Guild} object, or {@code null} if not found.
     */
    Guild getGuildById(String id);

    /**
     * Creates a new Guild.
     * <p>
     * Note: Bots are usually not allowed to create guilds. This is mostly for user tokens.
     *
     * @param name The name of the new guild.
     * @return A {@link RestAction} that resolves to the created {@link Guild}.
     */
    RestAction<Guild> createGuild(String name);

    /**
     * Sets the presence status of the current session.
     *
     * @param status The {@link OnlineStatus} (e.g., ONLINE, DND, IDLE).
     */
    void setStatus(OnlineStatus status);

    /**
     * Retrieves a User from the Fluxer API by their ID.
     *
     * @param userId The ID of the user.
     * @return A {@link RestAction} that resolves to the {@link User} object.
     */
    RestAction<User> retrieveUser(String userId);

}