package com.j4fluxer.entities.guild;

import com.j4fluxer.entities.channel.Channel;
import com.j4fluxer.entities.channel.TextChannel;
import com.j4fluxer.entities.channel.VoiceChannel;
import com.j4fluxer.internal.requests.RestAction;
import java.util.List;

public interface Guild {
    String getId();
    String getName();
    String getOwnerId();

    TextChannel getTextChannelById(String id);

    RestAction<List<Channel>> retrieveChannels();
    RestAction<TextChannel> createTextChannel(String name);
    RestAction<VoiceChannel> createVoiceChannel(String name);
}