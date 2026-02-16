package com.j4fluxer.internal.requests;

import com.j4fluxer.internal.constants.Constants;

public class Route {
    public static final Route GET_ME = new Route(Method.GET, "/users/@me");
    public static final Route CREATE_CHANNEL = new Route(Method.POST, "/guilds/{guild_id}/channels");
    public static final Route SEND_MESSAGE = new Route(Method.POST, "/channels/{channel_id}/messages");
    public static final Route EDIT_MESSAGE = new Route(Method.PATCH, "/channels/{channel_id}/messages/{message_id}");
    public static final Route GET_GUILD_CHANNELS = new Route(Method.GET, "/guilds/{guild_id}/channels");
    public static final Route DELETE_CHANNEL = new Route(Method.DELETE, "/channels/{channel_id}");
    public static final Route MANAGE_PERMISSION = new Route(Method.PUT, "/channels/{channel_id}/permissions/{target_id}");
    public static final Route DELETE_PERMISSION = new Route(Method.DELETE, "/channels/{channel_id}/permissions/{target_id}");
    public static final Route DELETE_MESSAGE = new Route(Method.DELETE, "/channels/{channel_id}/messages/{message_id}");
    public static final Route GET_GUILD = new Route(Method.GET, "/guilds/{guild_id}");
    public static final Route CREATE_GUILD = new Route(Method.POST, "/guilds");

    private final Method method;
    private final String path;

    public Route(Method method, String path) {
        this.method = method;
        this.path = path;
    }

    public CompiledRoute compile(String... args) {
        String compiledPath = path;
        for (String arg : args) {
            compiledPath = compiledPath.replaceFirst("\\{[a-z_]+\\}", arg);
        }
        return new CompiledRoute(this.method, Constants.API_BASE + compiledPath);
    }

    public enum Method {
        GET, POST, DELETE, PUT, PATCH
    }

    public static class CompiledRoute {
        public final Method method;
        public final String url;

        public CompiledRoute(Method method, String url) {
            this.method = method;
            this.url = url;
        }
    }
}