package com.dxy.library.network.http.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * 忽略SSL证书的配置
 * @author duanxinyuan
 * 2016/9/28 13:15
 */
public class SSLSocketFactoryImpl extends SSLSocketFactory {
    private SSLContext sslContext = SSLContext.getInstance("SSL");
    private TrustManager trustManager;

    public SSLContext getSSLContext() {
        return sslContext;
    }

    public X509TrustManager getTrustManager() {
        return (X509TrustManager) trustManager;
    }

    public SSLSocketFactoryImpl(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException {
        trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                //注意这里不能返回null，否则会报错,如下面错误[1]
                return new X509Certificate[0];
            }
        };

        sslContext.init(null, new TrustManager[]{trustManager}, null);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return new String[0];
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return new String[0];
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int post, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(socket, host, post, autoClose);
    }

    @Override
    public Socket createSocket(String s, int i) {
        return null;
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i) {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) {
        return null;
    }
}
