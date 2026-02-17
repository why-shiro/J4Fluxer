package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.Permission;
import com.j4fluxer.entities.PermissionOverwrite;
import com.j4fluxer.entities.channel.GuildChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class AbstractChannel implements GuildChannel {
    protected final String id;
    protected final String name;
    protected final Guild guild;
    protected final Requester requester;
    protected final JsonNode json;

    protected final List<PermissionOverwrite> permissionOverwrites;

    public AbstractChannel(JsonNode json, Guild guild, Requester requester) {
        this.json = json;
        this.id = json.get("id").asText();
        this.name = json.get("name").asText();
        this.guild = guild;
        this.requester = requester;

        this.permissionOverwrites = new ArrayList<>();
        if (json.has("permission_overwrites") && json.get("permission_overwrites").isArray()) {
            for (JsonNode node : json.get("permission_overwrites")) {
                this.permissionOverwrites.add(new PermissionOverwrite(node));
            }
        }
    }

    public AbstractChannel(String id, Guild guild, Requester requester) {
        this.id = id;
        this.guild = guild;
        this.requester = requester;
        this.json = null;
        this.name = "";
        this.permissionOverwrites = new ArrayList<>();
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public Guild getGuild() { return guild; }

    @Override
    public List<PermissionOverwrite> getPermissionOverwrites() {
        return Collections.unmodifiableList(permissionOverwrites);
    }

    @Override
    public String getParentId() {
        if (json == null) return null;
        return json.has("parent_id") && !json.get("parent_id").isNull()
                ? json.get("parent_id").asText() : null;
    }

    @Override
    public int getPosition() {
        if (json == null) return 0;
        return json.has("position") ? json.get("position").asInt() : 0;
    }


    @Override
    public RestAction<Void> delete() {
        Route.CompiledRoute route = Route.DELETE_CHANNEL.compile(this.id);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    @Override
    public RestAction<Void> upsertPermissionOverride(String targetId, int type, EnumSet<Permission> allow, EnumSet<Permission> deny) {
        Route.CompiledRoute route = Route.MANAGE_PERMISSION.compile(this.id, targetId);

        long allowRaw = (allow != null) ? Permission.getRaw(allow) : 0;
        long denyRaw = (deny != null) ? Permission.getRaw(deny) : 0;

        PermissionOverridePayload payload = new PermissionOverridePayload(
                type,
                String.valueOf(allowRaw),
                String.valueOf(denyRaw)
        );

        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) {
                return null;
            }
        }.setBody(payload);
    }

    private static class PermissionOverridePayload {
        public int type;
        public String allow;
        public String deny;

        public PermissionOverridePayload(int type, String allow, String deny) {
            this.type = type;
            this.allow = allow;
            this.deny = deny;
        }
    }

    @Override
    public RestAction<Void> deletePermissionOverride(String targetId) {
        Route.CompiledRoute route = Route.DELETE_PERMISSION.compile(this.id, targetId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    protected RestAction<Void> modifyChannel(String key, Object value) {
        Route.CompiledRoute route = Route.MODIFY_CHANNEL.compile(this.id);
        Map<String, Object> body = new HashMap<>();
        body.put(key, value);

        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(body);
    }

    @Override public RestAction<Void> setName(String name) { return modifyChannel("name", name); }
    @Override public RestAction<Void> setPosition(int position) { return modifyChannel("position", position); }
    @Override public RestAction<Void> setParent(String categoryId) { return modifyChannel("parent_id", categoryId); }

    @Override
    public RestAction<String> createInvite() {
        Route.CompiledRoute route = Route.CREATE_INVITE.compile(this.id);
        return new RestAction<String>(requester, route) {
            @Override
            protected String handleResponse(String json) throws Exception {
                return mapper.readTree(json).get("code").asText();
            }
        }.setBody(Map.of("max_age", 0, "max_uses", 0));
    }

    private static class PermissionPayload {
        public int type; public String allow; public String deny;
        public PermissionPayload(int type, String allow, String deny) {
            this.type = type; this.allow = allow; this.deny = deny;
        }
    }
}