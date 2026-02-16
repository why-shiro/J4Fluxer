package com.j4fluxer.internal.requests;

import com.j4fluxer.internal.constants.Constants;

/**
 * Represents the structure of an API request to the Fluxer platform.
 * <p>
 * This class contains static definitions for all supported API endpoints
 * and handles the injection of parameters (like IDs) into path placeholders.
 */
public class Route {

    // --- Channel Routes ---
    /** POST request to create a channel within a guild. */
    public static final Route CREATE_CHANNEL = new Route(Method.POST, "/guilds/{guild_id}/channels");
    /** GET request to retrieve all channels of a guild. */
    public static final Route GET_GUILD_CHANNELS = new Route(Method.GET, "/guilds/{guild_id}/channels");
    /** DELETE request to remove a specific channel. */
    public static final Route DELETE_CHANNEL = new Route(Method.DELETE, "/channels/{channel_id}");
    /** PUT request to manage permissions for a role or user on a channel. */
    public static final Route MANAGE_PERMISSION = new Route(Method.PUT, "/channels/{channel_id}/permissions/{target_id}");
    /** DELETE request to remove permission overrides from a channel. */
    public static final Route DELETE_PERMISSION = new Route(Method.DELETE, "/channels/{channel_id}/permissions/{target_id}");

    // --- Message Routes ---
    /** POST request to send a message to a channel. */
    public static final Route SEND_MESSAGE = new Route(Method.POST, "/channels/{channel_id}/messages");
    /** PATCH request to edit an existing message. */
    public static final Route EDIT_MESSAGE = new Route(Method.PATCH, "/channels/{channel_id}/messages/{message_id}");
    /** DELETE request to remove a specific message. */
    public static final Route DELETE_MESSAGE = new Route(Method.DELETE, "/channels/{channel_id}/messages/{message_id}");
    /** PUT request to add a reaction to a message. */
    public static final Route ADD_REACTION = new Route(Method.PUT, "/channels/{channel_id}/messages/{message_id}/reactions/{emoji}/@me");
    /** DELETE request to remove a reaction from a message. */
    public static final Route REMOVE_REACTION = new Route(Method.DELETE, "/channels/{channel_id}/messages/{message_id}/reactions/{emoji}/@me");

    // --- Guild & Member Routes ---
    /** GET request to retrieve guild information. */
    public static final Route GET_GUILD = new Route(Method.GET, "/guilds/{guild_id}");
    /** POST request to create a new guild. */
    public static final Route CREATE_GUILD = new Route(Method.POST, "/guilds");
    /** DELETE request to kick a member from a guild. */
    public static final Route KICK_MEMBER = new Route(Method.DELETE, "/guilds/{guild_id}/members/{user_id}");
    /** PUT request to assign a role to a guild member. */
    public static final Route ADD_ROLE = new Route(Method.PUT, "/guilds/{guild_id}/members/{user_id}/roles/{role_id}");
    /** DELETE request to remove a role from a guild member. */
    public static final Route REMOVE_ROLE = new Route(Method.DELETE, "/guilds/{guild_id}/members/{user_id}/roles/{role_id}");
    /** PATCH request to modify a guild member (e.g., Timeout, Nickname). */
    public static final Route MODIFY_MEMBER = new Route(Method.PATCH, "/guilds/{guild_id}/members/{user_id}");
    /** PUT request to ban a user from a guild. */
    public static final Route BAN_MEMBER = new Route(Method.PUT, "/guilds/{guild_id}/bans/{user_id}");
    /** DELETE request to unban a user from a guild. */
    public static final Route UNBAN_MEMBER = new Route(Method.DELETE, "/guilds/{guild_id}/bans/{user_id}");

    // --- User Routes ---
    /** GET request to retrieve a detailed user profile. */
    public static final Route GET_USER_PROFILE = new Route(Method.GET, "/users/{user_id}/profile");
    /** GET request to retrieve information about the current bot user. */
    public static final Route GET_ME = new Route(Method.GET, "/users/@me");


    private final Method method;
    private final String path;

    /**
     * Internal constructor for a new Route.
     *
     * @param method The HTTP method for the request.
     * @param path   The relative path of the endpoint (with placeholders).
     */
    public Route(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    /**
     * Compiles the route by replacing placeholders (e.g., {guild_id}) with actual values.
     * <p>
     * Arguments must be provided in the same order as they appear in the path.
     *
     * @param args The values to inject into the path.
     * @return A {@link CompiledRoute} containing the final URL and method.
     */
    public CompiledRoute compile(String... args) {
        String compiledPath = path;
        for (String arg : args) {
            compiledPath = compiledPath.replaceFirst("\\{[a-z_]+\\}", arg);
        }
        return new CompiledRoute(this.method, Constants.API_BASE + compiledPath);
    }

    /**
     * Supported HTTP methods for Fluxer API requests.
     */
    public enum Method {
        GET, POST, DELETE, PUT, PATCH
    }

    /**
     * A helper class that stores the result of a compiled route.
     */
    public static class CompiledRoute {
        /** The HTTP method to be used. */
        public final Method method;
        /** The fully qualified URL (Base + Path). */
        public final String url;

        /**
         * Constructor for a CompiledRoute.
         *
         * @param method The HTTP method.
         * @param url    The final destination URL.
         */
        public CompiledRoute(Method method, String url) {
            this.method = method;
            this.url = url;
        }
    }
}