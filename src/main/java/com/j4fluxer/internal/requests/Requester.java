package com.j4fluxer.internal.requests;

import com.j4fluxer.fluxer.FluxerImpl;
import com.j4fluxer.internal.constants.Constants;
import okhttp3.*;
import java.io.IOException;

/**
 * The central engine responsible for sending RESTful HTTP requests to the Fluxer API.
 *
 * <p>This class wraps an {@link OkHttpClient} and handles authentication headers,
 * User-Agent identification, and request body processing for all outgoing Fluxer API calls.</p>
 */
public class Requester {

    /** The underlying HTTP client used for network communication. */
    private final OkHttpClient httpClient;

    /** The authentication token used to authorize requests with the Fluxer API. */
    private final String token;

    /** Reference to the core {@link FluxerImpl} instance. */
    private FluxerImpl api;

    /**
     * Constructs a new {@code Requester} instance.
     *
     * @param token The bot or account token used for authentication.
     */
    public Requester(String token) {
        this.token = token;
        this.httpClient = new OkHttpClient.Builder().build();
    }

    /**
     * Executes a synchronous HTTP request to the Fluxer API.
     *
     * <p>This method automatically:
     * <ul>
     *     <li>Ensures the Authorization header is correctly formatted (prepends "Bot " if needed).</li>
     *     <li>Sets the {@code User-Agent} defined in {@link Constants}.</li>
     *     <li>Handles JSON body serialization or empty body requirements for POST/PUT methods.</li>
     *     <li>Logs detailed error information if the response status is not successful.</li>
     * </ul>
     * </p>
     *
     * @param route    The compiled {@link Route.CompiledRoute} containing the target URL and HTTP method.
     * @param jsonBody The JSON string to be sent as the request body, or {@code null} if no body is needed.
     * @return The {@link Response} received from the Fluxer API.
     * @throws IOException If the request could not be executed due to cancellation, a connectivity problem, or timeout.
     */
    public Response execute(Route.CompiledRoute route, String jsonBody) throws IOException {
        // Handle Authentication Prefixing
        String authHeader = token;
        if (!token.startsWith("Bot ") && !token.startsWith("flx_")) {
            authHeader = "Bot " + token;
        }

        RequestBody body = null;

        // Process Request Body
        if (jsonBody != null) {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            body = RequestBody.create(jsonBody, mediaType);
        } else if (route.method == Route.Method.PUT || route.method == Route.Method.POST) {
            // Fluxer API requires at least an empty body for certain methods
            body = RequestBody.create(new byte[0], null);
        }

        // Build the final OkHttp Request
        Request.Builder builder = new Request.Builder()
                .url(route.url)
                .header("Authorization", authHeader)
                .header("User-Agent", Constants.USER_AGENT)
                .method(route.method.name(), body);

        Response response = httpClient.newCall(builder.build()).execute();

        // Error Logging for failed requests
        if (!response.isSuccessful()) {
            System.err.println("Fluxer API Error [" + response.code() + "]");
            System.err.println("URL: " + route.url);
            System.err.println("Method: " + route.method);
            try {
                // Peeks the body to avoid consuming the stream during logging
                System.err.println("Response: " + response.peekBody(Long.MAX_VALUE).string());
            } catch (Exception ignored) {}
        }

        return response;
    }

    /**
     * Injects the {@link FluxerImpl} instance into this requester.
     *
     * @param api The active Fluxer API implementation.
     */
    public void setApi(FluxerImpl api) {
        this.api = api;
    }

    /**
     * Retrieves the {@link FluxerImpl} instance associated with this requester.
     *
     * @return The {@link FluxerImpl} instance.
     */
    public FluxerImpl getApi() {
        return api;
    }
}