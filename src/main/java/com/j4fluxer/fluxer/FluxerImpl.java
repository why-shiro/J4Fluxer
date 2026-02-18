package com.j4fluxer.fluxer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j4fluxer.entities.OnlineStatus;
import com.j4fluxer.entities.guild.Guild;
import com.j4fluxer.entities.guild.GuildImpl;
import com.j4fluxer.internal.json.EntityBuilder;
import com.j4fluxer.internal.requests.Requester;
import com.j4fluxer.internal.requests.RestAction;
import com.j4fluxer.internal.requests.Route;
import com.j4fluxer.internal.gateway.GatewayClient;
import com.j4fluxer.hooks.EventListener;
import com.j4fluxer.events.Event;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The primary implementation of the {@link Fluxer} interface.
 * <p>This class acts as the core of the library, managing the internal {@link Requester},
 * the {@link GatewayClient} connection, event dispatching, and guild caching.</p>
 */
public class FluxerImpl implements Fluxer {

    /** Internal requester used for making REST API calls. */
    private final Requester requester;

    /** Utility for building entities from JSON. */
    private final EntityBuilder entityBuilder;

    /** Mapper for processing JSON data. */
    private final ObjectMapper mapper = new ObjectMapper();

    /** A list of registered event listeners. */
    private final List<EventListener> listeners = new ArrayList<>();

    /** The client responsible for the Fluxer Gateway connection. */
    private GatewayClient gateway;

    /** A thread-safe cache for stored guilds, mapped by their unique ID. */
    private final Map<String, Guild> guildCache = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@code FluxerImpl} instance and initiates the connection to Fluxer.
     *
     * @param token The bot token used for authentication.
     */
    public FluxerImpl(String token) {
        this.requester = new Requester(token);
        this.entityBuilder = new EntityBuilder(requester);
        this.requester.setApi(this);

        try {
            this.gateway = new GatewayClient(token, this);
            this.gateway.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds one or more event listeners to the listener registry.
     * Only objects implementing {@link EventListener} will be added.
     *
     * @param listenersToAdd An array of listener objects to be registered.
     */
    @Override
    public void addEventListener(Object... listenersToAdd) {
        for (Object listener : listenersToAdd) {
            if (listener instanceof EventListener) {
                this.listeners.add((EventListener) listener);
            }
        }
    }

    /**
     * Dispatches an event to all registered {@link EventListener}s.
     * Any exception thrown by a listener is caught and printed to prevent blocking the event thread.
     *
     * @param event The {@link Event} object to be fired.
     */
    public void fireEvent(Event event) {
        for (EventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves a guild by its ID.
     * <p>This method checks the internal cache first. If the guild is not cached,
     * it performs a blocking REST request to fetch it from Fluxer and then caches the result.</p>
     *
     * @param id The unique ID of the guild.
     * @return The {@link Guild} object, or {@code null} if not found or an error occurs.
     */
    @Override
    public Guild getGuildById(String id) {
        if (guildCache.containsKey(id)) {
            return guildCache.get(id);
        }

        try {
            Route.CompiledRoute route = Route.GET_GUILD.compile(id);
            Response response = requester.execute(route, null);
            if (response.isSuccessful() && response.body() != null) {
                GuildImpl guild = new GuildImpl(mapper.readTree(response.body().string()), requester);
                guildCache.put(id, guild);
                return guild;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Manually adds a {@link Guild} instance to the internal cache.
     *
     * @param guild The guild object to cache.
     */
    public void cacheGuild(Guild guild) {
        guildCache.put(guild.getId(), guild);
        System.out.println("[CACHE] Server Cached: " + guild.getName());
    }

    /**
     * Creates a {@link RestAction} that, when executed, will create a new guild with the given name.
     *
     * @param name The name of the new guild.
     * @return A {@link RestAction} providing the newly created {@link Guild}.
     */
    @Override
    public RestAction<Guild> createGuild(String name) {
        Route.CompiledRoute route = Route.CREATE_GUILD.compile();
        return new RestAction<Guild>(requester, route) {
            @Override
            protected Guild handleResponse(String jsonStr) throws Exception {
                return new GuildImpl(mapper.readTree(jsonStr), requester);
            }
        }.setBody(new GuildCreatePayload(name));
    }

    /**
     * Updates the bot's presence status on the gateway.
     *
     * @param status The {@link OnlineStatus} to set (e.g., ONLINE, IDLE, DND).
     */
    @Override
    public void setStatus(OnlineStatus status) {
        if (gateway != null) {
            gateway.setPresence(status);
        }
    }

    /**
     * Returns the internal {@link Requester} used by this instance.
     *
     * @return The {@link Requester} instance.
     */
    public Requester getRequester() {
        return requester;
    }

    /**
     * Internal DTO used for the payload when creating a new guild.
     */
    private static class GuildCreatePayload {
        public String name;
        public GuildCreatePayload(String name) { this.name = name; }
    }
}