package com.j4fluxer.hooks;

import com.j4fluxer.events.Event;
import com.j4fluxer.events.guild.*;
import com.j4fluxer.events.guild.member.*;
import com.j4fluxer.events.message.*;
import com.j4fluxer.events.session.ReadyEvent;
import com.j4fluxer.events.user.TypingStartEvent;

/**
 * An abstract adapter class for receiving events.
 * <p>
 * This class provides empty "hook" methods for all possible events.
 * Users of the library should extend this class and override only the methods for the events they wish to handle.
 * <p>
 * This eliminates the need to manually perform {@code instanceof} checks on every event.
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
        // --- MESSAGE EVENTS ---
        if (event instanceof GuildMessageReceivedEvent)
            onGuildMessageReceived((GuildMessageReceivedEvent) event);
        else if (event instanceof PrivateMessageReceivedEvent)
            onPrivateMessageReceived((PrivateMessageReceivedEvent) event);

        else if (event instanceof MessageUpdateEvent) onMessageUpdate((MessageUpdateEvent) event);
        else if (event instanceof MessageDeleteEvent) onMessageDelete((MessageDeleteEvent) event);
        else if (event instanceof MessageBulkDeleteEvent) onMessageBulkDelete((MessageBulkDeleteEvent) event);
        else if (event instanceof MessageReactionAddEvent) onMessageReactionAdd((MessageReactionAddEvent) event);
        else if (event instanceof MessageReactionRemoveEvent) onMessageReactionRemove((MessageReactionRemoveEvent) event);

            // --- GUILD EVENTS ---
        else if (event instanceof GuildJoinEvent) onGuildJoin((GuildJoinEvent) event);
        else if (event instanceof GuildLeaveEvent) onGuildLeave((GuildLeaveEvent) event);
        else if (event instanceof GuildMemberJoinEvent) onMemberJoin((GuildMemberJoinEvent) event);
        else if (event instanceof GuildMemberLeaveEvent) onMemberLeave((GuildMemberLeaveEvent) event);
        else if (event instanceof GuildMemberUpdateEvent) onMemberUpdate((GuildMemberUpdateEvent) event);
        else if (event instanceof GuildBanEvent) {
            if (((GuildBanEvent) event).isBanned()) onGuildBan((GuildBanEvent) event);
            else onGuildUnban((GuildBanEvent) event);
        }
        else if (event instanceof RoleCreateEvent) onRoleCreate((RoleCreateEvent) event);
        else if (event instanceof RoleDeleteEvent) onRoleDelete((RoleDeleteEvent) event);

            // --- OTHER EVENTS ---
        else if (event instanceof ReadyEvent) onReady((ReadyEvent) event);
        else if (event instanceof TypingStartEvent) onTypingStart((TypingStartEvent) event);
    }

    // --- HOOK METHODS ---

    /**
     * Called when a message is received in a text channel within a guild.
     *
     * @param event The event data containing the message and guild context.
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {}

    /**
     * Called when a message is received in a private (DM) channel.
     *
     * @param event The event data containing the message and private channel context.
     */
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {}

    /**
     * Called when a message is edited or updated.
     *
     * @param event The event data containing the updated message information.
     */
    public void onMessageUpdate(MessageUpdateEvent event) {}

    /**
     * Called when a single message is deleted.
     *
     * @param event The event data containing the deleted message ID.
     */
    public void onMessageDelete(MessageDeleteEvent event) {}

    /**
     * Called when multiple messages are deleted at once (bulk delete).
     *
     * @param event The event data containing the list of deleted message IDs.
     */
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {}

    /**
     * Called when a user adds a reaction emoji to a message.
     *
     * @param event The event data containing the reaction details.
     */
    public void onMessageReactionAdd(MessageReactionAddEvent event) {}

    /**
     * Called when a user removes a reaction emoji from a message.
     *
     * @param event The event data containing the reaction details.
     */
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {}

    /**
     * Called when the bot has successfully connected to the gateway and is ready.
     *
     * @param event The event data containing the bot's session information.
     */
    public void onReady(ReadyEvent event) {}

    /**
     * Called when the bot joins a new guild.
     *
     * @param event The event data containing the joined guild.
     */
    public void onGuildJoin(GuildJoinEvent event) {}

    /**
     * Called when the bot leaves a guild, is kicked, or the guild is deleted.
     *
     * @param event The event data containing the guild ID.
     */
    public void onGuildLeave(GuildLeaveEvent event) {}

    /**
     * Called when a new member joins a guild.
     *
     * @param event The event data containing the new member.
     */
    public void onMemberJoin(GuildMemberJoinEvent event) {}

    /**
     * Called when a member leaves a guild (or is kicked/banned).
     *
     * @param event The event data containing the member's user ID.
     */
    public void onMemberLeave(GuildMemberLeaveEvent event) {}

    /**
     * Called when a guild member's data (like nickname or roles) is updated.
     *
     * @param event The event data containing the update details.
     */
    public void onMemberUpdate(GuildMemberUpdateEvent event) {}

    /**
     * Called when a user is banned from a guild.
     *
     * @param event The event data containing the banned user.
     */
    public void onGuildBan(GuildBanEvent event) {}

    /**
     * Called when a user is unbanned from a guild.
     *
     * @param event The event data containing the unbanned user.
     */
    public void onGuildUnban(GuildBanEvent event) {}

    /**
     * Called when a user starts typing in a channel.
     *
     * @param event The event data containing the user and channel IDs.
     */
    public void onTypingStart(TypingStartEvent event) {}

    /**
     * Called when a new role is created in a guild.
     *
     * @param event The event data containing the new role.
     */
    public void onRoleCreate(RoleCreateEvent event) {}

    /**
     * Called when a role is deleted from a guild.
     *
     * @param event The event data containing the deleted role ID.
     */
    public void onRoleDelete(RoleDeleteEvent event) {}
}