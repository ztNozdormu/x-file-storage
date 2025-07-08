package org.dromara.x.file.storage.spring.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.enums.ImageFileTypeEnum;

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
     * 更新元数据 TODO 比对 ossPath如果和历史不一致需要同时更新以及删除历史文件问题
     * @param storageCode storageCode|FileDetail id 文档存储编码-文档存储对象信息Id
     * @param metadata  元数据信息
     * @return true or fasle
     */
    boolean updateMetadata(String storageCode, Map<String, String> metadata);

    /**
     * 查询文档对象元数据信息--适用所有业务
     * @param storageCode  storageCode|FileDetail id 文档存储编码-文档存储对象信息Id
     * @return
     */
    String getMetadata(String storageCode);

    /**
     * 查询影像文档关联的元数据信息--只适用于影像业务
     * @param storageCode
     * @return
     */
    String getDicomMetadata(String storageCode);
    /**
     * 通过Id查询文档对象信息
     * @param storageCode 业务存储编码|FileDetail表ID
     * @return
     */
    FileInfo getById(String storageCode);

    /**
     * 通过业务存储编码获取文档访问路径 只支持 胶片/报告
     * @param storageCode 业务存储编码|FileDetail表ID
     * @return presignedUrl
     */
    String generatePresignedUrl(String storageCode);

    /**
     * 泛用性支持dicom
     * @param fileInfo 存储文档信息对象
     * @return   presignedUrl
     */
    String generatePresignedUrl(FileInfo fileInfo);

    /**
     * 通过业务存储编码获取文档访问路径 只支持 胶片/报告
     * @param storageCode 业务存储编码|FileDetail表ID
     * @param expirationTime 设置过期时间 单位秒
     * @return presignedUrl
     */
    String generatePresignedUrl(String storageCode, int expirationTime);

    /**
     * 泛用性支持dicom
     * @param fileInfo 存储文档信息对象
     * @param expirationTime 设置过期时间 单位秒
     * @return   presignedUrl
     */
    String generatePresignedUrl(FileInfo fileInfo, int expirationTime);

    /**
     * 解冻 (仅对oss存储有效) 其他类型存储解冻默认返回200
     * 通过业务存储编码获取文档访问路径 只支持 胶片/报告
     * @param storageCode 业务存储编码|FileDetail表ID
     * @return 解冻状态码
     *   第一次开始解冻 返回202 解冻中 返回409 已经解冻再次调用 返回200 objectName不存在 返回404 对非归档类型进行解冻操作 返回400
     */
    int freeze(String storageCode);

    /**
     * 解冻 (仅对oss存储有效) 其他类型存储解冻默认返回200
     * 泛用性支持dicom
     * @param fileInfo 存储文档信息对象
     * @return  解冻状态码
     *   第一次开始解冻 返回202 解冻中 返回409 已经解冻再次调用 返回200 objectName不存在 返回404 对非归档类型进行解冻操作 返回400
     */
    int freeze(FileInfo fileInfo);

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
     * 批量删除影像
     * @param storageCode 影像存储编码
     * @param ossKeys 影像文档存储ossPath集合
     * @param fileTypeEnum 业务文档类型
     * @return true or false
     */
    void deleteBatch(String storageCode, Set<String> ossKeys, ImageFileTypeEnum fileTypeEnum);

    /**
     * 不适合下载较大的文件（建议 <10MB）。
     * 适用场景：
     * 下载小文件并处理为字符串、JSON、图片预览等。
     * @param fileInfo 存储文件信息对象
     * @return byte[]
     */
    byte[] downLoadBytes(FileInfo fileInfo);

    /**
     * 判断文档是否存在--只适用于非影像业务
     * @param storageCode 业务存储编码 | FileDetail表ID
     * @return true or false
     */
    boolean exists(String storageCode);

    /**
     * 判断文档是否存在
     * @param fileInfo 文档存储信息对象
     * @return true or false
     */
    boolean exists(FileInfo fileInfo);
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
     * 非影像业务
     * 想将文件写入特定目标（如 HTTP 响应流、压缩文件、数据库 blob 字段等）。
     * 需要自定义控制文件流去向。
     * @param storageCode  业务存储编码 | FileDetail表ID
     * @param out
     */
    void downLoadOutStream(String storageCode, OutputStream out);
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
     * @param fileInfo  文档信息对象
     * @param out
     */
    void downLoadOutStream(FileInfo fileInfo, OutputStream out);

    /**
     * 上传临时文档
     * @param inputStream   文件流
     * @param filename 文件名称
     * @return FileInfo
     */
    FileInfo uploadPerpetual(InputStream inputStream, String filename);

    /**
     * 上传临时文档
     * @param inputStream   文件流
     * @param filename 文件名称
     * @param expirationTime 存储周期
     * @return FileInfo
     */
    FileInfo uploadTemporary(InputStream inputStream, String filename, long expirationTime);

    /**
     * 临时文档转换为永久文档
     * @param storageCode   业务存储编码 | FileDetail表ID
     * @return true or false
     */
    boolean becomePerpetual(String storageCode);

    /**
     * 获取文档大小
     * @param storageCode
     * @return
     */
    long getSize(String storageCode);
}
