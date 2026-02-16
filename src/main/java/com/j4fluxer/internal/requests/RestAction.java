package com.j4fluxer.internal.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * A class representing a pending request to the Fluxer API.
 * <p>
 * The {@code RestAction} acts as a terminal between the user and the API.
 * It allows for three types of execution:
 * <ul>
 *     <li>{@link #queue()} - Asynchronous execution (non-blocking).</li>
 *     <li>{@link #submit()} - Asynchronous execution returning a {@link CompletableFuture}.</li>
 *     <li>{@link #complete()} - Synchronous execution (blocking).</li>
 * </ul>
 *
 * @param <T> The type of the result returned by this action.
 */
public abstract class RestAction<T> {
    protected final Requester requester;
    protected final Route.CompiledRoute route;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected String requestBody = null;

    /**
     * Internal scheduler for planned executions (e.g., queueAfter).
     */
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "J4Fluxer-Scheduler");
        t.setDaemon(true);
        return t;
    });

    /**
     * Internal constructor for a new RestAction.
     *
     * @param requester The {@link Requester} instance to handle the HTTP call.
     * @param route     The {@link Route.CompiledRoute} containing the URL and Method.
     */
    public RestAction(Requester requester, Route.CompiledRoute route) {
        this.requester = requester;
        this.route = route;
    }

    /**
     * Sets the request body by serializing the provided object to JSON.
     *
     * @param bodyObject The object to be used as the JSON body.
     * @return The current RestAction instance for chaining.
     * @throws RuntimeException If JSON serialization fails.
     */
    public RestAction<T> setBody(Object bodyObject) {
        try {
            this.requestBody = mapper.writeValueAsString(bodyObject);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization error: " + e.getMessage(), e);
        }
        return this;
    }

    /**
     * Executes this action asynchronously.
     * Hata warnings are printed to the standard error stream.
     */
    public void queue() {
        queue(null, null);
    }

    /**
     * Executes this action asynchronously and provides the result to a consumer.
     *
     * @param success The consumer to handle the result.
     */
    public void queue(Consumer<T> success) {
        queue(success, null);
    }

    /**
     * Executes this action asynchronously with success and failure consumers.
     *
     * @param success The consumer to handle the result on success.
     * @param failure The consumer to handle errors on failure.
     */
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
                System.err.println("RestAction queue() encountered an error:");
                error.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Schedules this action to be queued after the specified delay.
     *
     * @param delay The amount of time to wait.
     * @param unit  The time unit for the delay.
     */
    public void queueAfter(long delay, TimeUnit unit) {
        queueAfter(delay, unit, null, null);
    }

    /**
     * Schedules this action to be queued after the specified delay.
     *
     * @param delay   The amount of time to wait.
     * @param unit    The time unit for the delay.
     * @param success The consumer to handle the result on success.
     */
    public void queueAfter(long delay, TimeUnit unit, Consumer<T> success) {
        queueAfter(delay, unit, success, null);
    }

    /**
     * Schedules this action to be queued after the specified delay.
     *
     * @param delay   The amount of time to wait.
     * @param unit    The time unit for the delay.
     * @param success The consumer to handle the result on success.
     * @param failure The consumer to handle errors on failure.
     */
    public void queueAfter(long delay, TimeUnit unit, Consumer<T> success, Consumer<Throwable> failure) {
        scheduler.schedule(() -> queue(success, failure), delay, unit);
    }

    /**
     * Executes this action and returns a {@link CompletableFuture}.
     *
     * @return A future representing the asynchronous request.
     */
    public CompletableFuture<T> submit() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeAndParse();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Executes this action after the specified delay and returns a {@link CompletableFuture}.
     *
     * @param delay The amount of time to wait.
     * @param unit  The time unit for the delay.
     * @return A future representing the delayed request.
     */
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

    /**
     * Executes this action synchronously and blocks the current thread until finished.
     * <p>
     * <b>Warning:</b> This method should not be called on the main thread or within
     * event listeners, as it will freeze the bot.
     *
     * @return The result of the request.
     * @throws RuntimeException If an error occurs during execution.
     */
    public T complete() {
        try {
            return executeAndParse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Internal logic to execute the request and handle the HTTP response.
     *
     * @return The parsed object of type T.
     * @throws Exception If the API returns an error or parsing fails.
     */
    private T executeAndParse() throws Exception {
        Response response = requester.execute(route, requestBody);

        if (!response.isSuccessful()) {
            String msg = "API Error: " + response.code() + " " + response.message();
            response.close();
            throw new RuntimeException(msg);
        }

        // Handle 204 No Content (Common in delete or role actions)
        if (response.code() == 204) {
            response.close();
            return null;
        }

        String jsonString = response.body().string();
        return handleResponse(jsonString);
    }

    /**
     * Abstract method implemented by subclasses to parse the raw JSON response.
     *
     * @param json The raw JSON string returned by the API.
     * @return The parsed entity.
     * @throws Exception If parsing fails.
     */
    protected abstract T handleResponse(String json) throws Exception;
}