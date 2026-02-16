package com.j4fluxer.events.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class ReadyEvent extends Event {
    private final String username;
    private final String userId;

    public ReadyEvent(Fluxer api, JsonNode data) {
        super(api);
        JsonNode user = data.get("user");
        this.username = user.get("username").asText();
        this.userId = user.get("id").asText();
    }

    public String getUsername() { return username; }
    public String getUserId() { return userId; }
}