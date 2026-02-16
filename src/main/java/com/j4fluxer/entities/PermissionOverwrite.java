package com.j4fluxer.entities;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.EnumSet;


public class PermissionOverwrite {

    private final String id;
    private final Type type;
    private final long allow;
    private final long deny;


    public PermissionOverwrite(JsonNode json) {
        this.id = json.get("id").asText();

        // 0 = ROLE, 1 = MEMBER
        int typeInt = json.get("type").asInt();
        this.type = (typeInt == 1) ? Type.MEMBER : Type.ROLE;

        this.allow = Long.parseLong(json.get("allow").asText());
        this.deny = Long.parseLong(json.get("deny").asText());
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }


    public EnumSet<Permission> getAllowed() {
        return Permission.getPermissions(allow);
    }


    public EnumSet<Permission> getDenied() {
        return Permission.getPermissions(deny);
    }


    public long getAllowedRaw() {
        return allow;
    }


    public long getDeniedRaw() {
        return deny;
    }


    public boolean isRole() {
        return type == Type.ROLE;
    }

    public boolean isMember() {
        return type == Type.MEMBER;
    }

    // --- ENUM: TYPE ---

    public enum Type {
        ROLE(0),
        MEMBER(1);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}