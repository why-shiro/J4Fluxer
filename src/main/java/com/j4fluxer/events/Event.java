package com.j4fluxer.events;

import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.fluxer.Fluxer;

/**
 * The abstract base class for all events in the J4Fluxer library.
 * <p>
 * Every event fired by the Gateway provides access to the core {@link Fluxer} API instance.
 * Unlike previous versions, this base class now supports native Guild retrieval.
 */
public abstract class Event {

    /** The core API instance. */
    protected final Fluxer api;

    /**
     * The ID of the guild where this event occurred.
     * Can be null for global events (like ReadyEvent) or DM events.
     */
    protected String guildId;

    /**
     * Internal constructor for a new Event.
     *
     * @param api The {@link Fluxer} instance associated with this event.
     */
    public Event(Fluxer api) {
        this.api = api;
        this.guildId = null; // Default to null
    }

    /**
     * Returns the current API instance.
     *
     * @return The {@link Fluxer} instance.
     */
    public Fluxer getFluxer() {
        return api;
    }

    /**
     * Returns the current API instance (Alias for {@link #getFluxer()}).
     *
     * @return The {@link Fluxer} instance.
     */
    public Fluxer getApi() {
        return api;
    }

    /**
     * Retrieves the {@link Guild} where this event occurred.
     * <p>
     * If the event happened in a Direct Message (DM) or is a global event (like ReadyEvent),
     * this method will return {@code null}.
     *
     * @return The {@link Guild} instance, or {@code null} if not applicable.
     */
    public Guild getGuild() {
        if (guildId == null) return null;
        return api.getGuildById(guildId);
    }

    /**
     * Sets the Guild ID for this event.
     * <p>
     * Subclasses should call this in their constructor if they belong to a guild.
     *
     * @param guildId The ID of the guild.
     */
    protected void setGuildId(String guildId) {
        this.guildId = guildId;
    }
}