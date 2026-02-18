package com.j4fluxer.entities.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The concrete implementation of a {@link Member} on the Fluxer platform.
 *
 * <p>A Member represents a {@link User} within the context of a specific guild.
 * While a User contains global information, a Member contains guild-specific
 * data such as assigned roles.</p>
 */
public class MemberImpl implements Member {

    /** The underlying global {@link User} information. */
    private final User user;

    /** A list of role IDs assigned to this member within the guild. */
    private final List<String> roleIds;

    /**
     * Constructs a {@code MemberImpl} using global user data and guild-specific JSON data.
     *
     * @param user       The global {@link User} object associated with this member.
     * @param memberJson The {@link JsonNode} containing guild-specific member payload
     *                   (e.g., role IDs, join dates) from the Fluxer API.
     */
    public MemberImpl(User user, JsonNode memberJson) {
        this.user = user;
        this.roleIds = new ArrayList<>();

        // Extract role IDs from the JSON array provided by Fluxer
        if (memberJson.has("roles") && memberJson.get("roles").isArray()) {
            for (JsonNode roleNode : memberJson.get("roles")) {
                roleIds.add(roleNode.asText());
            }
        }
    }

    /**
     * Retrieves the global {@link User} object associated with this member.
     *
     * @return The {@link User} instance.
     */
    @Override
    public User getUser() { return user; }

    /**
     * Retrieves an unmodifiable list of role IDs assigned to this member in the guild.
     *
     * @return An unmodifiable {@link List} of role ID strings.
     */
    @Override
    public List<String> getRoleIds() { return Collections.unmodifiableList(roleIds); }
}