package org.roag.rest;

import org.glassfish.jersey.SslConfigurator;
import org.roag.security.ssl.NoOpHostnameVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by eurohlam on 31/10/19.
 */
@Component
public class ClientHelper {

    private String restHost;
    private String restPort;
    private String restPath;
    private String securityProtocol;
    private String trustStoreFile;
    private String trustStorePassword;

    @Autowired
    public ClientHelper(@Value("${rest.host}") String restHost,
                        @Value("${rest.port}") String restPort,
                        @Value("${rest.path}") String restPath,
                        @Value("${ssl.protocol}") String securityProtocol,
                        @Value("${ssl.truststore.file}") String trustStoreFile,
                        @Value("${ssl.truststore.password}") String trustStorePassword) {
        this.restHost = restHost;
        this.restPort = restPort;
        this.restPath = restPath;
        this.securityProtocol = securityProtocol;
        this.trustStoreFile = trustStoreFile;
        this.trustStorePassword = trustStorePassword;
    }

    public String getRestHost() {
        return restHost;
    }

    public String getRestPort() {
        return restPort;
    }

    public String getRestPath() {
        return restPath;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public Client getClient() {
        if (restHost.startsWith("https://")) {
            return getSecuredClient();
        } else {
            return getSimpleClient();
        }
    }

    public Client getSimpleClient() {
        return ClientBuilder.newClient();
    }

    public Client getSecuredClient() {
        SslConfigurator sslConfigurator = SslConfigurator
                .newInstance()
                .securityProtocol(securityProtocol)
                .trustStoreFile(trustStoreFile)
                .trustStorePassword(trustStorePassword);

        return ClientBuilder
                .newBuilder()
                .sslContext(sslConfigurator.createSSLContext())
                .hostnameVerifier(NoOpHostnameVerifier.getInstance())
                .build();
    }

    public WebTarget getWebTarget() {
        return getClient().target(restHost + ":" + restPort + restPath);
    }

    public WebTarget getWebTarget(String path) {
        return getWebTarget().path(path);
    }

}
