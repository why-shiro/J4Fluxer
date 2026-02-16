package com.j4fluxer.internal.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j4fluxer.entities.message.Message;
import com.j4fluxer.entities.message.MessageImpl;
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
        System.out.println("[LOG] Gateway Bağlantısı Kuruldu.");
        sendIdentify();
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            int op = json.optInt("op", -1);
            String type = json.optString("t");

            // Veri (d) bazen null gelebilir (Heartbeat ACK gibi)
            JsonNode d = null;
            if (json.has("d") && !json.isNull("d")) {
                d = mapper.readTree(json.get("d").toString());
            }

            // --- 1. KALP ATIŞI AYARLAMASI (OpCode 10) ---
            if (op == 10 && d != null) {
                long interval = d.get("heartbeat_interval").asLong();
                startHeartbeat(interval);
                return; // Başka işlem yapma
            }

            // Olay tipi yoksa işlem yapma
            if (type == null || type.isEmpty()) return;

            Event event = null;

            // --- 2. OLAY YÖNETİMİ (EVENT DISPATCHER) ---
            switch (type) {
                case "READY":
                    System.out.println("[LOG] Giriş Başarılı: " + d.get("user").get("username").asText());
                    event = new ReadyEvent(api, d);
                    break;

                // --- MESAJ OLAYLARI ---
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

                // --- SUNUCU OLAYLARI ---
                case "GUILD_CREATE":
                    // Bot açıldığında veya yeni sunucuya girdiğinde
                    event = new GuildJoinEvent(api, d);
                    break;
                case "GUILD_DELETE":
                    // unavailable true ise sunucu çökmüştür, değilse atılmışızdır
                    if (!d.has("unavailable") || !d.get("unavailable").asBoolean()) {
                        event = new GuildLeaveEvent(api, d);
                    }
                    break;

                // --- ÜYE OLAYLARI ---
                case "GUILD_MEMBER_ADD":
                    event = new GuildMemberJoinEvent(api, d);
                    break;
                case "GUILD_MEMBER_REMOVE":
                    event = new GuildMemberLeaveEvent(api, d);
                    break;
                case "GUILD_MEMBER_UPDATE":
                    event = new GuildMemberUpdateEvent(api, d);
                    break;

                // --- YASAKLAMA (BAN) OLAYLARI ---
                case "GUILD_BAN_ADD":
                    event = new GuildBanEvent(api, d, true); // True = Banlandı
                    break;
                case "GUILD_BAN_REMOVE":
                    event = new GuildBanEvent(api, d, false); // False = Ban açıldı
                    break;

                // --- KULLANICI OLAYLARI ---
                case "TYPING_START":
                    event = new TypingStartEvent(api, d);
                    break;

                // --- ROL OLAYLARI ---
                case "GUILD_ROLE_CREATE":
                    event = new RoleCreateEvent(api, d);
                    break;
                case "GUILD_ROLE_DELETE":
                    event = new RoleDeleteEvent(api, d);
                    break;

                default:
                    // Tanımlamadığımız diğer olaylar (Göz ardı et)
                    // System.out.println("İşlenmeyen Event: " + type);
                    break;
            }

            // Eğer bir event oluştuysa sisteme ateşle
            if (event != null) {
                api.fireEvent(event);
            }

        } catch (Exception e) {
            System.err.println("[ERR] Paket Hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.err.println("[LOG] Bağlantı Kapandı: " + reason + " (Kod: " + code + ")");
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
        d.put("token", token);
        JSONObject properties = new JSONObject();
        properties.put("os", System.getProperty("os.name"));
        properties.put("browser", "Fluxer4J");
        properties.put("device", "Fluxer4J");
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