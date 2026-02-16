package com.j4fluxer.entities.channel;

import java.util.List;

public interface Category extends GuildChannel {
    List<GuildChannel> getChannels();
}