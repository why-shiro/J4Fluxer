package com.j4fluxer.entities.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.PrivateChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The concrete implementation of a {@link Member} on the Fluxer platform.
 */
public class MemberImpl implements Member {

    private final User user;
    private final List<String> roleIds;
    private final Guild guild;
    private final Requester requester;

    /**
     * Constructs a {@code MemberImpl} with full API access capabilities.
     *
     * @param user       The global {@link User} object.
     * @param memberJson The {@link JsonNode} containing member payload.
     * @param guild      The {@link Guild} instance.
     * @param requester  The {@link Requester} instance.
     */
    public MemberImpl(User user, JsonNode memberJson, Guild guild, Requester requester) {
        this.user = user;
        this.guild = guild;
        this.requester = requester;
        this.roleIds = new ArrayList<>();

        if (memberJson.has("roles") && memberJson.get("roles").isArray()) {
            for (JsonNode roleNode : memberJson.get("roles")) {
                roleIds.add(roleNode.asText());
            }
        }
    }

    /**
     * Legacy constructor for creating partial Member objects (No API Access).
     * @param user The user object.
     * @param memberJson The member JSON data.
     */
    public MemberImpl(User user, JsonNode memberJson) {
        this(user, memberJson, null, null);
    }

    @Override public User getUser() { return user; }
    @Override public List<String> getRoleIds() { return Collections.unmodifiableList(roleIds); }
    @Override public Guild getGuild() { return guild; }

    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        return user.openPrivateChannel();
    }

    // --- HELPER FOR GUILD CHECK ---
    private void checkContext() {
        if (guild == null) throw new IllegalStateException("Cannot perform action: Guild context is missing.");
    }

    private void checkApi() {
        if (requester == null) throw new IllegalStateException("Cannot perform action: API context (Requester) is missing.");
    }

    // --- ROLE MANAGEMENT ---

    @Override
    public RestAction<Void> addRole(String roleId) {
        checkContext();
        return guild.addRoleToMember(user.getId(), roleId);
    }

    @Override
    public RestAction<Void> removeRole(String roleId) {
        checkContext();
        return guild.removeRoleFromMember(user.getId(), roleId);
    }

    @Override
    public RestAction<Void> modifyRoles(List<String> newRoleIds) {
        checkContext(); checkApi();
        Route.CompiledRoute route = Route.MODIFY_MEMBER.compile(guild.getId(), user.getId());
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(new RoleUpdatePayload(newRoleIds));
    }

    // --- MODERATION ---

    @Override
    public RestAction<Void> kick() {
        checkContext();
        return guild.kickMember(user.getId());
    }

    @Override
    public RestAction<Void> ban(String reason) {
        checkContext();
        return guild.banMember(user.getId(), reason);
    }

    @Override
    public RestAction<Void> ban(int deleteMessageDays, long durationSeconds, String reason) {
        checkContext();
        return guild.banMember(user.getId(), deleteMessageDays, durationSeconds, reason);
    }

    @Override
    public RestAction<Void> timeout(long durationSeconds) {
        checkContext();
        return guild.timeoutMember(user.getId(), durationSeconds);
    }

    @Override
    public RestAction<Void> removeTimeout() {
        checkContext();
        return guild.removeTimeout(user.getId());
    }

    @Override
    public RestAction<Void> modifyNickname(String nickname) {
        checkContext(); checkApi();
        Route.CompiledRoute route = Route.MODIFY_MEMBER.compile(guild.getId(), user.getId());

        Map<String, String> body = new HashMap<>();
        body.put("nick", nickname);

        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        }.setBody(body);
    }

    // --- DTOs ---
    private static class RoleUpdatePayload {
        public List<String> roles;
        public RoleUpdatePayload(List<String> roles) { this.roles = roles; }
    }
}