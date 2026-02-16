package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.Permission;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

import java.util.EnumSet;

public abstract class AbstractChannel implements GuildChannel {
    protected final String id;
    protected final String name;
    protected final Guild guild;
    protected final Requester requester;
    protected final JsonNode json;

    public AbstractChannel(JsonNode json, Guild guild, Requester requester) {
        this.json = json;
        this.id = json.get("id").asText();
        this.name = json.get("name").asText();
        this.guild = guild;
        this.requester = requester;
    }

    public AbstractChannel(String id, Guild guild, Requester requester) {
        this.id = id;
        this.guild = guild;
        this.requester = requester;
        this.json = null;
        this.name = "";
    }

    @Override public String getId() { return id; }
    @Override public String getName() { return name; }
    @Override public Guild getGuild() { return guild; }

    @Override
    public String getParentId() {
        if (json == null) return null;
        return json.has("parent_id") && !json.get("parent_id").isNull() ? json.get("parent_id").asText() : null;
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
        long allowRaw = Permission.getRaw(allow);
        long denyRaw = Permission.getRaw(deny);
        PermissionPayload payload = new PermissionPayload(type, String.valueOf(allowRaw), String.valueOf(denyRaw));
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(payload);
    }

    @Override
    public RestAction<Void> deletePermissionOverride(String targetId) {
        Route.CompiledRoute route = Route.DELETE_PERMISSION.compile(this.id, targetId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    private static class PermissionPayload {
        public int type; public String allow; public String deny;
        public PermissionPayload(int type, String allow, String deny) {
            this.type = type; this.allow = allow; this.deny = deny;
        }
    }
}