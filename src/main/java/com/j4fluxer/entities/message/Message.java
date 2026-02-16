package com.j4fluxer.entities.message;

import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.RestAction;

public interface Message {
    String getId();
    String getContent();
    String getChannelId();

    User getAuthor();

    String getGuildId();
    RestAction<Message> edit(String newContent);
    RestAction<Void> delete();
}