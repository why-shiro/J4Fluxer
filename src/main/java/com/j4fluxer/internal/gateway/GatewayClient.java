package com.j4fluxer.internal.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j4fluxer.entities.OnlineStatus;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.message.MessageImpl;
import com.j4fluxer.events.Event;
import com.j4fluxer.events.guild.*;
import com.j4fluxer.events.guild.member.GuildMemberJoinEvent;
import com.j4fluxer.events.guild.member.GuildMemberLeaveEvent;
import com.j4fluxer.events.guild.member.GuildMemberUpdateEvent;
import com.j4fluxer.events.message.*;
import com.j4fluxer.events.session.ReadyEvent;
import com.j4fluxer.events.user.TypingStartEvent;
import com.j4fluxer.fluxer.FluxerImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The WebSocket client responsible for maintaining a persistent connection with the Fluxer Gateway.
 *
 * <p>This class handles the core Gateway protocol, including:</p>
 * <ul>
 *     <li>Authentication (Identify)</li>
 *     <li>Connection keep-alive (Heartbeats)</li>
 *     <li>Real-time event parsing and dispatching</li>
 *     <li>Presence and status updates</li>
 * </ul>
 */
public class GatewayClient extends WebSocketClient {

    /** The authentication token for the Fluxer bot. */
    private final String token;

    /** The core API implementation instance. */
    private final FluxerImpl api;

    /** JSON mapper for converting gateway payloads into data nodes. */
    private final ObjectMapper mapper = new ObjectMapper();

    /** Timer used to send periodic heartbeats to the Fluxer Gateway. */
    private Timer heartbeatTimer;

    /** The target URI for the Fluxer Gateway WebSocket connection. */
    private static final URI GATEWAY_URI = URI.create("wss://gateway.fluxer.app/?v=1&encoding=json&compress=none");

    /**
     * Constructs a new {@code GatewayClient} and prepares the WebSocket connection.
     *
     * @param token The bot token used for authentication.
     * @param api   The {@link FluxerImpl} instance that will handle dispatched events.
     */
    public GatewayClient(String token, FluxerImpl api) {
        super(GATEWAY_URI);
        this.token = token;
        this.api = api;
    }

    /**
     * Called when the WebSocket connection is successfully opened.
     * Initiates the Fluxer 'Identify' handshake.
     *
     * @param handshakedata Information about the server handshake.
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[LOG] Gateway Connection Successful.");
        sendIdentify();
    }

    /**
     * Sends a Presence Update (OpCode 3) to the Fluxer Gateway to change the bot's status.
     *
     * @param status The new {@link OnlineStatus} (e.g., ONLINE, DND, IDLE).
     */
    public void setPresence(OnlineStatus status) {
        if (!isOpen()) {
            System.err.println("[ERR] Gateway Client Not Open.");
            return;
        }

        JSONObject payload = new JSONObject();
        payload.put("op", 3); // Presence Update OpCode

        JSONObject d = new JSONObject();
        d.put("since", JSONObject.NULL);
        d.put("activities", new org.json.JSONArray());
        d.put("status", status.getKey());
        d.put("afk", false);

        payload.put("d", d);

        send(payload.toString());
        System.out.println("[GATEWAY] Status Changed: " + status.getKey());
    }

    /**
     * Handles incoming messages from the Fluxer Gateway.
     * <p>Parses OpCodes (like Heartbeat requests) and dispatches typed events
     * based on the 't' field in the Gateway payload.</p>
     *
     * @param message The raw JSON message received from the Gateway.
     */
    @Override
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            int op = json.optInt("op", -1);
            String type = json.optString("t");

            JsonNode d = null;
            if (json.has("d") && !json.isNull("d")) {
                d = mapper.readTree(json.get("d").toString());
            }

            // OpCode 10: Hello - Received upon connection to setup heartbeats
            if (op == 10 && d != null) {
                long interval = d.get("heartbeat_interval").asLong();
                startHeartbeat(interval);
                return;
            }

            if (type == null || type.isEmpty()) return;

            Event event = null;

