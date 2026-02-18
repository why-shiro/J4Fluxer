package com.j4fluxer.entities;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.EnumSet;

/**
 * Represents a permission overwrite on the Fluxer platform.
 *
 * <p>Permission overwrites are channel-specific rules that modify the base permissions
 * of a role or a member. They consist of a bitmask for permissions that are explicitly
 * <b>allowed</b> and a bitmask for permissions that are explicitly <b>denied</b>.</p>
 */
public class PermissionOverwrite {

    /** The unique ID of the target (Role or Member) for this overwrite. */
    private final String id;

    /** The type of target this overwrite applies to. */
    private final Type type;

    /** The bitmask of permissions explicitly allowed by this overwrite. */
    private final long allow;

    /** The bitmask of permissions explicitly denied by this overwrite. */
    private final long deny;

    /**
     * Constructs a new {@code PermissionOverwrite} using JSON data from the Fluxer API.
     *
     * @param json The {@link JsonNode} containing overwrite metadata, including target ID,
     *             type, and permission bitmasks.
     */
    public PermissionOverwrite(JsonNode json) {
        this.id = json.get("id").asText();

        // Resolves the target type: 0 represents a ROLE, 1 represents a MEMBER
        int typeInt = json.get("type").asInt();
        this.type = (typeInt == 1) ? Type.MEMBER : Type.ROLE;

        this.allow = Long.parseLong(json.get("allow").asText());
        this.deny = Long.parseLong(json.get("deny").asText());
    }

    /**
     * Returns the unique ID of the target (Role or Member) this overwrite is for.
     *
     * @return The target ID as a {@link String}.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the {@link Type} of this overwrite.
     *
     * @return The target type (ROLE or MEMBER).
     */
    public Type getType() {
        return type;
    }

    /**
     * Retrieves the set of permissions that are explicitly allowed by this overwrite.
     *
     * @return An {@link EnumSet} of allowed {@link Permission}s.
     */
    public EnumSet<Permission> getAllowed() {
        return Permission.getPermissions(allow);
    }

    /**
     * Retrieves the set of permissions that are explicitly denied by this overwrite.
     *
     * @return An {@link EnumSet} of denied {@link Permission}s.
     */
    public EnumSet<Permission> getDenied() {
        return Permission.getPermissions(deny);
    }

    /**
     * Returns the raw bitmask value of allowed permissions.
     *
     * @return The allowed bitmask as a {@code long}.
     */
    public long getAllowedRaw() {
        return allow;
    }

    /**
     * Returns the raw bitmask value of denied permissions.
     *
     * @return The denied bitmask as a {@code long}.
     */
    public long getDeniedRaw() {
        return deny;
    }

    /**
     * Checks if this overwrite is applied to a Role.
     *
     * @return {@code true} if the target is a role, {@code false} otherwise.
     */
    public boolean isRole() {
        return type == Type.ROLE;
    }

    /**
     * Checks if this overwrite is applied to a Member.
     *
     * @return {@code true} if the target is a member, {@code false} otherwise.
     */
    public boolean isMember() {
        return type == Type.MEMBER;
    }

    /**
     * Defines the target types for a {@link PermissionOverwrite}.
     */
    public enum Type {
        /** The overwrite targets a guild role. */
        ROLE(0),

        /** The overwrite targets a specific guild member. */
        MEMBER(1);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        /**
         * Returns the integer value associated with the overwrite type.
         *
         * @return The type value.
         */
        public int getValue() {
            return value;
        }
    }
}