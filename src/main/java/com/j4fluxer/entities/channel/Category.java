package com.j4fluxer.entities.channel;

import com.j4fluxer.internal.requests.RestAction;
import java.util.List;

/**
 * Represents a Fluxer Category, which is used to group and organize other channels together.
 *
 * <p>Categories act as parent entities for {@link TextChannel} and {@link VoiceChannel} instances,
 * allowing for collective permission management and visual organization within a guild.</p>
 */
public interface Category extends GuildChannel {

    /**
     * Retrieves a list of all {@link GuildChannel GuildChannels} that are currently
     * nested under this category.
     *
     * @return An unmodifiable {@link List} of channels belonging to this category.
     */
    List<GuildChannel> getChannels();

    /**
     * Creates a new {@link TextChannel} as a child of this category.
     *
     * <p>The new channel will automatically be assigned this category as its parent.</p>
     *
     * @param name The name of the new text channel.
     * @return A {@link RestAction} that, when executed, provides the newly created {@link TextChannel}.
     */
    RestAction<TextChannel> createTextChannel(String name);

    /**
     * Creates a new {@link VoiceChannel} as a child of this category.
     *
     * <p>The new channel will automatically be assigned this category as its parent.</p>
     *
     * @param name The name of the new voice channel.
     * @return A {@link RestAction} that, when executed, provides the newly created {@link VoiceChannel}.
     */
    RestAction<VoiceChannel> createVoiceChannel(String name);
}