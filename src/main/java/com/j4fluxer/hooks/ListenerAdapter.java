package com.j4fluxer.hooks;

import com.j4fluxer.events.Event;
import com.j4fluxer.events.guild.*;
import com.j4fluxer.events.guild.member.GuildMemberJoinEvent;
import com.j4fluxer.events.guild.member.GuildMemberLeaveEvent;
import com.j4fluxer.events.guild.member.GuildMemberUpdateEvent;
import com.j4fluxer.events.message.*;
import com.j4fluxer.events.session.ReadyEvent;
import com.j4fluxer.events.user.TypingStartEvent;

/**
 * An abstract adapter class for receiving events.
 * <p>This class provides empty "hook" methods for all possible events. Users of the library
 * should extend this class and override only the methods for the events they wish to handle.</p>
 *
 * <p>This eliminates the need to manually perform {@code instanceof} checks on every event.</p>
 */
public abstract class ListenerAdapter implements EventListener {

    /**
     * The entry point for all events. This method identifies the event type
     * and dispatches it to the corresponding hook method.
     *
     * @param event The event to be dispatched.
     */
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
        else if (event instanceof MessageReactionAddEvent) onMessageReactionAdd((MessageReactionAddEvent) event);
        else if (event instanceof GuildBanEvent) {
            if (((GuildBanEvent) event).isBanned()) onGuildBan((GuildBanEvent) event);
            else onGuildUnban((GuildBanEvent) event);
        }
        else if (event instanceof TypingStartEvent) onTypingStart((TypingStartEvent) event);
        else if (event instanceof RoleCreateEvent) onRoleCreate((RoleCreateEvent) event);
        else if (event instanceof RoleDeleteEvent) onRoleDelete((RoleDeleteEvent) event);
        else if (event instanceof MessageReactionRemoveEvent) onMessageReactionRemove((MessageReactionRemoveEvent) event);
    }

    /** Called when a message is received in a text channel. */
    public void onMessageReceived(MessageReceivedEvent event) {}

    /** Called when a message is edited or updated. */
    public void onMessageUpdate(MessageUpdateEvent event) {}

    /** Called when a single message is deleted. */
    public void onMessageDelete(MessageDeleteEvent event) {}

    /** Called when multiple messages are deleted at once (bulk delete). */
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {}

    /** Called when the bot has successfully connected to the gateway and is ready. */
    public void onReady(ReadyEvent event) {}

    /** Called when the bot joins a new guild. */
    public void onGuildJoin(GuildJoinEvent event) {}

    /** Called when the bot leaves a guild. */
    public void onGuildLeave(GuildLeaveEvent event) {}

    /** Called when a new member joins a guild. */
    public void onMemberJoin(GuildMemberJoinEvent event) {}

    /** Called when a member leaves a guild. */
    public void onMemberLeave(GuildMemberLeaveEvent event) {}

    /** Called when a guild member's data (like nickname or roles) is updated. */
    public void onMemberUpdate(GuildMemberUpdateEvent event) {}

    /** Called when a user is banned from a guild. */
    public void onGuildBan(GuildBanEvent event) {}

    /** Called when a user is unbanned from a guild. */
    public void onGuildUnban(GuildBanEvent event) {}

    /** Called when a user starts typing in a channel. */
    public void onTypingStart(TypingStartEvent event) {}

    /** Called when a new role is created in a guild. */
    public void onRoleCreate(RoleCreateEvent event) {}

    /** Called when a role is deleted from a guild. */
    public void onRoleDelete(RoleDeleteEvent event) {}

    /** Called when a user adds a reaction to a message. */
    public void onMessageReactionAdd(MessageReactionAddEvent event) {}

    /** Called when a user removes a reaction from a message. */
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {}
}