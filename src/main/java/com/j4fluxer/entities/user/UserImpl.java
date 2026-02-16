package com.j4fluxer.entities.user;

import com.fasterxml.jackson.databind.JsonNode;

public class UserImpl implements User {
    private final String id;
    private final String username;
    private final String discriminator;
    private final boolean isBot;

    public UserImpl(JsonNode json) {
        this.id = json.get("id").asText();
        this.username = json.get("username").asText();
        this.discriminator = json.has("discriminator") ? json.get("discriminator").asText() : "0000";

        this.isBot = json.has("bot") && json.get("bot").asBoolean();
    }

    @Override public String getId() { return id; }
    @Override public String getUsername() { return username; }
    @Override public String getDiscriminator() { return discriminator; }

    @Override
    public boolean isBot() { return isBot; }

    @Override
    public String toString() {
        return username + "#" + discriminator;
    }
}