package com.j4fluxer.events;

import com.j4fluxer.fluxer.Fluxer;

public abstract class Event {
    protected final Fluxer api;

    public Event(Fluxer api) {
        this.api = api;
    }

    public Fluxer getFluxer() {
        return api;
    }
}