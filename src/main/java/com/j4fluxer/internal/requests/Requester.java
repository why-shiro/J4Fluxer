package com.j4fluxer.internal.requests;

import com.j4fluxer.internal.constants.Constants;
import okhttp3.*;
import java.io.IOException;

public class Requester {
    private final OkHttpClient httpClient;
    private final String token;

    public Requester(String token) {
        this.token = token;
        this.httpClient = new OkHttpClient.Builder().build();
    }

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
            System.err.println("API Error [" + response.code() + "]");
            System.err.println("URL: " + route.url);
            System.err.println("Method: " + route.method);
            try {
                System.err.println("Response: " + response.peekBody(Long.MAX_VALUE).string());
            } catch (Exception ignored) {}
        }

        return response;
    }
}