package org.roag.security.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by eurohlam on 31/10/19.
 */
public class NoOpHostnameVerifier implements HostnameVerifier {

    private static final HostnameVerifier verifier = new NoOpHostnameVerifier();

    public static HostnameVerifier getInstance() {
        return verifier;
    }

    private NoOpHostnameVerifier() {
    }

    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
