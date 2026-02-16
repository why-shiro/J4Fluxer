package com.j4fluxer.internal.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;

import java.util.concurrent.*;
import java.util.function.Consumer;

public abstract class RestAction<T> {
    protected final Requester requester;
    protected final Route.CompiledRoute route;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected String requestBody = null;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "J4Fluxer-Scheduler");
        t.setDaemon(true);
        return t;
    });

    public RestAction(Requester requester, Route.CompiledRoute route) {
        this.requester = requester;
        this.route = route;
    }

    public RestAction<T> setBody(Object bodyObject) {
        try {
            this.requestBody = mapper.writeValueAsString(bodyObject);
        } catch (Exception e) {
            throw new RuntimeException("JSON hatası: " + e.getMessage(), e);
        }
        return this;
    }

    public void queue() {
        queue(null, null);
    }

    public void queue(Consumer<T> success) {
        queue(success, null);
    }

    public void queue(Consumer<T> success, Consumer<Throwable> failure) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return executeAndParse();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(result -> {
            if (success != null) success.accept(result);
        }).exceptionally(error -> {
            if (failure != null) {
                failure.accept(error);
            } else {
                System.err.println("RestAction queue() hatası:");
                error.printStackTrace();
            }
            return null;
        });
    }


    public void queueAfter(long delay, TimeUnit unit) {
        queueAfter(delay, unit, null, null);
    }

    public void queueAfter(long delay, TimeUnit unit, Consumer<T> success) {
        queueAfter(delay, unit, success, null);
    }

    public void queueAfter(long delay, TimeUnit unit, Consumer<T> success, Consumer<Throwable> failure) {
        scheduler.schedule(() -> {
            // Süre dolunca normal queue metodunu çağır
            queue(success, failure);
        }, delay, unit);
    }

    public CompletableFuture<T> submit() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeAndParse();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<T> submitAfter(long delay, TimeUnit unit) {
        CompletableFuture<T> future = new CompletableFuture<>();

        scheduler.schedule(() -> {
            try {
                T result = executeAndParse();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, delay, unit);

        return future;
    }

    public T complete() {
        try {
            return executeAndParse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private T executeAndParse() throws Exception {
        Response response = requester.execute(route, requestBody);

        if (!response.isSuccessful()) {
            String msg = "API Error: " + response.code() + " " + response.message();
            response.close();
            throw new RuntimeException(msg);
        }

        if (response.code() == 204) {
            response.close();
            return null;
        }

        String jsonString = response.body().string();
        return handleResponse(jsonString);
    }

    protected abstract T handleResponse(String json) throws Exception;
}