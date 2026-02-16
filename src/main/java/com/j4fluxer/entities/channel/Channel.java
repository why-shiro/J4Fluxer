package com.j4fluxer.entities.channel;

import com.j4fluxer.internal.requests.RestAction;

public interface Channel {
    String getId();
    String getName();
    ChannelType getType();

    RestAction<Void> delete();
}