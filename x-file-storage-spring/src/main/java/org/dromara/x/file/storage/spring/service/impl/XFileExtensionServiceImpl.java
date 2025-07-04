package org.dromara.x.file.storage.spring.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.constant.Constant;
import org.dromara.x.file.storage.core.enums.DownloadFlagEnum;
import org.dromara.x.file.storage.core.enums.ImageFileTypeEnum;
import org.dromara.x.file.storage.core.enums.TemporaryTypeEnum;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.presigned.GeneratePresignedUrlPretreatment;
import org.dromara.x.file.storage.spring.SpringFileStorageProperties;
import org.dromara.x.file.storage.spring.constant.MetadataKeyConst;
import org.dromara.x.file.storage.spring.domain.FileDetail;
import org.dromara.x.file.storage.spring.service.FileDetailService;
import org.dromara.x.file.storage.spring.service.XFileExtensionService;

@Slf4j
public class XFileExtensionServiceImpl implements XFileExtensionService {

    private FileDetailService fileDetailService;
    private FileStorageService fileStorageService;

    private SpringFileStorageProperties fileStorageProperties;

    public void setFileDetailService(FileDetailService fileDetailService) {
        this.fileDetailService = fileDetailService;
    }

    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public void setSpringFileStorageProperties(SpringFileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @SneakyThrows
    @Override
    public String createByMetadata(Map<String, String> metadata) {

        if (StringUtils.isBlank(metadata.get(MetadataKeyConst.PLATFORM_KEY))) {
            throw new Exception("存储平台编号不能为空");
        }
        if (StringUtils.isBlank(metadata.get(MetadataKeyConst.FILE_TYPE_KEY))) {
            throw new Exception("存储文件业务类型不能为空");
        }
        FileDetail detail = fileDetailService.toFileDetail(buildByMetadata(metadata));
        boolean b = fileDetailService.save(detail);
        if (b) {
            return detail.getId();
        }
        return null;
    }

    @SneakyThrows
    @Override
    public boolean updateMetadata(String storageCode, Map<String, String> metadata) {

        FileDetail detail = fileDetailService.getOne(new QueryWrapper<FileDetail>().eq(FileDetail.COL_ID, storageCode));
        detail.setMetadata(fileDetailService.valueToJson(metadata));

        return fileDetailService.updateById(detail);
    }

    @Override
    public String getMetadata(String storageCode) {
        FileDetail fileDetail = fileDetailService.getById(storageCode);
        if (fileDetail == null) {
            return null;
        }
        return fileDetail.getMetadata();
    }

    @Override
    public String getDicomMetadata(String storageCode) {
        FileInfo fileInfo = getById(storageCode);
        if (fileInfo == null || ObjectUtil.isEmpty(fileInfo.getMetadata())) {
            return null;
        }

        return fileInfo.getMetadata().get("DICOM_METADATA_KEY");
    }

    @Override
    public FileInfo getById(String storageCode) {
        try {
            return fileDetailService.toFileInfo(
                    fileDetailService.getOne(new QueryWrapper<FileDetail>().eq(FileDetail.COL_ID, storageCode)));
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public String generatePresignedUrl(String storageCode) {

        FileInfo fileInfo = getById(storageCode);
        if (fileInfo == null) {
            return null;
        }
        return generatePresignedUrl(fileInfo);
    }

    @Override
    public String generatePresignedUrl(FileInfo fileInfo) {

        if (fileInfo.getPlatform().toLowerCase().contains("local")) {
            return getLocalPresignedUrl(fileInfo, 604800);
        }
        FileStorage fileStorage = fileStorageService.getFileStorage(fileInfo.getPlatform());

        if (!fileStorage.isSupportPresignedUrl()) {
            log.error("该存储平台暂不支持生成预签名 URL{}", fileInfo.getPlatform());
            return null;
        }

        GeneratePresignedUrlPretreatment pretreatment = new GeneratePresignedUrlPretreatment()
                .setPath(fileInfo.getPath())
                .setFilename(fileInfo.getFilename())
                .setMethod(Constant.GeneratePresignedUrl.Method.GET)
                // .putHeaders(Constant.Metadata.CONTENT_TYPE,fileInfo.getContentType()) // 这个不能指定
                .setExpiration(DateUtil.offsetDay(new Date(), 7))
                .setPlatform(fileInfo.getPlatform());

        return fileStorage.generatePresignedUrl(pretreatment).getUrl();
    }

    /**
     * 自定义临时访问链接
     * @param fileInfo     文档信息对象
     * @param expireSecond 过期时间
     * @return String      临时访问链接地址
     * @throws Exception   异常信息对象
     */
    String getLocalPresignedUrl(FileInfo fileInfo, long expireSecond) {
        // 默认七天
        if (expireSecond == 0) {
            expireSecond = 604800;
        }

        String ossKey = fileInfo.getPath() + fileInfo.getFilename();

        long expire = Instant.now().plusSeconds(expireSecond).getEpochSecond();
        String raw = DownloadFlagEnum.NOT_DOWNLOAD.value() + expire + ossKey + Constant.FILE_DOWNLOAD_URI_SALT;
        String md5 = SecureUtil.md5(raw);

        return String.format(
                fileStorageProperties.getLocalPlus().get(0).getDomain()
                        + "/xfile/tempview?downloadFlag=%s&key=%s&expire=%s&signature=%s&storageTypeEnum=%s",
                DownloadFlagEnum.NOT_DOWNLOAD.value(),
                ossKey,
                expire,
                md5,
                fileInfo.getPlatform());
    }

    @Override
    public String generatePresignedUrl(String storageCode, int expirationTime) {
        FileInfo fileInfo = getById(storageCode);
        if (fileInfo == null) {
            return null;
        }
        return generatePresignedUrl(fileInfo, expirationTime);
    }

    @Override
    public String generatePresignedUrl(FileInfo fileInfo, int expirationTime) {
        if (fileInfo.getPlatform().toLowerCase().contains("local")) {
            return getLocalPresignedUrl(fileInfo, expirationTime);
        }
        FileStorage fileStorage = fileStorageService.getFileStorage(fileInfo.getPlatform());

        if (!fileStorage.isSupportPresignedUrl()) {
            log.error("该存储平台暂不支持生成预签名 URL {}", fileInfo.getPlatform());
        }

        GeneratePresignedUrlPretreatment pretreatment = new GeneratePresignedUrlPretreatment()
                .setPath(fileInfo.getPath())
                .setFilename(fileInfo.getFilename())
                .setMethod(Constant.GeneratePresignedUrl.Method.GET)
                .setExpiration(DateUtil.offsetSecond(new Date(), expirationTime))
                .setPlatform(fileInfo.getPlatform());

        return fileStorage.generatePresignedUrl(pretreatment).getUrl();
    }

    @Override
    public int freeze(String storageCode) {
        // todo
        return 200;
    }

    @Override
    public int freeze(FileInfo fileInfo) {
        // todo
        return 200;
    }

    @Override
    public boolean delete(String storageCode) {
        try {
            FileInfo fileInfo = fileDetailService.toFileInfo(
                    fileDetailService.getOne(new QueryWrapper<FileDetail>().eq(FileDetail.COL_ID, storageCode)));
            return fileStorageService.delete(fileInfo);
        } catch (Exception e) {
            log.error("删除文档失败,失败原因{}", e);
        }

        return false;
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        return fileStorageService.delete(fileInfo);
    }

    @Override
    public void deleteBatch(String storageCode, Set<String> ossKeys, ImageFileTypeEnum fileTypeEnum) {
        if (CollUtil.isEmpty(ossKeys)) {
            return;
        }

        try {
            FileInfo fileInfo = getById(storageCode);

            Set<String> allKeys = expandKeysByType(ossKeys, fileTypeEnum);

            for (String ossKey : allKeys) {
                try {
                    fileInfo.setPath(StrUtil.sub(ossKey, 0, ossKey.lastIndexOf("/") + 1));
                    fileInfo.setFilename(FileUtil.getName(ossKey));
                    delete(fileInfo);
                } catch (Exception e) {
                    log.warn("批量删除失败：类型={}，ossKey={}，错误={}", fileTypeEnum.name(), ossKey, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("XFILE 批量删除失败，storageCode={}，类型={}，错误={}", storageCode, fileTypeEnum.name(), e.getMessage(), e);
        }
    }

    private Set<String> expandKeysByType(Set<String> ossKeys, ImageFileTypeEnum fileTypeEnum) {

        Map<ImageFileTypeEnum, String[][]> suffixMapping = new HashMap<>();
        suffixMapping.put(ImageFileTypeEnum.DICOM, new String[][] {{".dcm", ".jpg"}});
        suffixMapping.put(ImageFileTypeEnum.REPORT, new String[][] {{".png", ".pdf"}});

        Set<String> allKeys = new HashSet<>(ossKeys);
        String[][] mappings = suffixMapping.get(fileTypeEnum);
        if (mappings != null) {
            for (String[] mapping : mappings) {
                String fromSuffix = mapping[0];
                String toSuffix = mapping[1];
                ossKeys.stream()
                        .filter(key -> key.endsWith(fromSuffix))
                        .map(key -> key.replace(fromSuffix, toSuffix))
                        .forEach(allKeys::add);
            }
        }
        return allKeys;
    }

    @Override
    public byte[] downLoadBytes(FileInfo fileInfo) {
        return fileStorageService.download(fileInfo).bytes();
    }

    @Override
    public boolean exists(String storageCode) {
        FileInfo fileInfo = getById(storageCode);
        return fileStorageService.exists(fileInfo);
    }

    @Override
    public boolean exists(FileInfo fileInfo) {
        return fileStorageService.exists(fileInfo);
    }

    @Override
    public void downLoadOutStream(String storageCode, OutputStream out) {
        FileInfo fileInfo = getById(storageCode);
        fileStorageService.download(fileInfo).outputStream(out);
    }

    @Override
    public void downLoadOutStream(FileInfo fileInfo, OutputStream out) {
        fileStorageService.download(fileInfo).outputStream(out);
    }

    @Override
    public FileInfo uploadTemporary(InputStream inputStream, String filename, long expirationTime) {
        FileInfo fileInfo =
                fileStorageService.of(inputStream).setOriginalFilename(filename).upload();
        FileDetail fileDetail;
        try {
            fileDetail = fileDetailService.toFileDetail(fileInfo);
            fileDetail.setTemporary(TemporaryTypeEnum.TEMPORARY.value());
            fileDetail.setExpireTime(DateUtil.offsetSecond(new Date(), (int) expirationTime));
            fileDetailService.updateById(fileDetail);
        } catch (Exception e) {
            log.warn("更新临时时间报错,文档ID为：{}", fileInfo.getId());
        }
        return fileInfo;
    }

    @Override
    public boolean becomePerpetual(String storageCode) {
        FileInfo fileInfo = getById(storageCode);
        FileDetail fileDetail;
        try {
            fileDetail = fileDetailService.toFileDetail(fileInfo);
            fileDetail.setTemporary(TemporaryTypeEnum.PERPETUAL.value());
            fileDetailService.updateById(fileDetail);
        } catch (Exception e) {
            log.warn("更新临时时间报错,文档ID为：{}", fileInfo.getId());
            return false;
        }
        return true;
    }

    FileInfo buildByMetadata(Map<String, String> metadata) throws Exception {
        // 注意本地存储方式需要另行处理
        // http://127.0.0.1:12001/file/minio/data/cloud/CT/2025-05-30/116733793/12410200010109072411696015156106830840116733793/685a531577ed8a9004e8ee14.dcm
        FileInfo info = new FileInfo();

        if (ImageFileTypeEnum.FILM.value().toString().equals(metadata.get(MetadataKeyConst.FILE_TYPE_KEY))
                || ImageFileTypeEnum.REPORT.value().toString().equals(metadata.get(MetadataKeyConst.FILE_TYPE_KEY))) {
            if (StringUtils.isBlank(metadata.get(MetadataKeyConst.OSS_PATH_KEY))) {
                throw new Exception("文档ossKey不能为空");
            }
            info.setPath(StrUtil.sub(
                    metadata.get(MetadataKeyConst.OSS_PATH_KEY),
                    0,
                    metadata.get(MetadataKeyConst.OSS_PATH_KEY).lastIndexOf("/") + 1));
            info.setFilename(FileUtil.getName(metadata.get(MetadataKeyConst.OSS_PATH_KEY)));
            if (metadata.get(metadata.get(MetadataKeyConst.PLATFORM_KEY))
                    .toLowerCase()
                    .contains("local")) {
                // 临时处理访问方式
                info.setUrl(fileStorageProperties.getLocalPlus().get(0).getDomain()
                        + metadata.get(MetadataKeyConst.OSS_PATH_KEY));
            } else {
                info.setUrl(metadata.get(MetadataKeyConst.OSS_PATH_KEY));
            }
            info.setExt("png");
            info.setContentType("image/jpeg");
        }
        if (ImageFileTypeEnum.DICOM.value().toString().equals(metadata.get(MetadataKeyConst.FILE_TYPE_KEY))) {
            info.setExt("dcm"); // 最好有值
            info.setContentType("application/dicom");
            if (StringUtils.isBlank(metadata.get("DICOM_METADATA_KEY"))) {
                throw new Exception("影像元数据不能为空");
            }
        }
        info.setMetadata(metadata);

        info.setPlatform(metadata.get(metadata.get(MetadataKeyConst.PLATFORM_KEY))); // 必填

        return info;
    }
}
