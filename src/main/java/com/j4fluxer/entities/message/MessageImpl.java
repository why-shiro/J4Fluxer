package com.j4fluxer.entities.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.GuildChannel;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.VoiceChannel;
import com.j4fluxer.entities.guild.Guild;
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

/**
 * The concrete implementation of a {@link Message} on the Fluxer platform.
 *
 * <p>This class manages message data received from the Fluxer API, providing
 * advanced functionality such as:
 * <ul>
 *     <li>Regex-based parsing of channel mentions within the message content.</li>
 *     <li>Resolution of mentioned users, roles, and channels via the internal cache.</li>
 *     <li>URL-safe reaction management for both Unicode and custom emojis.</li>
 *     <li>Asynchronous actions for editing and deleting messages.</li>
 * </ul>
 * </p>
 */
public class MessageImpl implements Message {

    /** The requester used for performing API operations. */
    private final Requester requester;

    /** The unique ID of the message. */
    private final String id;

    /** The raw text content of the message. */
    private final String content;

    /** The ID of the channel where the message was sent. */
    private final String channelId;

    /** The ID of the guild where the message was sent, or null if sent in a private channel. */
    private final String guildId;

    /** The global {@link User} object representing the message author. */
    private final User author;

    /** The guild-specific {@link Member} object representing the message author. */
    private final Member member;

    /** A list of {@link User} objects mentioned in the message. */
    private final List<User> mentions;

    /** A list of raw role IDs mentioned in the message. */
    private final List<String> mentionedRoleIds;

    /** Regex pattern used to identify channel mentions in the format {@code <#ID>}. */
    private static final Pattern CHANNEL_MENTION_PATTERN = Pattern.compile("<#(\\d+)>");

    /**
     * Constructs a {@code MessageImpl} from a Fluxer API JSON payload.
     *
     * @param json      The {@link JsonNode} containing message data.
     * @param requester The {@link Requester} instance for API operations.
     */
    public MessageImpl(JsonNode json, Requester requester) {
        this.requester = requester;
        this.id = json.get("id").asText();
        this.content = json.has("content") ? json.get("content").asText() : "";
        this.channelId = json.get("channel_id").asText();

        this.guildId = json.has("guild_id") && !json.get("guild_id").isNull()
                ? json.get("guild_id").asText() : null;

        // Author Parsing
        if (json.has("author") && json.get("author").isObject()) {
            this.author = new UserImpl(json.get("author"));
        } else {
            this.author = null;
        }

        // Member Parsing (Guild Context)
        if (this.author != null && json.has("member")) {
            this.member = new MemberImpl(this.author, json.get("member"));
        } else {
            this.member = null;
        }

        // User Mentions Parsing
        this.mentions = new ArrayList<>();
        if (json.has("mentions") && json.get("mentions").isArray()) {
            for (JsonNode mentionNode : json.get("mentions")) {
                this.mentions.add(new UserImpl(mentionNode));
            }
        }

        // Role Mentions Parsing
        this.mentionedRoleIds = new ArrayList<>();
        if (json.has("mention_roles") && json.get("mention_roles").isArray()) {
            for (JsonNode roleIdNode : json.get("mention_roles")) {
                this.mentionedRoleIds.add(roleIdNode.asText());
            }
        }
    }

    @Override public String getId() { return id; }
    @Override public String getContent() { return content; }
    @Override public String getChannelId() { return channelId; }
    @Override public String getGuildId() { return guildId; }
    @Override public User getAuthor() { return author; }

    /**
     * Returns the unique ID of the message author.
     *
     * @return The author's user ID, or {@code null} if no author data is present.
     */
    public String getAuthorId() { return author != null ? author.getId() : null; }

    @Override public Member getMember() { return member; }

    /**
     * {@inheritDoc}
     * @return An unmodifiable list of mentioned {@link User}s.
     */
    @Override
    public List<User> getMentions() {
        return Collections.unmodifiableList(mentions);
    }

    /**
     * Retrieves all {@link Role}s explicitly mentioned in this message.
     * <p>This method resolves the stored role IDs against the current guild's role cache.</p>
     *
     * @return An unmodifiable {@link List} of mentioned {@link Role}s.
     */
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

    /**
     * Scans the message content for channel mentions (formatted as {@code <#ID>})
     * and resolves them against the guild's channel cache.
     *
     * @return An unmodifiable {@link List} of mentioned {@link GuildChannel}s.
     */
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
    public List<VoiceChannel> getMentionedVoiceChannels() {
        return getMentionedChannels().stream()
                .filter(c -> c instanceof VoiceChannel)
                .map(c -> (VoiceChannel) c)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>Note: The emoji string is URL-encoded before being sent to the Fluxer API.</p>
     */
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

    /**
     * Removes a specific user's reaction from this message.
     *
     * @param userId The ID of the user whose reaction should be removed.
     * @param emoji  The emoji to remove.
     * @return A {@link RestAction} representing the removal operation.
     */
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

    /**
     * Internal helper to execute reaction-related API calls.
     *
     * @param emoji     The raw emoji string.
     * @param routeType The API route template to use.
     * @return A {@link RestAction} representing the request.
     */
    private RestAction<Void> executeReaction(String emoji, Route routeType) {
        String encodedEmoji;
        try {
            encodedEmoji = URLEncoder.encode(emoji, StandardCharsets.UTF_8);
        } catch (Exception e) {
            encodedEmoji = emoji;
            e.printStackTrace();
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

    /**
     * Helper method to retrieve the associated {@link Guild} via the internal API instance.
     *
     * @return The {@link Guild} object, or {@code null} if not a guild message.
     */
    private Guild getGuild() {
        if (this.guildId == null) return null;
        return requester.getApi().getGuildById(this.guildId);
    }

    /**
     * Internal DTO for message content updates.
     */
    private static class EditPayload {
        public String content;
        public EditPayload(String content) { this.content = content; }
    }
}