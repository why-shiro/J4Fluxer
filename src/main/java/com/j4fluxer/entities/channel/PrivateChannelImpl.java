package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.message.MessageImpl;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.entities.user.UserImpl;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

public class PrivateChannelImpl implements PrivateChannel {
    private final String id;
    private final User user; // Karşıdaki kişi
    private final Requester requester;

    public PrivateChannelImpl(JsonNode json, Requester requester) {
        this.requester = requester;
        this.id = json.get("id").asText();

        // DM kanal bilgisinde "recipients" listesi olur.
        // Bot kendisi hariç diğer kişiyi bulmalı.
        if (json.has("recipients") && json.get("recipients").isArray()) {
            JsonNode recipients = json.get("recipients");
            if (recipients.size() > 0) {
                // Genelde ilk kişi hedeftir
                this.user = new UserImpl(recipients.get(0));
            } else {
                this.user = null;
            }
        } else {
            this.user = null;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return user != null ? user.getUsername() : "Unknown";
    }

    @Override
    public ChannelType getType() {
        return ChannelType.DM;
    }

    @Override
    public RestAction<Void> delete() {
        //TODO
        return null;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public RestAction<Message> sendMessage(String content) {
        Route.CompiledRoute route = Route.SEND_MESSAGE.compile(this.id);
        return new RestAction<Message>(requester, route) {
            @Override
            protected Message handleResponse(String jsonStr) throws Exception {
                return new MessageImpl(mapper.readTree(jsonStr), requester);
            }
        }.setBody(new MessagePayload(content));
    }

    private static class MessagePayload {
        public String content;
        public MessagePayload(String content) { this.content = content; }
    }
}