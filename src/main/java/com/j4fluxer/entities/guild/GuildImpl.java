package com.j4fluxer.entities.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.*;
import com.j4fluxer.entities.user.UserProfile;
import com.j4fluxer.internal.json.EntityBuilder;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuildImpl implements Guild {
    private final Requester requester;
    private final String id;
    private final String name;
    private final String ownerId;
    private final Map<String, Role> roles = new HashMap<>();

    public GuildImpl(JsonNode json, Requester requester) {
        this.requester = requester;
        this.id = json.get("id").asText();
        this.name = json.has("name") ? json.get("name").asText() : "";
        this.ownerId = json.has("owner_id") && !json.get("owner_id").isNull()
                ? json.get("owner_id").asText() : null;
        if (json.has("roles") && json.get("roles").isArray()) {
            for (JsonNode roleNode : json.get("roles")) {
                Role role = new Role(roleNode);
                this.roles.put(role.getId(), role);
            }
        }
    }

    public GuildImpl(String id, Requester requester) {
        this.requester = requester;
        this.id = id;
        this.name = "";
        this.ownerId = null;
    }

    // --- CHANNEL RETRIEVAL ---

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public String getOwnerId() { return ownerId; }

    @Override
    public GuildChannel getGuildChannelById(String id) {
        // In the future, this should check cache and return the specific type (Text/Voice/Category)
        // For now, returning a TextChannelImpl as a generic GuildChannel wrapper is safe for ID operations.
        return new TextChannelImpl(id, this, requester);
    }

    @Override
    public TextChannel getTextChannelById(String channelId) {
        return new TextChannelImpl(channelId, this, requester);
    }

    @Override
    public Category getCategoryById(String id) {
        return new CategoryImpl(id, this, requester);
    }

    @Override
    public RestAction<List<Channel>> retrieveChannels() {
        Route.CompiledRoute route = Route.GET_GUILD_CHANNELS.compile(this.id);
        return new RestAction<List<Channel>>(requester, route) {
            @Override
            protected List<Channel> handleResponse(String jsonStr) throws Exception {
                JsonNode array = mapper.readTree(jsonStr);
                List<Channel> channels = new ArrayList<>();
                EntityBuilder builder = new EntityBuilder(requester);
                if (array.isArray()) {
                    for (JsonNode node : array) {
                        channels.add(builder.createChannel(node, GuildImpl.this));
                    }
                }
                return channels;
            }
        };
    }

    @Override
    public RestAction<Category> createCategory(String name) {
        return createChannel(name, ChannelType.CATEGORY, null, Category.class);
    }

    @Override
    public RestAction<TextChannel> createTextChannel(String name) {
        return createChannel(name, ChannelType.TEXT, null, TextChannel.class);
    }

    @Override
    public RestAction<TextChannel> createTextChannel(String name, String parentId) {
        return createChannel(name, ChannelType.TEXT, parentId, TextChannel.class);
    }

    @Override
    public RestAction<VoiceChannel> createVoiceChannel(String name) {
        return createChannel(name, ChannelType.VOICE, null, VoiceChannel.class);
    }

    @Override
    public RestAction<VoiceChannel> createVoiceChannel(String name, String parentId) {
        return createChannel(name, ChannelType.VOICE, parentId, VoiceChannel.class);
    }

    private <T extends Channel> RestAction<T> createChannel(String name, ChannelType type, String parentId, Class<T> clazz) {
        Route.CompiledRoute route = Route.CREATE_CHANNEL.compile(this.id);
        ChannelCreatePayload payload = new ChannelCreatePayload(name, type.getKey(), parentId);

        return new RestAction<T>(requester, route) {
            @Override
            protected T handleResponse(String jsonStr) throws Exception {
                JsonNode node = mapper.readTree(jsonStr);
                EntityBuilder builder = new EntityBuilder(requester);
                return clazz.cast(builder.createChannel(node, GuildImpl.this));
            }
        }.setBody(payload);
    }

    @Override
    public RestAction<UserProfile> retrieveMemberProfile(String userId) {
        Route.CompiledRoute baseRoute = Route.GET_USER_PROFILE.compile(userId);
        String fullUrl = baseRoute.url + "?guild_id=" + this.id;
        Route.CompiledRoute finalRoute = new Route.CompiledRoute(baseRoute.method, fullUrl);

        return new RestAction<UserProfile>(requester, finalRoute) {
            @Override
            protected UserProfile handleResponse(String jsonStr) throws Exception {
                return new UserProfile(mapper.readTree(jsonStr));
            }
        };
    }

    @Override
    public RestAction<Void> kickMember(String userId) {
        Route.CompiledRoute route = Route.KICK_MEMBER.compile(this.id, userId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    @Override
    public RestAction<Void> banMember(String userId, String reason) {
        return banMember(userId, 0, 0, reason);
    }

    @Override
    public RestAction<Void> banMember(String userId, int deleteMessageDays, long durationSeconds, String reason) {
        Route.CompiledRoute route = Route.BAN_MEMBER.compile(this.id, userId);
        BanPayload payload = new BanPayload(deleteMessageDays, durationSeconds, reason);

        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(payload);
    }

    @Override
    public RestAction<Void> unbanMember(String userId) {
        Route.CompiledRoute route = Route.UNBAN_MEMBER.compile(this.id, userId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    @Override
    public RestAction<Void> timeoutMember(String userId, long durationSeconds) {
        String isoTime = Instant.now().plus(durationSeconds, ChronoUnit.SECONDS).toString();
        Route.CompiledRoute route = Route.MODIFY_MEMBER.compile(this.id, userId);

        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(new TimeoutPayload(isoTime));
    }

    @Override
    public RestAction<Void> removeTimeout(String userId) {
        Route.CompiledRoute route = Route.MODIFY_MEMBER.compile(this.id, userId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(new TimeoutPayload(null));
    }

    @Override
    public RestAction<Void> addRoleToMember(String userId, String roleId) {
        Route.CompiledRoute route = Route.ADD_ROLE.compile(this.id, userId, roleId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    @Override
    public RestAction<Void> removeRoleFromMember(String userId, String roleId) {
        Route.CompiledRoute route = Route.REMOVE_ROLE.compile(this.id, userId, roleId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    @Override
    public Role getRoleById(String id) {
        return roles.get(id);
    }

    @Override
    public List<Role> getRoles() {
        return new ArrayList<>(roles.values());
    }

    private RestAction<Void> modifyGuild(String key, Object value) {
        Route.CompiledRoute route = Route.MODIFY_GUILD.compile(this.id);
        Map<String, Object> body = new HashMap<>();
        body.put(key, value);

        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(body);
    }

    @Override public RestAction<Void> setName(String name) { return modifyGuild("name", name); }
    @Override public RestAction<Void> setAfkChannelId(String channelId) { return modifyGuild("afk_channel_id", channelId); }
    @Override public RestAction<Void> setAfkTimeout(int seconds) { return modifyGuild("afk_timeout", seconds); }
    @Override public RestAction<Void> setSystemChannelId(String channelId) { return modifyGuild("system_channel_id", channelId); }
    @Override public RestAction<Void> setDefaultNotificationLevel(int level) { return modifyGuild("default_message_notifications", level); }

    private static class TimeoutPayload {
        public String communication_disabled_until;
        public TimeoutPayload(String timestamp) { this.communication_disabled_until = timestamp; }
    }

    private static class BanPayload {
        public int delete_message_days;
        public String reason;
        public long ban_duration_seconds;
        public BanPayload(int deleteMessageDays, long durationSeconds, String reason) {
            this.delete_message_days = deleteMessageDays;
            this.reason = reason;
            this.ban_duration_seconds = durationSeconds;
        }
    }

    private static class ChannelCreatePayload {
        public String name;
        public int type;
        public String parent_id;
        public ChannelCreatePayload(String name, int type, String parent_id) {
            this.name = name;
            this.type = type;
            this.parent_id = parent_id;
        }
    }
}