package com.j4fluxer.internal.requests;

import com.j4fluxer.internal.constants.Constants;

/**
 * Defines the structure of all supported API endpoints for the Fluxer platform.
 *
 * <p>This class acts as a template for RESTful requests, containing path placeholders
 * (e.g., {@code {guild_id}}) that are replaced during the compilation process.
 * It manages routing for channels, messages, guilds, members, and users.</p>
 */
public class Route {

    // --- Channel Routes ---

    /** POST request to create a new channel within a guild. */
    public static final Route CREATE_CHANNEL = new Route(Method.POST, "/guilds/{guild_id}/channels");

    /** GET request to retrieve a list of all channels within a guild. */
    public static final Route GET_GUILD_CHANNELS = new Route(Method.GET, "/guilds/{guild_id}/channels");

    /** DELETE request to permanently remove a specific channel. */
    public static final Route DELETE_CHANNEL = new Route(Method.DELETE, "/channels/{channel_id}");

    /** PUT request to set or update permission overrides for a specific user or role in a channel. */
    public static final Route MANAGE_PERMISSION = new Route(Method.PUT, "/channels/{channel_id}/permissions/{target_id}");

    /** DELETE request to remove permission overrides for a target from a channel. */
    public static final Route DELETE_PERMISSION = new Route(Method.DELETE, "/channels/{channel_id}/permissions/{target_id}");

    // --- Message Routes ---

    /** POST request to send a message to a text channel. */
    public static final Route SEND_MESSAGE = new Route(Method.POST, "/channels/{channel_id}/messages");

    /** PATCH request to update the content or metadata of an existing message. */
    public static final Route EDIT_MESSAGE = new Route(Method.PATCH, "/channels/{channel_id}/messages/{message_id}");

    /** DELETE request to remove a specific message from a channel. */
    public static final Route DELETE_MESSAGE = new Route(Method.DELETE, "/channels/{channel_id}/messages/{message_id}");

    /** PUT request to add a reaction to a specific message. */
    public static final Route ADD_REACTION = new Route(Method.PUT, "/channels/{channel_id}/messages/{message_id}/reactions/{emoji}/@me");

    /** DELETE request to remove the current bot's reaction from a message. */
    public static final Route REMOVE_REACTION = new Route(Method.DELETE, "/channels/{channel_id}/messages/{message_id}/reactions/{emoji}/@me");

    /** DELETE request to remove a specific user's reaction from a message. */
    public static final Route REMOVE_REACTION_USER = new Route(Method.DELETE, "/channels/{channel_id}/messages/{message_id}/reactions/{emoji}/{user_id}");

    // --- Guild & Member Routes ---

    /** GET request to retrieve detailed information about a guild. */
    public static final Route GET_GUILD = new Route(Method.GET, "/guilds/{guild_id}");

    /** POST request to create a new guild. */
    public static final Route CREATE_GUILD = new Route(Method.POST, "/guilds");

    /** DELETE request to kick a member from a guild. */
    public static final Route KICK_MEMBER = new Route(Method.DELETE, "/guilds/{guild_id}/members/{user_id}");

    /** PUT request to assign a specific role to a guild member. */
    public static final Route ADD_ROLE = new Route(Method.PUT, "/guilds/{guild_id}/members/{user_id}/roles/{role_id}");

    /** DELETE request to remove a role from a guild member. */
    public static final Route REMOVE_ROLE = new Route(Method.DELETE, "/guilds/{guild_id}/members/{user_id}/roles/{role_id}");

    /** PATCH request to modify member properties, such as timeouts or nicknames. */
    public static final Route MODIFY_MEMBER = new Route(Method.PATCH, "/guilds/{guild_id}/members/{user_id}");

    /** PUT request to ban a user from a guild. */
    public static final Route BAN_MEMBER = new Route(Method.PUT, "/guilds/{guild_id}/bans/{user_id}");

    /** DELETE request to remove a ban (unban) from a user in a guild. */
    public static final Route UNBAN_MEMBER = new Route(Method.DELETE, "/guilds/{guild_id}/bans/{user_id}");

    // --- User Routes ---

    /** GET request to retrieve a public user profile. */
    public static final Route GET_USER_PROFILE = new Route(Method.GET, "/users/{user_id}/profile");

    /** GET request to retrieve the current bot's user information. */
    public static final Route GET_ME = new Route(Method.GET, "/users/@me");

    /** PATCH request to modify channel properties (e.g., name, topic). */
    public static final Route MODIFY_CHANNEL = new Route(Method.PATCH, "/channels/{channel_id}");

    /** POST request to create a new invite for a channel. */
    public static final Route CREATE_INVITE = new Route(Method.POST, "/channels/{channel_id}/invites");

    /** PATCH request to update guild settings. */
    public static final Route MODIFY_GUILD = new Route(Method.PATCH, "/guilds/{guild_id}");

    /** GET request to retrieve a specific member from a guild. */
    public static final Route GET_MEMBER = new Route(Method.GET, "/guilds/{guild_id}/members/{user_id}");

    /** POST request to open a DM channel with a user. */
    public static final Route CREATE_DM = new Route(Method.POST, "/users/@me/channels");

    /** PUT request to pin a message in a channel. */
    public static final Route PIN_MESSAGE = new Route(Method.PUT, "/channels/{channel_id}/pins/{message_id}");

    /** DELETE request to unpin a message in a channel. */
    public static final Route UNPIN_MESSAGE = new Route(Method.DELETE, "/channels/{channel_id}/pins/{message_id}");


    /** The HTTP method required for this route. */
    private final Method method;

    /** The raw path containing optional placeholders. */
    private final String path;

    /**
     * Constructs a new {@code Route}.
     *
     * @param method The {@link Method} to use for the request.
     * @param path   The relative URL path with placeholders.
     */
    public Route(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    /**
     * Compiles the route into a {@link CompiledRoute} by replacing placeholders with provided values.
     * <p>Values must be provided in the order they appear in the path (e.g., guild_id then channel_id).</p>
     *
     * @param args The replacement values for the placeholders in the path.
     * @return A {@link CompiledRoute} ready to be executed.
     */
    public CompiledRoute compile(String... args) {
        String compiledPath = path;
        for (String arg : args) {
            compiledPath = compiledPath.replaceFirst("\\{[a-z_]+\\}", arg);
        }
        return new CompiledRoute(this.method, Constants.API_BASE + compiledPath);
    }

    /**
     * Enumeration of supported HTTP methods for the Fluxer API.
     */
    public enum Method {
        GET, POST, DELETE, PUT, PATCH
    }

    /**
     * Represents a fully prepared API route with a final URL and HTTP method.
     */
    public static class CompiledRoute {
        /** The HTTP method for the request. */
        public final Method method;
        /** The complete URL including the base API endpoint and resolved parameters. */
        public final String url;

        /**
         * Constructs a {@code CompiledRoute}.
         *
         * @param method The HTTP method.
         * @param url    The final resolved URL.
         */
        public CompiledRoute(Method method, String url) {
            this.method = method;
            this.url = url;
        }
    }
}