package com.j4fluxer.fluxer; // PAKET İSMİ DÜZELTİLDİ

import com.fasterxml.jackson.databind.ObjectMapper;
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

public class FluxerImpl implements Fluxer {
    private final Requester requester;
    private final EntityBuilder entityBuilder;
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<EventListener> listeners = new ArrayList<>();
    private GatewayClient gateway;

    public FluxerImpl(String token) {
        this.requester = new Requester(token);
        this.entityBuilder = new EntityBuilder(requester);

        try {
            this.gateway = new GatewayClient(token, this);
            this.gateway.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEventListener(Object... listenersToAdd) {
        for (Object listener : listenersToAdd) {
            if (listener instanceof EventListener) {
                this.listeners.add((EventListener) listener);
            }
        }
    }

    public void fireEvent(Event event) {
        for (EventListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Guild getGuildById(String id) {
        try {
            Route.CompiledRoute route = Route.GET_GUILD.compile(id);
            Response response = requester.execute(route, null);

            if (response.isSuccessful() && response.body() != null) {
                return new GuildImpl(mapper.readTree(response.body().string()), requester);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

    public Requester getRequester() {
        return requester;
    }

    private static class GuildCreatePayload {
        public String name;
        public GuildCreatePayload(String name) { this.name = name; }
    }
}