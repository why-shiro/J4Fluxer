package com.j4fluxer.internal.requests;

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

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

        Request.Builder builder = new Request.Builder()
                .url(route.url)
                .header("Authorization", authHeader)
                .header("User-Agent", userAgent)
                .header("Content-Type", "application/json");

        if (jsonBody != null) {
            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonBody, mediaType);
            builder.method(route.method.name(), body);
        } else {
            builder.method(route.method.name(), null);
        }

        Response response = httpClient.newCall(builder.build()).execute();

        if (!response.isSuccessful()) {
            System.err.println("❌ API Hatası: " + response.code() + " " + response.message());
            System.err.println("URL: " + route.url);
            System.err.println("Auth: " + authHeader);
        }

        return response;
    }
}