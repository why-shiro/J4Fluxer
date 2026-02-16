package com.j4fluxer.fluxer;

public class FluxerBuilder {
    private String token;

    public static FluxerBuilder create(String token) {
        FluxerBuilder builder = new FluxerBuilder();
        builder.token = token;
        return builder;
    }

    public Fluxer build() {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return new FluxerImpl(token);
    }
}