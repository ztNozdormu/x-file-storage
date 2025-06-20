package org.dromara.x.file.storage.spring.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.constant.Constant;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.presigned.GeneratePresignedUrlPretreatment;
import org.dromara.x.file.storage.spring.domain.FileDetail;
import org.dromara.x.file.storage.spring.service.FileDetailService;
import org.dromara.x.file.storage.spring.service.XFileExtensionService;

@Slf4j
public class XFileExtensionServiceImpl implements XFileExtensionService {

    private FileDetailService fileDetailService;
    private FileStorageService fileStorageService;

    public void setFileDetailService(FileDetailService fileDetailService) {
        this.fileDetailService = fileDetailService;
    }

    public void setFileStorageService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @SneakyThrows
    @Override
    public String createByMetadata(Map<String, String> metadata) {

        if (StringUtils.isBlank(metadata.get("platformId"))) {
            throw new Exception("存储平台编号不能为空");
        }
        if (StringUtils.isBlank(metadata.get("fileType"))) {
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

    @SneakyThrows
    @Override
    public FileInfo getById(String storageCode) {
        return fileDetailService.toFileInfo(
                fileDetailService.getOne(new QueryWrapper<FileDetail>().eq(FileDetail.COL_ID, storageCode)));
    }

    @SneakyThrows
    @Override
    public String generatePresignedUrl(String storageCode) {

        FileInfo fileInfo = getById(storageCode);

        return generatePresignedUrl(fileInfo);
    }

    @Override
    public String generatePresignedUrl(FileInfo fileInfo) {
        FileStorage fileStorage = fileStorageService.getFileStorage(fileInfo.getPlatform());

        if (!fileStorage.isSupportPresignedUrl()) {
            log.error("该存储平台暂不支持生成预签名 URL{}", fileInfo.getPlatform());
        }

        GeneratePresignedUrlPretreatment pretreatment = new GeneratePresignedUrlPretreatment()
                .setPath(fileInfo.getPath())
                .setFilename(fileInfo.getFilename())
                .setMethod(Constant.GeneratePresignedUrl.Method.GET)
                //                .putHeaders(Constant.Metadata.CONTENT_TYPE,fileInfo.getContentType()) // 这个不能指定
                .setExpiration(DateUtil.offsetDay(new Date(), 7))
                .setPlatform(fileInfo.getPlatform());

        return fileStorage.generatePresignedUrl(pretreatment).getUrl();
    }

    @SneakyThrows
    @Override
    public boolean delete(String storageCode) {
        FileInfo fileInfo = fileDetailService.toFileInfo(
                fileDetailService.getOne(new QueryWrapper<FileDetail>().eq(FileDetail.COL_ID, storageCode)));
        return fileStorageService.delete(fileInfo);
    }

    @Override
    public boolean delete(FileInfo fileInfo) {
        return fileStorageService.delete(fileInfo);
    }

    @Override
    public byte[] downLoadBytes(FileInfo fileInfo) {
        return fileStorageService.download(fileInfo).bytes();
    }

    @Override
    public void downLoadOutStream(FileInfo fileInfo, ByteArrayOutputStream out) {
        fileStorageService.download(fileInfo).outputStream(out);
    }

    FileInfo buildByMetadata(Map<String, String> metadata) throws Exception {

        FileInfo info = new FileInfo();

        if (metadata.get("fileType").equals("1") || metadata.get("fileType").equals("2")) {
            if (StringUtils.isBlank(metadata.get("ossKey"))) {
                throw new Exception("文档ossKey不能为空");
            }
            info.setPath(StrUtil.sub(
                    metadata.get("ossKey"), 0, metadata.get("ossKey").lastIndexOf("/") + 1));
            info.setFilename(FileUtil.getName(metadata.get("ossKey")));
            info.setUrl(metadata.get("ossKey"));
            info.setExt("png");
            info.setContentType("image/jpeg");
        }
        if (metadata.get("fileType").equals("3")) {
            info.setExt("dcm"); // 最好有值
            info.setContentType("application/dicom");
            if (StringUtils.isBlank(metadata.get("biz_info_key"))) {
                throw new Exception("影像元数据不能为空");
            }
        }
        info.setMetadata(metadata);

        info.setPlatform(metadata.get("platformId")); // 必填

        return info;
    }
}
