package com.j4fluxer.hooks;

import com.j4fluxer.events.Event;

public interface EventListener {
    void onEvent(Event event);
}