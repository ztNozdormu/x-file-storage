package org.dromara.x.file.storage.core.platform;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dromara.x.file.storage.core.FileStorageProperties.RemoteProxyConfig;
import org.dromara.x.file.storage.core.client.RemoteProxyClient;

/**
 * 代理 存储平台的 Client 工厂
 */
@Getter
@Setter
@NoArgsConstructor
public class RemoteProxyStorageClientFactory implements FileStorageClientFactory<RemoteProxyClient> {
    private String platform;
    private String accessKey;
    private String secretKey;
    private String endPoint;
    private volatile RemoteProxyClient client;

    public RemoteProxyStorageClientFactory(RemoteProxyConfig config) {
        platform = config.getPlatform();
        accessKey = config.getAccessKey();
        secretKey = config.getSecretKey();
        endPoint = config.getEndPoint();
    }

    @Override
    public RemoteProxyClient getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = new RemoteProxyClient.Builder()
                            .endpoint(endPoint)
                            .build();
                }
            }
        }
        return client;
    }

    @Override
    public void close() {
        client = null;
    }
}
