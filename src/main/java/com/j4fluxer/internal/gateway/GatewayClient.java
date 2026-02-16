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

public class GatewayClient extends WebSocketClient {
    private final String token;
    private final FluxerImpl api;
    private final ObjectMapper mapper = new ObjectMapper();

    private Timer heartbeatTimer;
    private static final URI GATEWAY_URI = URI.create("wss://gateway.fluxer.app/?v=1&encoding=json&compress=none");

    public GatewayClient(String token, FluxerImpl api) {
        super(GATEWAY_URI);
        this.token = token;
        this.api = api;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[LOG] Gateway Connection Successful.");
        sendIdentify();
    }

    public void setPresence(OnlineStatus status) {
        if (!isOpen()) {
            System.err.println("[ERR] Gateway Client Not Open.");
            return;
        }

        JSONObject payload = new JSONObject();
        payload.put("op", 3);

        JSONObject d = new JSONObject();
        d.put("since", JSONObject.NULL);
        d.put("activities", new org.json.JSONArray());
        d.put("status", status.getKey());
        d.put("afk", false);

        payload.put("d", d);

        send(payload.toString());
        System.out.println("[GATEWAY] Status Changed: " + status.getKey());
    }

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

            if (op == 10 && d != null) {
                long interval = d.get("heartbeat_interval").asLong();
                startHeartbeat(interval);
                return; // Başka işlem yapma
            }

            if (type == null || type.isEmpty()) return;

            Event event = null;

            switch (type) {
                case "READY":
                    System.out.println("[LOG] Login Successful: " + d.get("user").get("username").asText());
                    event = new ReadyEvent(api, d);
                    break;

                case "MESSAGE_CREATE":
                    Message msg = new MessageImpl(d, api.getRequester());
                    event = new MessageReceivedEvent(api, msg);
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

                case "GUILD_CREATE":
                    GuildImpl guild =
                            new GuildImpl(d, api.getRequester());
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
                    event = new GuildBanEvent(api, d, true); // True = Banlandı
                    break;
                case "GUILD_BAN_REMOVE":
                    event = new GuildBanEvent(api, d, false); // False = Ban açıldı
                    break;
                case "MESSAGE_REACTION_ADD":
                    event = new MessageReactionAddEvent(api, d);
                    break;
                case "TYPING_START":
                    event = new TypingStartEvent(api, d);
                    break;

                case "GUILD_ROLE_CREATE":
                    event = new RoleCreateEvent(api, d);
                    break;
                case "GUILD_ROLE_DELETE":
                    event = new RoleDeleteEvent(api, d);
                    break;
                default:
                    break;
            }

            if (event != null) {
                api.fireEvent(event);
            }

        } catch (Exception e) {
            System.err.println("[ERR] Packet Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("[LOG] Connection Closed: " + reason + " (Kod: " + code + ")");
        if (heartbeatTimer != null) heartbeatTimer.cancel();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

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