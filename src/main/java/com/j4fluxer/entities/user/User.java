package com.j4fluxer.entities.user;

public interface User {
    String getId();
    String getUsername();
    String getDiscriminator();
    boolean isBot();
    String getAvatarHash();
    String getAvatarUrl(); // Link dönecek
}