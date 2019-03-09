package org.roag.camel;

import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;


/**
 * Created by eurohlam on 20/11/2018.
 */
public class TrustAllHttpConfigurer implements HttpClientConfigurer {

    @Override
    public void configureHttpClient(HttpClientBuilder builder) {
        try {
            SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustAllStrategy()).build();
            HostnameVerifier verifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1.2", "TLSv1.1"},
                    null,
                    verifier);
            final org.apache.http.config.Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();

            builder.setSSLSocketFactory(sslConnectionSocketFactory);

            HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);

            builder.setConnectionManager(ccm);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

