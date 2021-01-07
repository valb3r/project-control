package com.valb3r.projectcontrol.repository.domain;

import com.valb3r.projectcontrol.domain.Alias;
import com.valb3r.projectcontrol.domain.User;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(name = "aliasExcerpt", types = { Alias.class })
public interface AliasExcerpt {

    String getName();
}