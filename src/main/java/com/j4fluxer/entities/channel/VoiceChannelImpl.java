package com.j4fluxer.entities.channel;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.PermissionOverwrite;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.Requester;

import java.util.List;

/**
 * The concrete implementation of a {@link VoiceChannel} on the Fluxer platform.
 *
 * <p>Voice channels allow for audio and video communication. This implementation
 * manages voice-specific metadata such as audio bitrate and user capacity limits.</p>
 */
public class VoiceChannelImpl extends AbstractChannel implements VoiceChannel {

    /**
     * Constructs a {@code VoiceChannelImpl} using JSON data received from the Fluxer API.
     *
     * @param json      The {@link JsonNode} containing voice channel details.
     * @param guild     The {@link Guild} this channel belongs to.
     * @param requester The {@link Requester} used for API operations.
     */
    public VoiceChannelImpl(JsonNode json, Guild guild, Requester requester) {
        super(json, guild, requester);
    }

    /**
     * Returns the type of this channel.
     *
     * @return {@link ChannelType#VOICE}.
     */
    @Override
    public ChannelType getType() { return ChannelType.VOICE; }

    /**
     * Retrieves the audio bitrate for this voice channel.
     *
     * <p>Higher bitrates generally result in better audio quality but require
     * more bandwidth. The default is typically 64,000 bps (64kbps).</p>
     *
     * @return The bitrate in bits per second.
     */
    @Override
    public int getBitrate() {
        return json.has("bitrate") ? json.get("bitrate").asInt() : 64000;
    }

    /**
     * Retrieves the maximum number of users allowed to be in this channel simultaneously.
     *
     * <p>A value of {@code 0} indicates that there is no fixed limit.</p>
     *
     * @return The user limit, or {@code 0} if no limit is set.
     */
    @Override
    public int getUserLimit() {
        return json.has("user_limit") ? json.get("user_limit").asInt() : 0;
    }

    /**
     * Attempts to initiate a connection to this voice channel.
     *
     * <p><b>Note:</b> Voice connectivity is currently under development and requires
     * additional Gateway implementation to establish a voice socket.</p>
     */
    @Override
    public void connect() {
        // TODO 0.1.4-alpha
        System.out.println("This feature is currently under development. Please check back later.");
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
}