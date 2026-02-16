package com.j4fluxer.entities.guild;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.Permission;

import java.util.EnumSet;

public class Role {
    private final String id;
    private final String name;
    private final long permissions;
    private final int position;
    private final int color;

    public Role(JsonNode json) {
        this.id = json.get("id").asText();
        this.name = json.get("name").asText();
        this.permissions = Long.parseLong(json.get("permissions").asText());
        this.position = json.get("position").asInt();
        this.color = json.get("color").asInt();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public long getPermissionsRaw() { return permissions; }
    public int getPosition() { return position; }
    public int getColor() { return color; }


    public boolean hasPermission(Permission permission) {
        if ((permissions & Permission.ADMINISTRATOR.getRawValue()) == Permission.ADMINISTRATOR.getRawValue()) {
            return true;
        }
        return (permissions & permission.getRawValue()) == permission.getRawValue();
    }

    public EnumSet<Permission> getPermissions() {
        return Permission.getPermissions(permissions);
    }
}