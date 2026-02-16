package com.j4fluxer.entities.message;

import com.j4fluxer.entities.member.Member;
import com.j4fluxer.entities.user.User;
import com.j4fluxer.internal.requests.RestAction;

import java.util.List;

public interface Message {
    String getId();
    String getContent();
    String getChannelId();

    User getAuthor();
    Member getMember();

    String getGuildId();

    List<User> getMentions();

    RestAction<Message> edit(String newContent);
    RestAction<Void> delete();
}