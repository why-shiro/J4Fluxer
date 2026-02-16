package com.j4fluxer.events.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class TypingStartEvent extends Event {
    private final String userId;
    private final String channelId;

    public TypingStartEvent(Fluxer api, JsonNode data) {
        super(api);
        this.userId = data.get("user_id").asText();
        this.channelId = data.get("channel_id").asText();
    }

    public String getUserId() { return userId; }
    public String getChannelId() { return channelId; }
}