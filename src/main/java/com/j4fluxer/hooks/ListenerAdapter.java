package com.j4fluxer.hooks;

import com.j4fluxer.events.Event;
import com.j4fluxer.events.guild.*;
import com.j4fluxer.events.guild.member.GuildMemberJoinEvent;
import com.j4fluxer.events.guild.member.GuildMemberLeaveEvent;
import com.j4fluxer.events.guild.member.GuildMemberUpdateEvent;
import com.j4fluxer.events.message.MessageBulkDeleteEvent;
import com.j4fluxer.events.message.MessageDeleteEvent;
import com.j4fluxer.events.message.MessageReceivedEvent;
import com.j4fluxer.events.message.MessageUpdateEvent;
import com.j4fluxer.events.session.ReadyEvent;
import com.j4fluxer.events.user.TypingStartEvent;

public abstract class ListenerAdapter implements EventListener {
    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageReceivedEvent) onMessageReceived((MessageReceivedEvent) event);
        else if (event instanceof MessageUpdateEvent) onMessageUpdate((MessageUpdateEvent) event);
        else if (event instanceof MessageDeleteEvent) onMessageDelete((MessageDeleteEvent) event);
        else if (event instanceof MessageBulkDeleteEvent) onMessageBulkDelete((MessageBulkDeleteEvent) event);
        else if (event instanceof ReadyEvent) onReady((ReadyEvent) event);
        else if (event instanceof GuildJoinEvent) onGuildJoin((GuildJoinEvent) event);
        else if (event instanceof GuildLeaveEvent) onGuildLeave((GuildLeaveEvent) event);
        else if (event instanceof GuildMemberJoinEvent) onMemberJoin((GuildMemberJoinEvent) event);
        else if (event instanceof GuildMemberLeaveEvent) onMemberLeave((GuildMemberLeaveEvent) event);
        else if (event instanceof GuildMemberUpdateEvent) onMemberUpdate((GuildMemberUpdateEvent) event);
        else if (event instanceof GuildBanEvent) {
            if (((GuildBanEvent) event).isBanned()) onGuildBan((GuildBanEvent) event);
            else onGuildUnban((GuildBanEvent) event);
        }
        else if (event instanceof TypingStartEvent) onTypingStart((TypingStartEvent) event);
        else if (event instanceof RoleCreateEvent) onRoleCreate((RoleCreateEvent) event);
        else if (event instanceof RoleDeleteEvent) onRoleDelete((RoleDeleteEvent) event);
    }

    public void onMessageReceived(MessageReceivedEvent event) {}
    public void onMessageUpdate(MessageUpdateEvent event) {}
    public void onMessageDelete(MessageDeleteEvent event) {}
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {}
    public void onReady(ReadyEvent event) {}
    public void onGuildJoin(GuildJoinEvent event) {}
    public void onGuildLeave(GuildLeaveEvent event) {}
    public void onMemberJoin(GuildMemberJoinEvent event) {}
    public void onMemberLeave(GuildMemberLeaveEvent event) {}
    public void onMemberUpdate(GuildMemberUpdateEvent event) {}
    public void onGuildBan(GuildBanEvent event) {}
    public void onGuildUnban(GuildBanEvent event) {}
    public void onTypingStart(TypingStartEvent event) {}
    public void onRoleCreate(RoleCreateEvent event) {}
    public void onRoleDelete(RoleDeleteEvent event) {}
}