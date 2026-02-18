package com.j4fluxer.hooks;

import com.j4fluxer.events.Event;

/**
 * The core interface for listening to events in the J4Fluxer library.
 *
 * <p>Any class that needs to handle events triggered by the Fluxer Gateway must
 * implement this interface. Once implemented, the listener can be registered to
 * the {@link com.j4fluxer.fluxer.Fluxer} instance.</p>
 *
 * <p>Typically, users will extend {@link ListenerAdapter} instead of implementing
 * this interface directly, as the adapter provides a more convenient way to
 * handle specific event types.</p>
 *
 * @see ListenerAdapter
 */
public interface EventListener {

    /**
     * Fired whenever an event is received from the Fluxer Gateway.
     *
     * <p>This is the main entry point for all events. Implementations are responsible
     * for determining the event type and performing the necessary logic.</p>
     *
     * @param event The {@link Event} object containing the data for the specific event.
     */
    void onEvent(Event event);
}