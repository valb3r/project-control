package com.valb3r.projectcontrol.repository.domain;

import com.valb3r.projectcontrol.domain.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(name = "userExcerpt", types = { User.class })
public interface UserExcerpt {

    String getName();
    List<AliasExcerpt> getAliases();
}