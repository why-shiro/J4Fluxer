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

    private final OkHttpClient httpClient;
    private final String token;
    private FluxerImpl api;

    public Requester(String token) {
        this.token = token;
        this.httpClient = new OkHttpClient.Builder().build();
    }

    /**
     * Executes a synchronous HTTP request to the Fluxer API.
     *
     * <p>This method automatically handles the following:</p>
     * <ul>
     *     <li>Ensures the Authorization header is correctly formatted.</li>
     *     <li>Sets the {@code User-Agent} defined in constants.</li>
     *     <li>Handles JSON body serialization or empty body requirements.</li>
     *     <li>Logs detailed error information if the response status is not successful.</li>
     * </ul>
     *
     * @param route    The compiled {@link Route.CompiledRoute} containing the target URL and HTTP method.
     * @param jsonBody The JSON string to be sent as the request body, or {@code null} if no body is needed.
     * @return The {@link Response} received from the Fluxer API.
     * @throws IOException If the request could not be executed due to connectivity problems.
     */
    public Response execute(Route.CompiledRoute route, String jsonBody) throws IOException {
        String authHeader = token;
        if (!token.startsWith("Bot ") && !token.startsWith("flx_")) {
            authHeader = "Bot " + token;
        }

        RequestBody body = null;

        if (jsonBody != null) {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            body = RequestBody.create(jsonBody, mediaType);
        } else if (route.method == Route.Method.PUT || route.method == Route.Method.POST) {
            body = RequestBody.create(new byte[0], null);
        }

        Request.Builder builder = new Request.Builder()
                .url(route.url)
                .header("Authorization", authHeader)
                .header("User-Agent", Constants.USER_AGENT)
                .method(route.method.name(), body);

        Response response = httpClient.newCall(builder.build()).execute();

        if (!response.isSuccessful()) {
            System.err.println("Fluxer API Error [" + response.code() + "]");
            System.err.println("URL: " + route.url);
            System.err.println("Method: " + route.method);
            try {
                System.err.println("Response: " + response.peekBody(Long.MAX_VALUE).string());
            } catch (Exception ignored) {}
        }

        return response;
    }

    public void setApi(FluxerImpl api) {
        this.api = api;
    }

    public FluxerImpl getApi() {
        return api;
    }
}