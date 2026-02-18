package com.j4fluxer.internal.constants;

/**
 * Global constant values used throughout the Fluxer library.
 * <p>This class contains static configuration for network communication,
 * including API endpoints, versioning, and client identification.</p>
 */
public class Constants {

    /**
     * The version of the Fluxer API used by the library.
     */
    public static final String API_VERSION = "v1";

    /**
     * The root host URL for the Fluxer API services.
     */
    public static final String BASE_HOST = "https://api.fluxer.app";

    /**
     * The complete base URL for API requests, combining the host and the API version.
     */
    public static final String API_BASE = BASE_HOST + "/" + API_VERSION;

    /**
     * The User-Agent string sent in the HTTP header to identify the Fluxer4J client.
     */
    public static final String USER_AGENT = "Fluxer4J (Java, 1.0.0)";

    /**
     * The base URL for the Fluxer Content Delivery Network (CDN), used for static assets and media.
     */
    public static final String CDN_URL = "https://fluxerusercontent.com";
}