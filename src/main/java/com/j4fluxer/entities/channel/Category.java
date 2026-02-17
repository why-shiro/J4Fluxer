package com.j4fluxer.entities.channel;

import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

/**
 * Represents a Fluxer Category, which is used to group other channels together.
 */
public interface Category extends GuildChannel {
    /**
     * Retrieves a list of all {@link GuildChannel GuildChannels} that are currently
     * nested under this category.
     *
     * @return An immutable list of channels in this category.
     */
    List<GuildChannel> getChannels();

    RestAction<TextChannel> createTextChannel(String name);

    RestAction<VoiceChannel> createVoiceChannel(String name);
}