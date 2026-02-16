package com.j4fluxer.fluxer;

/**
 * Used to create and configure a {@link Fluxer} instance.
 */
public class FluxerBuilder {
    private String token;

    /**
     * Creates a new FluxerBuilder with the provided token.
     *
     * @param token The authentication token (Bot or User token).
     * @return A new FluxerBuilder instance.
     */
    public static FluxerBuilder create(String token) {
        FluxerBuilder builder = new FluxerBuilder();
        builder.token = token;
        return builder;
    }

    /**
     * Builds and initializes the Fluxer instance.
     * <p>
     * This method will start the WebSocket connection to the gateway.
     *
     * @return A ready-to-use {@link Fluxer} instance.
     * @throws IllegalArgumentException If the token is null or empty.
     */
    public Fluxer build() {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return new FluxerImpl(token);
    }
}