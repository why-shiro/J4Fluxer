package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.PermissionOverwrite;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.message.MessageImpl;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

import java.util.List;

public class TextChannelImpl extends AbstractChannel implements TextChannel {

    public TextChannelImpl(JsonNode json, Guild guild, Requester requester) {
        super(json, guild, requester);
    }

    public TextChannelImpl(String id, Guild guild, Requester requester) {
        super(id, guild, requester);
    }

    @Override
    public ChannelType getType() {
        return ChannelType.TEXT;
    }

    @Override
    public String getTopic() {
        return (json != null && json.has("topic") && !json.get("topic").isNull())
                ? json.get("topic").asText() : null;
    }

    @Override
    public boolean isNSFW() {
        return json != null && json.has("nsfw") && json.get("nsfw").asBoolean();
    }

    @Override
    public RestAction<Message> sendMessage(String content) {
        Route.CompiledRoute route = Route.SEND_MESSAGE.compile(this.id);

        return new RestAction<Message>(requester, route) {
            @Override
            protected Message handleResponse(String responseJson) throws Exception {
                return new MessageImpl(mapper.readTree(responseJson), requester);
            }
        }.setBody(new MessagePayload(content));
    }

    @Override
    public RestAction<Void> setTopic(String topic) {
        return modifyChannel("topic", topic);
    }

    @Override
    public RestAction<Void> setNSFW(boolean nsfw) {
        return modifyChannel("nsfw", nsfw);
    }

    @Override
    public RestAction<Void> setSlowmode(int seconds) {
        return modifyChannel("rate_limit_per_user", seconds);
    }

    @Override
    public List<PermissionOverwrite> getPermissionOverwrites() {
        return List.of();
    }

    private static class MessagePayload {
        public String content;
        public MessagePayload(String content) { this.content = content; }
    }
}