package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.Permission;
import com.j4fluxer.entities.PermissionOverwrite;
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

/**
 * The foundational implementation for channels within the Fluxer ecosystem.
 *
 * <p>This abstract class provides shared logic for all guild channels, including
 * metadata management (ID, name, position), permission overrides, and
 * common RESTful actions such as deleting channels or creating invites.</p>
 */
public abstract class AbstractChannel implements GuildChannel {

    /** The unique identifier of the channel. */
    protected final String id;

    /** The name of the channel. */
    protected final String name;

    /** The {@link Guild} that this channel belongs to. */
    protected final Guild guild;

    /** The requester used to perform API operations. */
    protected final Requester requester;

    /** The raw JSON data received from the Fluxer API. */
    protected final JsonNode json;

    /** A list of permission overrides specific to this channel. */
    protected final List<PermissionOverwrite> permissionOverwrites;

    /**
     * Constructs a new {@code AbstractChannel} from a JSON payload.
     *
     * @param json      The {@link JsonNode} containing the channel data.
     * @param guild     The guild associated with this channel.
     * @param requester The requester for performing API actions.
     */
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

    /**
     * Constructs a minimal {@code AbstractChannel} with only an ID.
     *
     * @param id        The unique ID of the channel.
     * @param guild     The guild associated with this channel.
     * @param requester The requester for performing API actions.
     */
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

    /**
     * Returns an unmodifiable list of permission overwrites for this channel.
     *
     * @return A {@link List} of {@link PermissionOverwrite} objects.
     */
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

    /**
     * Deletes this channel from the guild.
     *
     * @return A {@link RestAction} that represents the deletion process.
     */
    @Override
    public RestAction<Void> delete() {
        Route.CompiledRoute route = Route.DELETE_CHANNEL.compile(this.id);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    /**
     * Updates or inserts a permission override for a member or role in this channel.
     *
     * @param targetId The ID of the member or role.
     * @param type     The type of target (e.g., 0 for role, 1 for member).
     * @param allow    An {@link EnumSet} of permissions to allow.
     * @param deny     An {@link EnumSet} of permissions to deny.
     * @return A {@link RestAction} representing the API request.
     */
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

    /**
     * Internal DTO for permission override payloads.
     */
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

    /**
     * Removes a permission override for a specific target.
     *
     * @param targetId The ID of the member or role to remove the override for.
     * @return A {@link RestAction} representing the deletion.
     */
    @Override
    public RestAction<Void> deletePermissionOverride(String targetId) {
        Route.CompiledRoute route = Route.DELETE_PERMISSION.compile(this.id, targetId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    /**
     * Internal helper method to modify channel properties via a PATCH request.
     *
     * @param key   The JSON key to update.
     * @param value The new value for the key.
     * @return A {@link RestAction} representing the modification.
     */
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

    /**
     * Creates a new invite code for this channel.
     *
     * @return A {@link RestAction} providing the unique invite code string.
     */
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
}