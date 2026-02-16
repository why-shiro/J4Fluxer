package com.j4fluxer.fluxer;

import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.RestAction;

public interface Fluxer {
    void addEventListener(Object... listenersToAdd);

    Guild getGuildById(String id);
    RestAction<Guild> createGuild(String name);
}