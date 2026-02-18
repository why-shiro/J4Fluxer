package com.j4fluxer.entities.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.Permission;

import java.util.EnumSet;

/**
 * Represents a Role within a Fluxer Guild.
 *
 * <p>Roles are used to define sets of permissions and visual styling (such as colors)
 * for members. They are organized in a hierarchy determined by their position.</p>
 */
public class Role {

    /** The unique identifier of the role. */
    private final String id;

    /** The display name of the role. */
    private final String name;

    /** The raw bitmask value representing the permissions assigned to this role. */
    private final long permissions;

    /** The numerical position of the role in the guild's hierarchy. */
    private final int position;

    /** The decimal color value of the role. */
    private final int color;

    /**
     * Constructs a new {@code Role} instance from Fluxer API JSON data.
     *
     * @param json The {@link JsonNode} containing the role's metadata.
     */
    public Role(JsonNode json) {
        this.id = json.get("id").asText();
        this.name = json.get("name").asText();
        this.permissions = Long.parseLong(json.get("permissions").asText());
        this.position = json.get("position").asInt();
        this.color = json.get("color").asInt();
    }

    /**
     * Returns the unique ID of this role.
     *
     * @return The role ID.
     */
    public String getId() { return id; }

    /**
     * Returns the name of this role.
     *
     * @return The role name.
     */
    public String getName() { return name; }

    /**
     * Returns the raw bitmask of permissions for this role.
     *
     * @return The permissions as a {@code long}.
     */
    public long getPermissionsRaw() { return permissions; }

    /**
     * Returns the position of this role in the guild hierarchy.
     * Higher values indicate a higher placement in the list.
     *
     * @return The role position.
     */
    public int getPosition() { return position; }

    /**
     * Returns the integer color value for this role.
     *
     * @return The color value.
     */
    public int getColor() { return color; }

    /**
     * Checks if this role possesses the specified {@link Permission}.
     *
     * <p>Note: If the role has the {@link Permission#ADMINISTRATOR} permission,
     * this method will always return {@code true} regardless of the input.</p>
     *
     * @param permission The permission to check.
     * @return {@code true} if the role has the permission or is an administrator;
     *         {@code false} otherwise.
     */
    public boolean hasPermission(Permission permission) {
        // Administrator override logic
        if ((permissions & Permission.ADMINISTRATOR.getRawValue()) == Permission.ADMINISTRATOR.getRawValue()) {
            return true;
        }
        return (permissions & permission.getRawValue()) == permission.getRawValue();
    }

    /**
     * Retrieves an {@link EnumSet} of all {@link Permission}s granted to this role.
     *
     * @return A set of permissions.
     */
    public EnumSet<Permission> getPermissions() {
        return Permission.getPermissions(permissions);
    }
}