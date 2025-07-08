package org.dromara.x.file.storage.spring.service;

import org.dromara.x.file.storage.core.upload.FilePartInfo;

public interface FilePartDetailService {
    /**
     * 保存文档分片信息
     * @param info
     */
    void saveFilePart(FilePartInfo info, String tableSuffix);

    /**
     * 删除文档分片信息
     * @param uploadId
     * @param tableSuffix
     */
    void deleteFilePartByUploadId(String uploadId, String tableSuffix);
}
