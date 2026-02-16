package com.j4fluxer.entities.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.member.MemberImpl;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.entities.user.UserImpl;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageImpl implements Message {
    private final Requester requester;
    private final String id;
    private final String content;
    private final String channelId;
    private final String guildId;
    private final User author;
    private final Member member;
    private final List<User> mentions;

    public MessageImpl(JsonNode json, Requester requester) {
        this.requester = requester;
        this.id = json.get("id").asText();
        this.content = json.has("content") ? json.get("content").asText() : "";
        this.channelId = json.get("channel_id").asText();

        this.guildId = json.has("guild_id") && !json.get("guild_id").isNull()
                ? json.get("guild_id").asText() : null;

        if (json.has("author") && json.get("author").isObject()) {
            this.author = new UserImpl(json.get("author"));
        } else {
            this.author = null;
        }

        if (this.author != null && json.has("member")) {
            this.member = new MemberImpl(this.author, json.get("member"));
        } else {
            this.member = null;
        }

        this.mentions = new ArrayList<>();
        if (json.has("mentions") && json.get("mentions").isArray()) {
            for (JsonNode mentionNode : json.get("mentions")) {
                this.mentions.add(new UserImpl(mentionNode));
            }
        }
    }


    @Override public String getId() { return id; }
    @Override public String getContent() { return content; }
    @Override public String getChannelId() { return channelId; }
    @Override public String getGuildId() { return guildId; }

    @Override public User getAuthor() { return author; }
    public String getAuthorId() { return author != null ? author.getId() : null; }

    @Override public Member getMember() { return member; }

    @Override
    public List<User> getMentions() {
        return Collections.unmodifiableList(mentions);
    }


    @Override
    public RestAction<Message> edit(String newContent) {
        Route.CompiledRoute route = Route.EDIT_MESSAGE.compile(this.channelId, this.id);

        return new RestAction<Message>(requester, route) {
            @Override
            protected Message handleResponse(String jsonStr) throws Exception {
                return new MessageImpl(mapper.readTree(jsonStr), requester);
            }
        }.setBody(new EditPayload(newContent));
    }

    @Override
    public RestAction<Void> delete() {
        Route.CompiledRoute route = Route.DELETE_MESSAGE.compile(this.channelId, this.id);

        return new RestAction<Void>(requester, route) {
            @Override
            protected Void handleResponse(String json) {
                return null; // 204 No Content
            }
        };
    }

    private static class EditPayload {
        public String content;
        public EditPayload(String content) { this.content = content; }
    }
}