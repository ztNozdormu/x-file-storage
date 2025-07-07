package org.dromara.x.file.storage.core.constant;

public interface Constant {

    /**
     * 文件的访问控制列表，一般情况下只有对象存储支持该功能
     */
    interface ACL {
        /**
         * 私有
         */
        String PRIVATE = "private";
        /**
         * 公共读
         */
        String PUBLIC_READ = "public-read";
        /**
         * 公共读写
         */
        String PUBLIC_READ_WRITE = "public-read-write";
    }

    /**
     * 阿里云 OSS 的 ACL
     * {@link com.aliyun.oss.model.CannedAccessControlList}
     */
    interface AliyunOssACL extends ACL {
        String DEFAULT = "default";
    }

    /**
     * Amazon S3 的 ACL
     * {@link com.amazonaws.services.s3.model.CannedAccessControlList}
     */
    interface AmazonS3ACL extends ACL {
        String AUTHENTICATED_READ = "authenticated-read";
        String LOG_DELIVERY_WRITE = "log-delivery-write";
        String BUCKET_OWNER_READ = "bucket-owner-read";
        String BUCKET_OWNER_FULL_CONTROL = "bucket-owner-full-control";
        String AWS_EXEC_READ = "aws-exec-read";
    }

    /**
     * Amazon S3 的 ACL
     * {@link com.amazonaws.services.s3.model.CannedAccessControlList}
     */
    @Deprecated
    interface AwsS3ACL extends AmazonS3ACL {}

    /**
     * Amazon S3 V2 的 ACL
     * {@link software.amazon.awssdk.services.s3.model.ObjectCannedACL}
     */
    interface AmazonS3V2ACL extends ACL {
        String AUTHENTICATED_READ = "authenticated-read";
        String AWS_EXEC_READ = "aws-exec-read";
        String BUCKET_OWNER_READ = "bucket-owner-read";
        String BUCKET_OWNER_FULL_CONTROL = "bucket-owner-full-control";
        String UNKNOWN_TO_SDK_VERSION = null;
    }

    /**
     * 华为云 OBS 的 ACL
     * {@link com.obs.services.model.AccessControlList}
     * {@link com.obs.services.internal.IConvertor#transCannedAcl(String)}
     */
    interface HuaweiObsACL extends ACL {
        String PUBLIC_READ_DELIVERED = "public-read-delivered";
        String PUBLIC_READ_WRITE_DELIVERED = "public-read-write-delivered";
        String AUTHENTICATED_READ = "authenticated-read";
        String BUCKET_OWNER_READ = "bucket-owner-read";
        String BUCKET_OWNER_FULL_CONTROL = "bucket-owner-full-control";
        String LOG_DELIVERY_WRITE = "log-delivery-write";
    }

    /**
     * 百度云 BOS 的 ACL
     * {@link com.baidubce.services.bos.model.CannedAccessControlList}
     */
    interface BaiduBosACL extends ACL {}

    /**
     * 腾讯云 COS 的 ACL
     * {@link com.qcloud.cos.model.CannedAccessControlList}
     */
    interface TencentCosACL extends ACL {
        String DEFAULT = "default";
    }

    /**
     * GoogleCloud Storage 的 ACL（已经做了命名规则转换）
     * {@link com.google.cloud.storage.Storage.PredefinedAcl}
     * 如果这里的预定义ACL满足不了要求，也可以使用 {@link com.google.cloud.storage.Acl}
     * 文档：https://cloud.google.com/storage/docs/access-control/lists?hl=zh-cn
     */
    interface GoogleCloudStorageACL extends ACL {
        String AUTHENTICATED_READ = "authenticated-read";
        String ALL_AUTHENTICATED_USERS = "all-authenticated-users";
        String PROJECT_PRIVATE = "project-private";
        String BUCKET_OWNER_READ = "bucket-owner-read";
        String BUCKET_OWNER_FULL_CONTROL = "bucket-owner-full-control";
    }

    /**
     * Azure Blob Storage 的 ACL（已经做了命名规则转换）
     * {@link com.azure.storage.file.datalake.models.PathPermissions}
     * {@link com.azure.storage.file.datalake.models.PathAccessControlEntry}
     * 文档：https://learn.microsoft.com/zh-cn/azure/storage/blobs/data-lake-storage-access-control
     */
    interface AzureBlobStorageACL extends ACL {}

    /**
     * 火山引擎 TOS 的 ACL
     * {@link com.volcengine.tos.comm.common.ACLType}
     */
    interface VolcengineTosACL extends ACL {
        String AUTHENTICATED_READ = "authenticated-read";
        String BUCKET_OWNER_READ = "bucket-owner-read";
        String BUCKET_OWNER_FULL_CONTROL = "bucket-owner-full-control";
        String ACL_LOG_DELIVERY_WRITE = "log-delivery-write";
        String ACL_BUCKET_OWNER_ENTRUSTED = "bucket-owner-entrusted";
        String ACL_UNKNOWN = "unknown";
    }

