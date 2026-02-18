package com.j4fluxer.events.message;

import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.TextChannelImpl;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.user.User; // User import edildi
import com.j4fluxer.events.Event;
import com.j4fluxer.fluxer.Fluxer;
import com.j4fluxer.fluxer.FluxerImpl;

/**
 * Fired when a new message is received in a text channel.
 * <p>
 * This event wraps a {@link Message} object and provides convenience methods
 * to access the channel and guild contexts.
 */
public class MessageReceivedEvent extends Event {

    private final Message message;

    /**
     * Constructs a new MessageReceivedEvent.
     *
     * @param api     The API instance.
     * @param message The message that was received.
     */
    public MessageReceivedEvent(Fluxer api, Message message) {
        super(api);
        this.message = message;
        this.guildId = message.getGuildId();
    }

    /**
     * Retrieves the message object that triggered this event.
     *
     * @return The {@link Message} object.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Retrieves the user who sent the message.
     * <p>This is a convenience method for {@code getMessage().getAuthor()}.</p>
     *
     * @return The {@link User} object of the author.
     */
    public User getAuthor() {
        return message.getAuthor();
    }

    /**
     * Retrieves the raw text content of the message.
     *
     * @return The plain text content.
     */
    public String getContent() {
        return message.getContent();
    }

    /**
     * Retrieves the {@link Guild} where this message was sent.
     *
     * @return The {@link Guild} object, or {@code null} if sent in a Direct Message.
     */
    public Guild getGuild() {
        if (message.getGuildId() == null) return null;
        return api.getGuildById(message.getGuildId());
    }

    /**
     * Retrieves the channel where the message was sent.
     * <p>
     * This method attempts to find the channel in the guild cache. If the guild
     * or channel is not cached, it returns a lazily-initialized {@link TextChannelImpl}.
     *
     * @return The {@link TextChannel}, or {@code null} if sent in DMs (not yet supported).
     */
    public TextChannel getChannel() {
        if (message.getGuildId() == null) return null;

        Guild guild = api.getGuildById(message.getGuildId());

        if (guild != null) {
            return guild.getTextChannelById(message.getChannelId());
        }

        // Fallback: Lazy load if guild is not in cache
        FluxerImpl core = (FluxerImpl) api;
        Guild tempGuild = new GuildImpl(message.getGuildId(), core.getRequester());
        return new TextChannelImpl(message.getChannelId(), tempGuild, core.getRequester());
    }
}