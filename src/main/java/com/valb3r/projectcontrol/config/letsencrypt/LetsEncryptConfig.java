package com.valb3r.projectcontrol.config.letsencrypt;

import com.github.valb3r.letsencrypthelper.TomcatWellKnownLetsEncryptChallengeEndpointConfig;
import org.springframework.context.annotation.Import;

@Import(TomcatWellKnownLetsEncryptChallengeEndpointConfig.class)
public class LetsEncryptConfig {
}
