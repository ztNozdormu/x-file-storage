package org.dromara.x.file.storage.core.platform;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageProperties.RemoteProxyConfig;
import org.dromara.x.file.storage.core.UploadPretreatment;
import org.dromara.x.file.storage.core.client.RemoteProxyClient;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 远程代理 存储
 */
@Getter
@Setter
@NoArgsConstructor
public class RemoteProxyFileStorage implements FileStorage {
    private String platform;
    private String domain;
    private int multipartThreshold;
    private int multipartPartSize;
    private FileStorageClientFactory<RemoteProxyClient> clientFactory;

    public RemoteProxyFileStorage(RemoteProxyConfig config, FileStorageClientFactory<RemoteProxyClient> clientFactory) {
        platform = config.getPlatform();
        domain = config.getDomain();
        multipartThreshold = config.getMultipartThreshold();
        multipartPartSize = config.getMultipartPartSize();
        this.clientFactory = clientFactory;
    }

    public RemoteProxyClient getClient() {
        return clientFactory.getClient();
    }

    @Override
    public boolean save(FileInfo fileInfo, UploadPretreatment pre) {
        return false;
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        return false;
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        return false;
    }

    @Override
    public void download(FileInfo fileInfo, Consumer<InputStream> consumer) {

    }

    @Override
    public void downloadTh(FileInfo fileInfo, Consumer<InputStream> consumer) {

    }

    @Override
    public void close() {
        clientFactory.close();
    }


}
