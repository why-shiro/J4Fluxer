package com.j4fluxer.entities.member;

import com.j4fluxer.entities.user.User;
import java.util.List;

/**
 * Represents a {@link User} specifically within the context of a Guild.
 */
public interface Member {
    /**
     * @return The underlying global {@link User} account.
     */
    User getUser();

    /**
     * @return A list of IDs for the roles assigned to this member in the guild.
     */
    List<String> getRoleIds();
}