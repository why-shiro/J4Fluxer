package com.j4fluxer.internal.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.channel.*;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;

/**
 * A factory class responsible for building Fluxer entities from JSON data.
 * <p>This utility parses raw JSON responses from the Fluxer API and converts them
 * into concrete implementations of entities such as channels, roles, or users.</p>
 */
public class EntityBuilder {

    /** The requester used to perform internal API actions within built entities. */
    private final Requester requester;

    /**
     * Constructs a new {@code EntityBuilder}.
     *
     * @param requester The {@link Requester} instance to be passed to newly created entities.
     */
    public EntityBuilder(Requester requester) {
        this.requester = requester;
    }

    /**
     * Creates a concrete {@link Channel} instance based on the type specified in the JSON data.
     * <p>This method identifies whether the channel is a {@link TextChannel}, {@link VoiceChannel},
     * or {@link Category} and returns the appropriate implementation.</p>
     *
     * @param json  The {@link JsonNode} containing the channel data.
     * @param guild The {@link Guild} that this channel belongs to.
     * @return A specific {@link Channel} implementation, or {@code null} if the channel type is unknown.
     */
    public Channel createChannel(JsonNode json, Guild guild) {
        int typeId = json.has("type") ? json.get("type").asInt() : -1;
        ChannelType type = ChannelType.fromKey(typeId);

        switch (type) {
            case TEXT:
                return new TextChannelImpl(json, guild, requester);
            case VOICE:
                return new VoiceChannelImpl(json, guild, requester);
            case CATEGORY:
                return new CategoryImpl(json, guild, requester);
            default:
                // Logs an error if the channel type provided by Fluxer is not yet supported.
                System.err.println("Unknown channel type: " + typeId);
                return null;
        }
    }
}