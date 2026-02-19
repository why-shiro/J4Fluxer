package com.j4fluxer.entities.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.GuildChannel;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.VoiceChannel;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.guild.GuildImpl; // Minimal Guild oluşturmak için lazım
import com.j4fluxer.entities.guild.Role;
import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.member.MemberImpl;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.entities.user.UserImpl;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageImpl implements Message {
    private final Requester requester;
    private final String id;
    private final String content;
    private final String channelId;
    private final String guildId;
    private final User author;
    private final Member member;
    private final List<User> mentions;
    private final List<String> mentionedRoleIds;
    private final boolean pinned;

    private final Message referencedMessage;

    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(\\d+)>");

    public MessageImpl(JsonNode json, Requester requester) {
        this.requester = requester;
        this.id = json.get("id").asText();
        this.content = json.has("content") ? json.get("content").asText() : "";
        this.channelId = json.get("channel_id").asText();
        this.pinned = json.has("pinned") && json.get("pinned").asBoolean();
        this.guildId = json.has("guild_id") && !json.get("guild_id").isNull()
                ? json.get("guild_id").asText() : null;

        if (json.has("author") && json.get("author").isObject()) {
            this.author = new UserImpl(json.get("author"), requester);
        } else {
            this.author = null;
        }

        Guild guildContext = null;
        if (this.guildId != null) {
            guildContext = requester.getApi().getGuildById(this.guildId);
            if (guildContext == null) {
                // Cache'de yoksa, işlem yapabilmek için (kick/ban vb.) sadece ID içeren minimal bir Guild oluşturuyoruz.
                guildContext = new GuildImpl(this.guildId, requester);
            }
        }

        if (this.author != null && json.has("member")) {
            this.member = new MemberImpl(this.author, json.get("member"), guildContext, requester);
        } else {
            this.member = null;
        }

        this.mentions = new ArrayList<>();
        if (json.has("mentions") && json.get("mentions").isArray()) {
            for (JsonNode mentionNode : json.get("mentions")) {
                this.mentions.add(new UserImpl(mentionNode, requester));
            }
        }

        this.mentionedRoleIds = new ArrayList<>();
        if (json.has("mention_roles") && json.get("mention_roles").isArray()) {
            for (JsonNode roleIdNode : json.get("mention_roles")) {
                this.mentionedRoleIds.add(roleIdNode.asText());
            }
        }

        if (json.has("referenced_message") && !json.get("referenced_message").isNull()) {
            this.referencedMessage = new MessageImpl(json.get("referenced_message"), requester);
        } else {
            this.referencedMessage = null;
        }
    }

    @Override public String getId() { return id; }
    @Override public String getContent() { return content; }
    @Override public String getChannelId() { return channelId; }
    @Override public String getGuildId() { return guildId; }
    @Override public User getAuthor() { return author; }
    @Override public Member getMember() { return member; }

    @Override public Message getReferencedMessage() { return referencedMessage; }

    @Override
    public List<User> getMentions() {
        return Collections.unmodifiableList(mentions);
    }

    @Override
    public List<Role> getMentionedRoles() {
        Guild guild = getGuild();
        if (guild == null || mentionedRoleIds.isEmpty()) return Collections.emptyList();

        List<Role> roles = new ArrayList<>();
        for (String rId : mentionedRoleIds) {
            Role role = guild.getRoleById(rId);
            if (role != null) roles.add(role);
        }
        return Collections.unmodifiableList(roles);
    }

    @Override
    public List<GuildChannel> getMentionedChannels() {
        Guild guild = getGuild();
        if (guild == null) return Collections.emptyList();

        List<GuildChannel> channels = new ArrayList<>();
        Matcher matcher = CHANNEL_MENTION_PATTERN.matcher(this.content);

        while (matcher.find()) {
            String cId = matcher.group(1);
            GuildChannel channel = guild.getGuildChannelById(cId);
            if (channel != null) {
                channels.add(channel);
            }
        }
        return Collections.unmodifiableList(channels);
    }

    @Override
    public List<TextChannel> getMentionedTextChannels() {
        return getMentionedChannels().stream()
                .filter(c -> c instanceof TextChannel)
                .map(c -> (TextChannel) c)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPinned() {
        return pinned;
    }

    @Override
    public RestAction<Void> pin() {
        Route.CompiledRoute route = Route.PIN_MESSAGE.compile(this.channelId, this.id);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    @Override
    public RestAction<Void> unpin() {
        Route.CompiledRoute route = Route.UNPIN_MESSAGE.compile(this.channelId, this.id);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    @Override
    public List<VoiceChannel> getMentionedVoiceChannels() {
        return getMentionedChannels().stream()
                .filter(c -> c instanceof VoiceChannel)
                .map(c -> (VoiceChannel) c)
                .collect(Collectors.toList());
    }

    @Override
    public RestAction<Message> reply(String content) {
        return reply(content, true); // Varsayılan olarak etiketle
    }

    @Override
    public RestAction<Message> reply(String content, boolean mentionRepliedUser) {
        Route.CompiledRoute route = Route.SEND_MESSAGE.compile(this.channelId);

        ReplyPayload payload = new ReplyPayload(content, this.id, mentionRepliedUser);

        return new RestAction<Message>(requester, route) {
            @Override
            protected Message handleResponse(String responseJson) throws Exception {
                return new MessageImpl(mapper.readTree(responseJson), requester);
            }
        }.setBody(payload);
    }

    @Override
    public RestAction<Void> addReaction(String emoji) {
        return executeReaction(emoji, Route.ADD_REACTION);
    }

    @Override
    public RestAction<Void> removeReaction(String emoji) {
        return executeReaction(emoji, Route.REMOVE_REACTION);
    }

    @Override
    public RestAction<Void> removeReaction(User user, String emoji) {
        return removeReaction(user.getId(), emoji);
    }

    @Override
    public RestAction<Void> removeReaction(String userId, String emoji) {
        String encodedEmoji;
        try {
            encodedEmoji = URLEncoder.encode(emoji, StandardCharsets.UTF_8);
        } catch (Exception e) {
            encodedEmoji = emoji;
        }
        Route.CompiledRoute route = Route.REMOVE_REACTION_USER.compile(this.channelId, this.id, encodedEmoji, userId);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    private RestAction<Void> executeReaction(String emoji, Route routeType) {
        String encodedEmoji;
        try {
            encodedEmoji = URLEncoder.encode(emoji, StandardCharsets.UTF_8);
        } catch (Exception e) {
            encodedEmoji = emoji;
        }
        Route.CompiledRoute route = routeType.compile(this.channelId, this.id, encodedEmoji);
        return new RestAction<Void>(requester, route) {
            @Override protected Void handleResponse(String json) { return null; }
        };
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
            @Override protected Void handleResponse(String json) { return null; }
        };
    }

    private Guild getGuild() {
        if (this.guildId == null) return null;
        return requester.getApi().getGuildById(this.guildId);
    }

    // --- PAYLOAD ---

    private static class EditPayload {
        public String content;
        public EditPayload(String content) { this.content = content; }
    }

    private static class ReplyPayload {
        public String content;
        public MessageReference message_reference;
        public AllowedMentions allowed_mentions;

        public ReplyPayload(String content, String messageId, boolean mentionRepliedUser) {
            this.content = content;
            this.message_reference = new MessageReference(messageId);
            this.allowed_mentions = new AllowedMentions(mentionRepliedUser);
        }

        private static class MessageReference {
            public String message_id;
            public MessageReference(String message_id) { this.message_id = message_id; }
        }

        private static class AllowedMentions {
            public boolean replied_user;
            public AllowedMentions(boolean replied_user) { this.replied_user = replied_user; }
        }
    }
}