package com.j4fluxer.events.message;
import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import java.util.ArrayList;
import java.util.List;

public class MessageBulkDeleteEvent extends Event {
    private final List<String> messageIds = new ArrayList<>();
    private final String channelId;
    public MessageBulkDeleteEvent(Fluxer api, JsonNode data) {
        super(api);
        this.channelId = data.get("channel_id").asText();
        if (data.has("ids")) {
            for (JsonNode idNode : data.get("ids")) {
                messageIds.add(idNode.asText());
            }
        }
    }
    public List<String> getMessageIds() { return messageIds; }
}