    /**
     * 元数据名称，这里列举的是一些相对通用的名称，但不一定每个存储平台都支持，具体支持情况自行查阅对应存储的相关文档
     * <p>阿里云 OSS {@link com.aliyun.oss.model.ObjectMetadata} {@link com.aliyun.oss.internal.OSSHeaders}</p>
     * <p>Amazon S3 {@link com.amazonaws.services.s3.model.ObjectMetadata} {@link com.amazonaws.services.s3.Headers}</p>
     * <p>Amazon S3 V2 {@link software.amazon.awssdk.services.s3.model.PutObjectRequest.Builder} {@link software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest.Builder} {@link software.amazon.awssdk.services.s3.model.CopyObjectRequest.Builder}</p>
     * <p>华为云 OBS {@link com.obs.services.model.ObjectMetadata }</p>
     * <p>百度云 BOS {@link com.baidubce.services.bos.model.ObjectMetadata }</p>
     * <p>腾讯云 COS {@link com.qcloud.cos.model.ObjectMetadata }</p>
     * <p>七牛云 Kodo <a href="https://developer.qiniu.com/kodo/1312/upload">https://developer.qiniu.com/kodo/1312/upload</a></p>
     * <p>又拍云 USS {@link com.upyun.RestManager.PARAMS}</p>
     * <p>MinIO {@link io.minio.ObjectWriteArgs}</p>
     * <p>GoogleCloud Storage {@link com.google.cloud.storage.BlobInfo} {@link com.google.cloud.storage.Storage.BlobField}</p>
     * <p>FastDFS {@link org.dromara.x.file.storage.core.platform.FastDfsFileStorage#getObjectMetadata(org.dromara.x.file.storage.core.FileInfo)}</p>
     * <p>Azure Blob Storage {@link com.azure.storage.blob.models.BlobHttpHeaders}</p>
     * <p>火山引擎 TOS {@link com.volcengine.tos.model.object.ObjectMetaRequestOptions}</p>
     */
    interface Metadata {
        String CACHE_CONTROL = "Cache-Control";
        String CONTENT_DISPOSITION = "Content-Disposition";
        String CONTENT_ENCODING = "Content-Encoding";
        String CONTENT_LENGTH = "Content-Length";
        String CONTENT_MD5 = "Content-MD5";
        String CONTENT_TYPE = "Content-Type";
        String CONTENT_LANGUAGE = "Content-Language";
        String EXPIRES = "Expires";
        String LAST_MODIFIED = "Last-Modified";
    }

    /**
     * 复制模式
     */
    enum CopyMode {
        /**
         * 自动选择，优先使用同存储平台复制，不支持同存储平台复制的情况下走跨存储平台复制
         */
        AUTO,
        /**
         * 仅使用同存储平台复制，如果不支持同存储平台复制则抛出异常。FTP、SFTP等存储平台不支持同存储平台复制，只能走跨存储平台复制
         */
        SAME,
        /**
         * 仅使用跨存储平台复制
         */
        CROSS
    }

    /**
     * 移动模式
     */
    enum MoveMode {
        /**
         * 自动选择，优先使用同存储平台复制，不支持同存储平台复制的情况下走跨存储平台复制
         */
        AUTO,
        /**
         * 仅使用同存储平台复制，如果不支持同存储平台复制则抛出异常。FTP、SFTP等存储平台不支持同存储平台复制，只能走跨存储平台复制
         */
        SAME,
        /**
         * 仅使用跨存储平台复制
         */
        CROSS
    }

    /**
     * FileInfo 中上传状态的常量
     * {@link org.dromara.x.file.storage.core.FileInfo#uploadStatus}
     */
    interface FileInfoUploadStatus {
        /**
         * 1：初始化完成
         */
        int INITIATE = 1;

        /**
         * 2：上传完成
         */
        int COMPLETE = 2;
    }

    /**
     * 哈希类型
     */
    interface Hash {

        /**
         * 摘要信息
         */
        interface MessageDigest {
            String MD2 = "MD2";
            String MD5 = "MD5";
            String SHA1 = "SHA-1";
            String SHA256 = "SHA-256";
            String SHA384 = "SHA-384";
            String SHA512 = "SHA-512";
        }
    }

    /**
     * 生成预签名 URL
     */
    interface GeneratePresignedUrl {
        /**
         * HTTP 请求方法
         */
        interface Method {
            String GET = "GET";
            String PUT = "PUT";
            String POST = "POST";
            String DELETE = "DELETE";
            String HEAD = "HEAD";
            String OPTIONS = "OPTIONS";
        }
    }

    /**
     * 生成url盐
     */
    String FILE_DOWNLOAD_URI_SALT = "^KywPkljKF5ZGz^5Lj@#tiSPfX%mDPoK6Vs5SY&W#8qtioIooj#tS75m%6ZNsOmM";
}
