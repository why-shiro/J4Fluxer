package com.j4fluxer.events.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

/**
 * Fired when a user starts typing in a text channel.
 */
public class TypingStartEvent extends Event {
    private final String userId;
    private final String channelId;

    public TypingStartEvent(Fluxer api, JsonNode data) {
        super(api);
        this.userId = data.get("user_id").asText();
        this.channelId = data.get("channel_id").asText();
    }

    /** @return The ID of the user who is typing. */
    public String getUserId() { return userId; }

    /** @return The ID of the channel where the typing is occurring. */
    public String getChannelId() { return channelId; }
}