            // Dispatch events based on Fluxer Event Type
            switch (type) {
                case "READY":
                    System.out.println("[LOG] Login Successful: " + d.get("user").get("username").asText());
                    event = new ReadyEvent(api, d);
                    break;

                case "MESSAGE_CREATE":
                    Message msg = new MessageImpl(d, api.getRequester());

                    if (msg.getGuildId() != null) {
                        api.fireEvent(new GuildMessageReceivedEvent(api, msg));
                    } else {
                        api.fireEvent(new PrivateMessageReceivedEvent(api, msg));
                    }
                    break;
                case "MESSAGE_UPDATE":
                    event = new MessageUpdateEvent(api, d);
                    break;
                case "MESSAGE_DELETE":
                    event = new MessageDeleteEvent(api, d);
                    break;
                case "MESSAGE_DELETE_BULK":
                    event = new MessageBulkDeleteEvent(api, d);
                    break;
                case "MESSAGE_REACTION_ADD":
                    event = new MessageReactionAddEvent(api, d);
                    break;
                case "MESSAGE_REACTION_REMOVE":
                    event = new MessageReactionRemoveEvent(api, d);
                    break;

                case "CHANNEL_CREATE":
                case "CHANNEL_UPDATE":
                    if (d.has("guild_id")) {
                        String gId = d.get("guild_id").asText();
                        GuildImpl guild = (GuildImpl) api.getGuildById(gId);
                        if (guild != null) {
                            guild.updateChannelCache(d);
                        }
                    }
                    break;

                case "CHANNEL_DELETE":
                    if (d.has("guild_id")) {
                        String gId = d.get("guild_id").asText();
                        String cId = d.get("id").asText();
                        GuildImpl guild = (GuildImpl) api.getGuildById(gId);
                        if (guild != null) {
                            guild.removeChannelFromCache(cId);
                        }
                    }
                    break;

                case "GUILD_CREATE":
                    GuildImpl guild = new GuildImpl(d, api.getRequester());
                    api.cacheGuild(guild);
                    event = new GuildJoinEvent(api, d);
                    break;
                case "GUILD_DELETE":
                    if (!d.has("unavailable") || !d.get("unavailable").asBoolean()) {
                        event = new GuildLeaveEvent(api, d);
                    }
                    break;

                case "GUILD_MEMBER_ADD":
                    event = new GuildMemberJoinEvent(api, d);
                    break;
                case "GUILD_MEMBER_REMOVE":
                    event = new GuildMemberLeaveEvent(api, d);
                    break;
                case "GUILD_MEMBER_UPDATE":
                    event = new GuildMemberUpdateEvent(api, d);
                    break;

                case "GUILD_BAN_ADD":
                    event = new GuildBanEvent(api, d, true);
                    break;
                case "GUILD_BAN_REMOVE":
                    event = new GuildBanEvent(api, d, false);
                    break;

                case "GUILD_ROLE_CREATE":
                    event = new RoleCreateEvent(api, d);
                    break;
                case "GUILD_ROLE_DELETE":
                    event = new RoleDeleteEvent(api, d);
                    break;

                case "TYPING_START":
                    event = new TypingStartEvent(api, d);
                    break;

                default:
                    break;
            }

            if (event != null) {
                api.fireEvent(event);
            }

        } catch (Exception e) {
            System.err.println("[ERR] Packet Processing Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called when the connection to the Fluxer Gateway is closed.
     * Stops the heartbeat timer.
     *
     * @param code   The closure code.
     * @param reason The reason for closure.
     * @param remote Whether the closure was initiated by the remote host.
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("[LOG] Connection Closed: " + reason + " (Code: " + code + ")");
        if (heartbeatTimer != null) heartbeatTimer.cancel();
    }

    /**
     * Called when an error occurs during WebSocket communication.
     *
     * @param ex The exception that occurred.
     */
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    /**
     * Sends the identification payload (OpCode 2) to authenticate the bot
     * with the Fluxer Gateway service.
     */
    private void sendIdentify() {
        JSONObject payload = new JSONObject();
        payload.put("op", 2);

        JSONObject d = new JSONObject();

        String authToken = token;
        if (!token.startsWith("Bot ")) {
            authToken = "Bot " + token;
        }
        d.put("token", authToken);
        d.put("intents", 0);

        JSONObject properties = new JSONObject();
        properties.put("os", System.getProperty("os.name"));
        properties.put("browser", "J4Fluxer");
        properties.put("device", "J4Fluxer");
        d.put("properties", properties);

        JSONObject presence = new JSONObject();
        presence.put("status", "online");
        presence.put("afk", false);
        d.put("presence", presence);

        payload.put("d", d);

        send(payload.toString());
    }

    /**
     * Starts the heartbeat loop at the interval specified by the Fluxer Gateway.
     * Ensures the connection is kept alive by sending OpCode 1 periodically.
     *
     * @param interval The heartbeat interval in milliseconds.
     */
    private void startHeartbeat(long interval) {
        if (heartbeatTimer != null) heartbeatTimer.cancel();
        heartbeatTimer = new Timer();
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isOpen()) {
                    JSONObject heartbeat = new JSONObject();
                    heartbeat.put("op", 1);
                    heartbeat.put("d", JSONObject.NULL);
                    send(heartbeat.toString());
                } else {
                    cancel();
                }
            }
        }, interval, interval);
    }
}