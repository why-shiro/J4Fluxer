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



    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public String getOwnerId() { return ownerId; }

    @Override
    public TextChannel getTextChannelById(String channelId) {
        return new TextChannelImpl(channelId, this, requester);
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
    public RestAction<Void> timeoutMember(String userId, long durationSeconds) {
        String isoTime = Instant.now().plus(durationSeconds, ChronoUnit.SECONDS).toString();

        Route.CompiledRoute route = Route.MODIFY_MEMBER.compile(this.id, userId);

        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) { return null; }
        }.setBody(new TimeoutPayload(isoTime));
    }

    @Override
    public RestAction<Void> removeTimeout(String userId) {
        Route.CompiledRoute route = Route.MODIFY_MEMBER.compile(this.id, userId);

        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) { return null; }
        }.setBody(new TimeoutPayload(null));
    }

    private static class TimeoutPayload {
        public String communication_disabled_until;

        public TimeoutPayload(String timestamp) {
            this.communication_disabled_until = timestamp;
        }
    }

    @Override
    public RestAction<Void> addRoleToMember(String userId, String roleId) {
        Route.CompiledRoute route = Route.ADD_ROLE.compile(this.id, userId, roleId);
        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) {
                return null;
            }
        };
    }

    @Override
    public RestAction<Void> removeRoleFromMember(String userId, String roleId) {
        Route.CompiledRoute route = Route.REMOVE_ROLE.compile(this.id, userId, roleId);

        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) {
                return null;
            }
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


    @Override
    public RestAction<Void> kickMember(String userId) {
        Route.CompiledRoute route = Route.KICK_MEMBER.compile(this.id, userId);

        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) {
                return null;
            }
        };
    }

    @Override
    public RestAction<TextChannel> createTextChannel(String name) {
        return createChannel(name, ChannelType.TEXT, TextChannel.class);
    }

    @Override
    public RestAction<VoiceChannel> createVoiceChannel(String name) {
        return createChannel(name, ChannelType.VOICE, VoiceChannel.class);
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
            @Override
            protected Void handleResponse(String json) {
                return null;
            }
        }.setBody(payload);
    }

    @Override
    public RestAction<Void> unbanMember(String userId) {
        Route.CompiledRoute route = Route.UNBAN_MEMBER.compile(this.id, userId);

        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) {
                return null;
            }
        };
    }

    private static class BanPayload {
        public int delete_message_days;    // Kaç günlük mesaj silinsin?
        public String reason;              // Sebep
        public long ban_duration_seconds;  // Ne kadar süre banlı kalsın? (0 = Sonsuz)

        public BanPayload(int deleteMessageDays, long durationSeconds, String reason) {
            this.delete_message_days = deleteMessageDays;
            this.reason = reason;
            this.ban_duration_seconds = durationSeconds;
        }
    }

    private <T extends Channel> RestAction<T> createChannel(String name, ChannelType type, Class<T> clazz) {
        Route.CompiledRoute route = Route.CREATE_CHANNEL.compile(this.id);
        return new RestAction<T>(requester, route) {
            @Override
            protected T handleResponse(String jsonStr) throws Exception {
                JsonNode node = mapper.readTree(jsonStr);
                EntityBuilder builder = new EntityBuilder(requester);
                return clazz.cast(builder.createChannel(node, GuildImpl.this));
            }
        }.setBody(new ChannelCreatePayload(name, type.getKey()));
    }

    private static class ChannelCreatePayload {
        public String name; public int type;
        public ChannelCreatePayload(String name, int type) { this.name = name; this.type = type; }
    }
}