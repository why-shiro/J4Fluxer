package com.j4fluxer.entities.channel;

import com.j4fluxer.entities.message.Message;
import com.j4fluxer.internal.requests.RestAction;

public interface TextChannel extends GuildChannel {
    String getTopic();
    boolean isNSFW();

    RestAction<Message> sendMessage(String content);
}