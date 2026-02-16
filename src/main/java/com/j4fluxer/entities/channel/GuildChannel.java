package com.j4fluxer.entities.channel;

import com.j4fluxer.entities.Permission;
import com.j4fluxer.entities.PermissionOverwrite;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.internal.requests.RestAction;

import java.util.EnumSet;
import java.util.List;

public interface GuildChannel extends Channel {
    Guild getGuild();
    String getParentId();
    int getPosition();
    List<PermissionOverwrite> getPermissionOverwrites();

    RestAction<Void> upsertPermissionOverride(String targetId, int type, EnumSet<Permission> allow, EnumSet<Permission> deny);

    RestAction<Void> deletePermissionOverride(String targetId);
}