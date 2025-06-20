package org.dromara.x.file.storage.spring.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.dromara.x.file.storage.core.FileInfo;

/**
 * XFile 组件 文件操作封装
 */
public interface XFileExtensionService {

    /**
     * 根据元数据创建文档对象信息
     * @param metadata  元数据信息
     * @return storageCode|FileDetail id  文档存储编码-文档存储对象信息Id
     */
    String createByMetadata(Map<String, String> metadata);

    /**
     * 更新元数据
     * @param storageCode storageCode|FileDetail id 文档存储编码-文档存储对象信息Id
     * @param metadata  元数据信息
     * @return true or fasle
     */
    boolean updateMetadata(String storageCode, Map<String, String> metadata);
    /**
     * 通过Id查询文档对象信息
     * @param storageCode 业务存储编码|FileDetail表ID
     * @return
     */
    FileInfo getById(String storageCode);

    /**
     * TODO 缺少解冻实现
     * 通过业务存储编码获取文档访问路径 只支持 胶片/报告
     * @param storageCode 业务存储编码|FileDetail表ID
     * @return presignedUrl
     */
    String generatePresignedUrl(String storageCode);

    /**
     * TODO 缺少解冻实现
     * 泛用性支持dicom
     * @param fileInfo 存储文档信息对象
     * @return   presignedUrl
     */
    String generatePresignedUrl(FileInfo fileInfo);

    /**
     * 删除文档--注意这个函数只适用于非影像业务文档删除
     * @param storageCode 业务存储编码 | FileDetail表ID
     * @return true or false
     */
    boolean delete(String storageCode);

    /**
     * 删除文档
     * @param fileInfo 存储文件信息对象
     * @return true or false
     */
    boolean delete(FileInfo fileInfo);

    /**
     * 不适合下载较大的文件（建议 <10MB）。
     * 适用场景：
     * 下载小文件并处理为字符串、JSON、图片预览等。
     * @param fileInfo 存储文件信息对象
     * @return byte[]
     */
    byte[] downLoadBytes(FileInfo fileInfo);

    /**
     * 优点：
     *
     * 自定义输出流，灵活性高。
     * 可写入 ByteArrayOutputStream、FileOutputStream、GZIPOutputStream等。
     * 支持流式处理，适合中大型文件。
     *
     * 缺点：
     * 稍微比 .file() 多写几行代码。
     *
     * 适用场景：
     * 想将文件写入特定目标（如 HTTP 响应流、压缩文件、数据库 blob 字段等）。
     * 需要自定义控制文件流去向。
     *
     * @param out
     */
    void downLoadOutStream(FileInfo fileInfo, ByteArrayOutputStream out);
}
