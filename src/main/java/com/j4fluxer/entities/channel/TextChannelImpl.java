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

/**
 * The concrete implementation of a {@link TextChannel} on the Fluxer platform.
 *
 * <p>This class provides the logic for interacting with text-based channels,
 * including sending messages, managing channel topics, and configuring
 * channel-specific settings like NSFW and slowmode via the Fluxer REST API.</p>
 */
public class TextChannelImpl extends AbstractChannel implements TextChannel {

    /**
     * Constructs a {@code TextChannelImpl} using JSON data received from the Fluxer API.
     *
     * @param json      The {@link JsonNode} containing text channel details.
     * @param guild     The {@link Guild} this channel belongs to.
     * @param requester The {@link Requester} used for API operations.
     */
    public TextChannelImpl(JsonNode json, Guild guild, Requester requester) {
        super(json, guild, requester);
    }

    /**
     * Constructs a minimal {@code TextChannelImpl} with only an ID.
     *
     * @param id        The unique ID of the text channel.
     * @param guild     The {@link Guild} this channel belongs to.
     * @param requester The {@link Requester} used for API operations.
     */
    public TextChannelImpl(String id, Guild guild, Requester requester) {
        super(id, guild, requester);
    }

    /**
     * Returns the type of this channel.
     *
     * @return {@link ChannelType#TEXT}.
     */
    @Override
    public ChannelType getType() {
        return ChannelType.TEXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTopic() {
        return (json != null && json.has("topic") && !json.get("topic").isNull())
                ? json.get("topic").asText() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNSFW() {
        return json != null && json.has("nsfw") && json.get("nsfw").asBoolean();
    }

    /**
     * Sends a plain text message to this channel using the Fluxer API.
     *
     * @param content The text content of the message.
     * @return A {@link RestAction} providing the sent {@link Message}.
     */
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

    /**
     * Updates the channel's topic.
     *
     * @param topic The new topic string.
     * @return A {@link RestAction} representing the modification.
     */
    @Override
    public RestAction<Void> setTopic(String topic) {
        return modifyChannel("topic", topic);
    }

    /**
     * Sets the NSFW status for this channel.
     *
     * @param nsfw {@code true} to enable, {@code false} to disable.
     * @return A {@link RestAction} representing the modification.
     */
    @Override
    public RestAction<Void> setNSFW(boolean nsfw) {
        return modifyChannel("nsfw", nsfw);
    }

    /**
     * Sets the slowmode (rate limit) for users in this channel.
     *
     * @param seconds The wait time between messages in seconds.
     * @return A {@link RestAction} representing the modification.
     */
    @Override
    public RestAction<Void> setSlowmode(int seconds) {
        return modifyChannel("rate_limit_per_user", seconds);
    }

    /**
     * Retrieves the permission overwrites for this channel.
     * <p>Note: Currently returns an empty list in this specific implementation.</p>
     *
     * @return An empty {@link List} of {@link PermissionOverwrite}s.
     */
    @Override
    public List<PermissionOverwrite> getPermissionOverwrites() {
        return List.of();
    }

    /**
     * Internal DTO used to encapsulate the content when sending a message.
     */
    private static class MessagePayload {
        public String content;
        public MessagePayload(String content) { this.content = content; }
    }
}