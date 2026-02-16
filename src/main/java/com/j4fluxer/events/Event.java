package com.j4fluxer.events;

import com.j4fluxer.fluxer.Fluxer;

/**
 * The base class for all events in J4Fluxer.
 * <p>
 * Every event fired by the gateway provides access to the core {@link Fluxer} instance.
 */
public abstract class Event {
    protected final Fluxer api;

    /**
     * Internal constructor for a new Event.
     *
     * @param api The {@link Fluxer} instance associated with this event.
     */
    public Event(Fluxer api) {
        this.api = api;
    }

    /**
     * Returns the core API instance.
     *
     * @return The {@link Fluxer} instance.
     */
    public Fluxer getFluxer() {
        return api;
    }
}