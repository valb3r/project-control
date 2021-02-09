package com.valb3r.projectcontrol.repository.domain;

import com.valb3r.projectcontrol.domain.Alias;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "aliasExcerpt", types = { Alias.class })
public interface AliasExcerpt {

    String getName();
}