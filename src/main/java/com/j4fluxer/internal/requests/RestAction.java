package com.j4fluxer.internal.requests;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

public abstract class RestAction<T> {
    protected final Requester requester;
    protected final Route.CompiledRoute route;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected String requestBody = null;

    public RestAction(Requester requester, Route.CompiledRoute route) {
        this.requester = requester;
        this.route = route;
    }

    public RestAction<T> setBody(Object bodyObject) {
        try {
            this.requestBody = mapper.writeValueAsString(bodyObject);
        } catch (Exception e) { e.printStackTrace(); }
        return this;
    }

    public CompletableFuture<T> submit() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var response = requester.execute(route, requestBody);
                if (!response.isSuccessful()) {
                    throw new RuntimeException("API Error: " + response.code() + " " + response.message());
                }
                return handleResponse(response.body().string());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected abstract T handleResponse(String json) throws Exception;
}