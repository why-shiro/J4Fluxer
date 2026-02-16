package com.j4fluxer.entities.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.internal.constants.Constants;

public class UserProfile {
    private final User user;
    private final String bio;
    private final String pronouns;
    private final String bannerHash;
    private final String accentColor;

    private final String nickname;
    private final String joinedAt;

    public UserProfile(JsonNode json) {
        this.user = new UserImpl(json.get("user"));

        JsonNode profile = json.get("user_profile");
        this.bio = profile.has("bio") && !profile.get("bio").isNull() ? profile.get("bio").asText() : "";
        this.pronouns = profile.has("pronouns") && !profile.get("pronouns").isNull() ? profile.get("pronouns").asText() : "";
        this.bannerHash = profile.has("banner") && !profile.get("banner").isNull() ? profile.get("banner").asText() : null;
        this.accentColor = profile.has("accent_color") && !profile.get("accent_color").isNull() ? profile.get("accent_color").asText() : null;

        if (json.has("guild_member") && !json.get("guild_member").isNull()) {
            JsonNode member = json.get("guild_member");
            this.nickname = member.has("nick") && !member.get("nick").isNull() ? member.get("nick").asText() : null;
            this.joinedAt = member.get("joined_at").asText();
        } else {
            this.nickname = null;
            this.joinedAt = null;
        }
    }

    public User getUser() { return user; }
    public String getBio() { return bio; }
    public String getPronouns() { return pronouns; }

    public String getBannerUrl() {
        if (bannerHash == null) return null;
        return Constants.CDN_URL + "/banners/" + user.getId() + "/" + bannerHash + ".png";
    }

    public String getNickname() { return nickname; }
}