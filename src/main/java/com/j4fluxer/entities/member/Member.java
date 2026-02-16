package com.j4fluxer.entities.member;

import com.j4fluxer.entities.user.User;

import java.util.List;

public interface Member {
    User getUser();
    List<String> getRoleIds(); // Rol ID'lerini liste olarak dönecek
}