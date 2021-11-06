package com.valb3r.projectcontrol.config.letsencrypt;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This configuration class is responsible for maintaining KeyStore with LetsEncrypt certificates.
 * Key property is {@code lets-encrypt-helper.keystore} that defiles path to the KeyStore with LetsEncrypt certificate.
 * By default, this bean will try to use server.ssl.* configuration variables, so all you need is just to configure your Tomcat SSL properly.
 * If the KeyStore does not exist at the moment application is started, this bean will try to issue the certificate
 * After start, this bean will watch LetsEncrypt certificate for expiration and will reissue certificate if it is close to its expiration.
 *
 * Note: It will use same KeyStore to store your Certificate, Traffic encryption key and User key.
 */
@Configuration
public class TomcatWellKnownLetsEncryptChallengeEndpointConfig implements TomcatConnectorCustomizer, ApplicationListener<ApplicationReadyEvent>, SmartLifecycle {
    private final Logger logger = LoggerFactory.getLogger(TomcatWellKnownLetsEncryptChallengeEndpointConfig.class);

    private final String domain;
    private final String letsEncryptServer;
    private final int keySize;
    private final Duration updateBeforeExpiry;
    private final Duration busyWaitInterval;
    private final String accountKeyAlias;
    private final boolean enabled;
    private final ServerProperties serverProperties;

    private final List<Endpoint> observedEndpoints = new CopyOnWriteArrayList<>();
    private final AtomicBoolean customized = new AtomicBoolean();
    private final AtomicBoolean ready = new AtomicBoolean();

    public TomcatWellKnownLetsEncryptChallengeEndpointConfig(
            ServerProperties serverProperties,
            @Value("${lets-encrypt-helper.domain}") String domain,
            @Value("${lets-encrypt-helper.letsencrypt-server:acme://letsencrypt.org/staging}") String letsEncryptServer,
            @Value("${lets-encrypt-helper.key-size:2048}") int keySize,
            @Value("${lets-encrypt-helper.update-before-expiry:PT7D}") Duration updateBeforeExpiry,
            @Value("${lets-encrypt-helper.busy-wait-interval:PT10S}") Duration busyWaitInterval,
            @Value("${lets-encrypt-helper.account-key-alias:letsencrypt-user}") String accountKeyAlias,
            @Value("${lets-encrypt-helper.enabled:true}") boolean enabled
    ) {
        Security.addProvider(new BouncyCastleProvider());
        this.serverProperties = serverProperties;
        this.domain = domain;
        this.letsEncryptServer = letsEncryptServer;
        this.keySize = keySize;
        this.updateBeforeExpiry = updateBeforeExpiry;
        this.busyWaitInterval = busyWaitInterval;
        this.accountKeyAlias = accountKeyAlias;
        this.enabled = enabled;

        if (null == this.serverProperties.getSsl()) {
            throw new IllegalStateException("SSL is not configured");
        }

        if (null == this.serverProperties.getSsl().getKeyStore()) {
            throw new IllegalStateException("KeyStore is not configured");
        }

        if (null == this.serverProperties.getSsl().getKeyStorePassword()) {
            throw new IllegalStateException("Missing keystore password");
        }

        if (null == this.serverProperties.getSsl().getKeyAlias()) {
            throw new IllegalStateException("Missing key alias");
        }

        if (null == this.serverProperties.getSsl().getKeyPassword()) {
            throw new IllegalStateException("Missing key password");
        }
    }

    @Override
    public void start() {
        if (!enabled) {
            ready.set(true);
            return;
        }

        File certFile = new File(serverProperties.getSsl().getKeyStore());
        if (certFile.exists()) {
            ready.set(true);
            return;
        }

        updateCertificateAndKeystore(true);
        ready.set(true);
    }

    @Override
    public void stop() {
        // NOP
    }

