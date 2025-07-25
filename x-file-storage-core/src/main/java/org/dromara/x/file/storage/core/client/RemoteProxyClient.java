package org.dromara.x.file.storage.core.client;


import io.minio.http.HttpUtils;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class RemoteProxyClient {

    private HttpUrl endpoint;
    private String apiKey;
    private final OkHttpClient httpClient;

    private RemoteProxyClient(HttpUrl endpoint, String apiKey, OkHttpClient httpClient) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.httpClient = httpClient != null ? httpClient : new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    }

    public static final class Builder {
        private HttpUrl baseUrl;

        private String apiKey;

        private OkHttpClient httpClient;

        public Builder() {
        }


        private void setBaseUrl(HttpUrl url) {
            this.baseUrl = url;
        }
        private void setApiKey(String apiKey) {
            this.apiKey = apiKey;
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
                this.httpClient = HttpUtils.newDefaultHttpClient(TimeUnit.MINUTES.toMillis(5L), TimeUnit.MINUTES.toMillis(5L), TimeUnit.MINUTES.toMillis(5L));
            }
            return new RemoteProxyClient(this.baseUrl,this.apiKey, this.httpClient);
        }

    }
}
