package com.j4fluxer.entities.member;

import com.fasterxml.jackson.databind.JsonNode;
import com.j4fluxer.entities.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemberImpl implements Member {
    private final User user;
    private final List<String> roleIds;

    public MemberImpl(User user, JsonNode memberJson) {
        this.user = user;
        this.roleIds = new ArrayList<>();

        // Rolleri JSON'dan çekip listeye atıyoruz
        if (memberJson.has("roles") && memberJson.get("roles").isArray()) {
            for (JsonNode roleNode : memberJson.get("roles")) {
                roleIds.add(roleNode.asText());
            }
        }
    }

    @Override public User getUser() { return user; }
    @Override public List<String> getRoleIds() { return Collections.unmodifiableList(roleIds); }
}