package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;

import java.util.Collections;
import java.util.List;

/**
 * The concrete implementation of a {@link Category} on the Fluxer platform.
 *
 * <p>This class handles category-specific logic, such as creating child channels
 * (Text or Voice) that are automatically nested within this category.</p>
 */
public class CategoryImpl extends AbstractChannel implements Category {

    /**
     * Constructs a {@code CategoryImpl} instance using JSON data received from the Fluxer API.
     *
     * @param json      The {@link JsonNode} containing category details.
     * @param guild     The {@link Guild} this category belongs to.
     * @param requester The {@link Requester} used for API operations.
     */
    public CategoryImpl(JsonNode json, Guild guild, Requester requester) {
        super(json, guild, requester);
    }

    /**
     * Constructs a minimal {@code CategoryImpl} instance with only an ID.
     *
     * @param id        The unique ID of the category.
     * @param guild     The {@link Guild} this category belongs to.
     * @param requester The {@link Requester} used for API operations.
     */
    public CategoryImpl(String id, Guild guild, Requester requester) {
        super(id, guild, requester);
    }

    /**
     * Returns the type of this channel.
     *
     * @return {@link ChannelType#CATEGORY}.
     */
    @Override
    public ChannelType getType() {
        return ChannelType.CATEGORY;
    }

    /**
     * Retrieves a list of channels that are organized under this category.
     *
     * <p>Note: Currently returns an empty list. In a full implementation, this
     * would filter the guild's channels by their parent category ID.</p>
     *
     * @return An unmodifiable {@link List} of child {@link GuildChannel}s.
     */
    @Override
    public List<GuildChannel> getChannels() {
        // Implementation detail: Returns empty list as per current code logic.
        return Collections.emptyList();
    }

    /**
     * Creates a new {@link TextChannel} and nests it under this category.
     *
     * <p>This method delegates the creation to the parent {@link Guild}
     * while providing this category's ID as the parent reference.</p>
     *
     * @param name The name of the new text channel.
     * @return A {@link RestAction} providing the newly created {@link TextChannel}.
     */
    @Override
    public RestAction<TextChannel> createTextChannel(String name) {
        return getGuild().createTextChannel(name, this.id);
    }

    /**
     * Creates a new {@link VoiceChannel} and nests it under this category.
     *
     * <p>This method delegates the creation to the parent {@link Guild}
     * while providing this category's ID as the parent reference.</p>
     *
     * @param name The name of the new voice channel.
     * @return A {@link RestAction} providing the newly created {@link VoiceChannel}.
     */
    @Override
    public RestAction<VoiceChannel> createVoiceChannel(String name) {
        return getGuild().createVoiceChannel(name, this.id);
    }

}