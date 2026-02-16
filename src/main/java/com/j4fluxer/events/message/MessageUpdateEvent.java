package com.j4fluxer.events.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;

public class MessageUpdateEvent extends Event {
    private final String messageId;
    private final String channelId;
    private final String content;

    public MessageUpdateEvent(Fluxer api, JsonNode data) {
        super(api);
        this.messageId = data.get("id").asText();
        this.channelId = data.get("channel_id").asText();
        this.content = data.has("content") ? data.get("content").asText() : null;
    }

    public String getMessageId() { return messageId; }
    public String getChannelId() { return channelId; }
    public String getContent() { return content; }
}