    @Override
    public boolean isRunning() {
        return ready.get();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void customize(Connector connector) {
        if (!enabled) {
            return;
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

            var sslConfig = Arrays.stream(endpoint.findSslHostConfigs())
                    .filter(it -> null != it.getCertificateKeystoreFile())
                    .filter(it -> it.getCertificateKeystoreFile().contains(serverProperties.getSsl().getKeyStore()))
                    .findFirst()
                    .orElse(null);

            if (null == sslConfig) {
                logger.info("Endpoint {}:{} has different KeyStore file", endpoint.getClass().getCanonicalName(), endpoint.getPort());
                return;
            }

            File certFile = new File(parseCertificateKeystoreFilePath(sslConfig));
            if (certFile.exists() && !certFile.canWrite()) {
                throw new IllegalStateException("Unable to write to: " + serverProperties.getSsl().getKeyStore());
            } else if (!certFile.exists()) {
                throw new IllegalStateException("No Keystore: " + serverProperties.getSsl().getKeyStore());
            }

            Endpoint observe = createObservableEndpoint(endpoint, sslConfig);
            if (observe == null) {
                return;
            }

            observedEndpoints.add(observe);
            if (customized.compareAndSet(false, true)) {
                new Thread(this::letsEncryptCheckCertValidityAndRotateIfNeeded).start();
            }
        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Endpoint createObservableEndpoint(AbstractEndpoint<?, ?> endpoint, SSLHostConfig sslConfig) {
        var observe = new Endpoint(sslConfig, endpoint);
        var cert = tryToReadCertificate(observe);
        if (null == cert) {
            logger.warn(
                    "For Endpoint {}:{} unable to read certificate from {}",
                    endpoint.getClass().getCanonicalName(),
                    endpoint.getPort(),
                    sslConfig.getCertificateKeystoreFile()
            );
            return null;
        }
        return observe;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (observedEndpoints.isEmpty()) {
            throw new IllegalStateException("Failed to configure LetsEncrypt");
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

    private void letsEncryptCheckCertValidityAndRotateIfNeeded() {
        while (true) {
            try {
                executeCheckCertValidityAndRotateIfNeeded();
                Thread.sleep(busyWaitInterval.toMillis());
            } catch (InterruptedException ex) {
                logger.info("LetsEncrypt update interrupted", ex);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeCheckCertValidityAndRotateIfNeeded() {
        for (var endpoint : observedEndpoints) {
            var cert = tryToReadCertificate(endpoint);
            if (null == cert) {
                logger.warn("Certificate is null on {}:{} from {}",
                        endpoint.getTomcatEndpoint().getClass(),
                        endpoint.getTomcatEndpoint().getPort(),
                        endpoint.getHostConfig().getCertificateKeystoreFile()
                );
                continue;
            }

            if (Instant.now().isBefore(cert.getNotAfter().toInstant().minus(updateBeforeExpiry))) {
                continue;
            }

            updateCertificateAndKeystore(false);
        }
    }

    private void updateCertificateAndKeystore(boolean isNewKeystore) {
        Session session = new Session(letsEncryptServer);
        URI tos;
        try {
            tos = session.getMetadata().getTermsOfService();
            if (null != tos && isNewKeystore) {
                logger.warn("Please review carefully and accept TOS {}", tos);
            }
            KeyPair domainKeyPair = KeyPairUtils.createKeyPair(keySize);
            Account account = new AccountBuilder()
                    .agreeToTermsOfService()
                    .useKeyPair(accountKey)
                    .create(session);

            Order order = account.newOrder().domains(domain).create();

            // Perform all required authorizations
            for (Authorization auth : order.getAuthorizations()) {
                authorize(auth);
            }

        } catch (AcmeException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyStore tryToReadKeystore(String keystoreType, String keystorePath, String keystorePassword) {
        var filePath = parseCertificateKeystoreFilePath(keystorePath);
        try {
            var ks = KeyStore.getInstance(keystoreType);
            try (var is = Files.newInputStream(new File(filePath).toPath())) {
                ks.load(is, keystorePassword.toCharArray());
            } catch (NoSuchAlgorithmException|IOException|CertificateException e) {
                logger.warn("Failed reading KeyStore of type {}", keystoreType, e);
                return null;
            }

            return ks;
        } catch (KeyStoreException ex) {
            logger.warn("Failed creating KeyStore of type {}", keystoreType, ex);
            return null;
        }
    }

    private X509Certificate tryToReadCertificate(Endpoint endpoint) {
        var ks = tryToReadKeystore(serverProperties.getSsl().getKeyStoreType(), serverProperties.getSsl().getKeyStore(), serverProperties.getSsl().getKeyStorePassword());
        if (null == ks) {
            throw new IllegalStateException("Missing KeyStore: " + serverProperties.getSsl().getKeyStore());
        }

        String keyAlias = endpoint.getHostConfig().getCertificateKeyAlias();
        Certificate certificate;
        try {
            certificate = ks.getCertificate(keyAlias);
        } catch (KeyStoreException e) {
            logger.warn("Failed reading certificate {} from {}", keyAlias, serverProperties.getSsl().getKeyStore());
            return null;
        }

        if (certificate instanceof X509Certificate) {
            return (X509Certificate) certificate;
        }

        return null;
    }

    private String parseCertificateKeystoreFilePath(SSLHostConfig sslConfig) {
        return parseCertificateKeystoreFilePath(sslConfig.getCertificateKeystoreFile());
    }

    private String parseCertificateKeystoreFilePath(String path) {
        return path.replaceAll("file://", "").replaceAll("file:", "");
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
