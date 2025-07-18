package org.dromara.x.file.storage.core.client;


import io.minio.MinioAsyncClient;
import io.minio.S3Base;
import io.minio.credentials.Provider;
import io.minio.credentials.StaticProvider;
import io.minio.http.HttpUtils;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.net.URL;

public class RemoteProxyClient {

    private final OkHttpClient httpClient;

    private RemoteProxyClient(String endpoint, String apiKey, OkHttpClient httpClient) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.httpClient = httpClient != null ? httpClient : new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    }

//    public static Builder builder() {
//        return new Builder();
//    }
//
//    public static final class Builder {
//        private String endpoint;
//        private String apiKey;
//        private OkHttpClient httpClient;
//
//        public Builder endpoint(String endpoint) {
//            this.endpoint = endpoint;
//            return this;
//        }
//
//        public Builder credentials(String accessKey, String secretKey) {
//            // 简化：直接使用 accessKey 作为 API key，如果你有签名算法可替换这里
//            this.apiKey = accessKey;
//            return this;
//        }
//
//        public Builder httpClient(OkHttpClient httpClient) {
//            this.httpClient = httpClient;
//            return this;
//        }
//
//        public RemoteProxyClient build() {
//            if (endpoint == null || apiKey == null) {
//                throw new IllegalStateException("endpoint and credentials must not be null");
//            }
//            return new RemoteProxyClient(endpoint, apiKey, httpClient);
//        }
//    }
//    public static MinioAsyncClient.Builder builder() {
//        return new MinioAsyncClient.Builder();
//    }

    public static final class Builder {
        private HttpUrl baseUrl;

        private OkHttpClient httpClient;

        public Builder() {
        }


        private void setBaseUrl(HttpUrl url) {
            this.baseUrl = url;
        }

        public RemoteProxyClient.Builder endpoint(String endpoint) {
            this.setBaseUrl(HttpUtils.getBaseUrl(endpoint));
            return this;
        }

        public RemoteProxyClient.Builder endpoint(String endpoint, int port, boolean secure) {
            HttpUrl url = HttpUtils.getBaseUrl(endpoint);
            if (port >= 1 && port <= 65535) {
                url = url.newBuilder().port(port).scheme(secure ? "https" : "http").build();
                this.setBaseUrl(url);
                return this;
            } else {
                throw new IllegalArgumentException("port must be in range of 1 to 65535");
            }
        }

        public RemoteProxyClient.Builder endpoint(URL url) {
            HttpUtils.validateNotNull(url, "url");
            return this.endpoint(HttpUrl.get(url));
        }

        public RemoteProxyClient.Builder endpoint(HttpUrl url) {
            HttpUtils.validateNotNull(url, "url");
            HttpUtils.validateUrl(url);
            this.setBaseUrl(url);
            return this;
        }

        public RemoteProxyClient.Builder httpClient(OkHttpClient httpClient) {
            HttpUtils.validateNotNull(httpClient, "http client");
            this.httpClient = httpClient;
            return this;
        }

        public RemoteProxyClient build() {
            HttpUtils.validateNotNull(this.baseUrl, "endpoint");

            if (this.httpClient == null) {
                this.httpClient = HttpUtils.newDefaultHttpClient(S3Base.DEFAULT_CONNECTION_TIMEOUT, S3Base.DEFAULT_CONNECTION_TIMEOUT, S3Base.DEFAULT_CONNECTION_TIMEOUT);
            }
            return new RemoteProxyClient(this.baseUrl, this.httpClient);
        }
    }
}
