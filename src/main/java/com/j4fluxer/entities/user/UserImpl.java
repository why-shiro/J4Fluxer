package com.j4fluxer.entities.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.PrivateChannel;
import com.j4fluxer.entities.channel.PrivateChannelImpl;
import com.j4fluxer.internal.constants.Constants;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

/**
 * The concrete implementation of a {@link User} on the Fluxer platform.
 */
public class UserImpl implements User {

    private final String id;
    private final String username;
    private final String discriminator;
    private final boolean isBot;
    private final String avatarHash;

    // Optional: Only available if created with a Requester
    private final Requester requester;

    /**
     * Creates a User object from JSON data without API access.
     * (Methods like openPrivateChannel() will fail)
     *
     * @param json The user data JSON.
     */
    public UserImpl(JsonNode json) {
        this(json, null);
    }

    /**
     * Creates a User object with full API access.
     *
     * @param json      The user data JSON.
     * @param requester The API requester instance.
     */
    public UserImpl(JsonNode json, Requester requester) {
        this.id = json.get("id").asText();
        this.username = json.get("username").asText();
        this.discriminator = json.has("discriminator") ? json.get("discriminator").asText() : "0000";
        this.avatarHash = json.has("avatar") && !json.get("avatar").isNull() ? json.get("avatar").asText() : null;
        this.isBot = json.has("bot") && json.get("bot").asBoolean();
        this.requester = requester;
    }

    @Override public String getId() { return id; }
    @Override public String getUsername() { return username; }
    @Override public String getDiscriminator() { return discriminator; }
    @Override public boolean isBot() { return isBot; }
    @Override public String getAvatarHash() { return avatarHash; }

    @Override
    public String getAvatarUrl() {
        if (avatarHash == null) return null;
        // Format: https://fluxerusercontent.com/avatars/USER_ID/HASH.png
        return Constants.CDN_URL + "/avatars/" + id + "/" + avatarHash + ".png";
    }

    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        if (requester == null) {
            throw new IllegalStateException("Cannot open DM: This User object has no API access (Requester is null).");
        }

        Route.CompiledRoute route = Route.CREATE_DM.compile();
        RecipientPayload payload = new RecipientPayload(this.id);

        return new RestAction<PrivateChannel>(requester, route) {
            @Override
            protected PrivateChannel handleResponse(String jsonStr) throws Exception {
                // The API returns the DM channel object
                return new PrivateChannelImpl(mapper.readTree(jsonStr), requester);
            }
        }.setBody(payload);
    }

    @Override
    public String toString() {
        return "User:" + username + "#" + discriminator + "(" + id + ")";
    }

    // --- Internal Payload DTO ---
    private static class RecipientPayload {
        public String recipient_id;
        public RecipientPayload(String id) { this.recipient_id = id; }
    }
}