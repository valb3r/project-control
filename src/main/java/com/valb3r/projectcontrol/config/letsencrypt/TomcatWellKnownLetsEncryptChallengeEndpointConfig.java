package com.valb3r.projectcontrol.config.letsencrypt;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class TomcatWellKnownLetsEncryptChallengeEndpointConfig implements TomcatConnectorCustomizer {
    private final Logger logger = LoggerFactory.getLogger(TomcatWellKnownLetsEncryptChallengeEndpointConfig.class);

    @Value("${lets-encrypt-helper.keystore}")
    private String letsEncryptKeystore;

    @Value("${lets-encrypt-helper.enabled:true}")
    private boolean enabled;

    private final List<Endpoint> observedEndpoints = new CopyOnWriteArrayList<>();
    private final AtomicBoolean customized = new AtomicBoolean();

    @Override
    public void customize(Connector connector) {
        if (!enabled) {
            return;
        }

        if (customized.compareAndSet(false, true)) {
            new Thread().start();
        }

        var protocol = connector.getProtocolHandler();
        if (!(protocol instanceof AbstractProtocol)) {
            logger.info("Impossible to customize protocol {} for connector {}", connector.getProtocolHandler(), connector);
            return;
        }

        try {
            var method = AbstractProtocol.class.getDeclaredMethod("getEndpoint");
            method.setAccessible(true);
            var endpoint = (AbstractEndpoint<?, ?>) method.invoke(protocol);
            if (!endpoint.isSSLEnabled()) {
                logger.info("Endpoint {}:{} is not SSL enabled", endpoint.getClass().getCanonicalName(), endpoint.getPort());
                return;
            }

            var sslConfig = Arrays.stream(endpoint.findSslHostConfigs()).filter(it -> letsEncryptKeystore.equals(it.getCertificateKeystoreFile())).findFirst().orElse(null);
            if (null == sslConfig) {
                logger.info("Endpoint {}:{} has different KeyStore file", endpoint.getClass().getCanonicalName(), endpoint.getPort());
                return;
            }

            observedEndpoints.add(new Endpoint(sslConfig, endpoint));
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SimpleUrlHandlerMapping wellKnownLetsEncryptHook(WellKnownLetsEncryptChallenge challenge) {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setOrder(Integer.MAX_VALUE - 2); // Launch before ResourceHttpRequestHandler
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("/.well-known/acme-challenge", challenge);
        simpleUrlHandlerMapping.setUrlMap(urlMap);

        return simpleUrlHandlerMapping;
    }

    @Bean
    WellKnownLetsEncryptChallenge wellKnownLetsEncryptChallenge() {
        return new WellKnownLetsEncryptChallenge();
    }

    public static class WellKnownLetsEncryptChallenge extends AbstractController {

        @Override
        protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
            var res = new ModelAndView();
            res.setView((model, viewRequest, viewResponse) -> {
                try (var os = viewResponse.getOutputStream()) {
                    os.write("HELLO".getBytes(StandardCharsets.UTF_8));
                }
            });
            return res;
        }
    }

    private static class Endpoint {
        private final SSLHostConfig hostConfig;
        private final AbstractEndpoint<?, ?> tomcatEndpoint;

        public Endpoint(SSLHostConfig hostConfig, AbstractEndpoint<?, ?> tomcatEndpoint) {
            this.hostConfig = hostConfig;
            this.tomcatEndpoint = tomcatEndpoint;
        }

        public SSLHostConfig getHostConfig() {
            return hostConfig;
        }

        public AbstractEndpoint<?, ?> getTomcatEndpoint() {
            return tomcatEndpoint;
        }
    }
}
