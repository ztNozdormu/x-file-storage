package org.dromara.x.file.storage.spring.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.tika.utils.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.constant.Constant;
import org.dromara.x.file.storage.core.enums.DownloadFlagEnum;
import org.dromara.x.file.storage.core.file.HttpServletRequestFileWrapper;
import org.dromara.x.file.storage.core.file.MultipartFormDataReader;
import org.dromara.x.file.storage.core.hash.HashInfo;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.platform.LocalPlusFileStorage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("/xfile")
public class FileDetailController {

    private final FileStorageService fileStorageService;

    public FileDetailController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * 上传文件，成功返回文件 url
     */
    @PostMapping("/upload")
    @ResponseBody
    public String upload(MultipartFile file) {
        FileInfo fileInfo = fileStorageService
                .of(file)
                .setPath("upload/") // 保存到相对路径下，为了方便管理，不需要可以不写
                .setObjectId("0") // 关联对象id，为了方便管理，不需要可以不写
                .setObjectType("0") // 关联对象类型，为了方便管理，不需要可以不写
                .upload(); // 将文件上传到对应地方
        return fileInfo == null ? "上传失败！" : fileInfo.getUrl();
    }

    /**
     * 上传图片，成功返回文件信息
     * 图片处理使用的是 https://github.com/coobird/thumbnailator
     */
    @PostMapping("/upload-image")
    @ResponseBody
    public FileInfo uploadImage(MultipartFile file) {
        return fileStorageService
                .of(file)
                .image(img -> img.size(1000, 1000)) // 将图片大小调整到 1000*1000
                .thumbnail(th -> th.size(200, 200)) // 再生成一张 200*200 的缩略图
                .upload();
    }

    /**
     * 上传文件到指定存储平台，成功返回文件信息
     */
    @PostMapping("/upload-platform")
    @ResponseBody
    public FileInfo uploadPlatform(MultipartFile file) {
        return fileStorageService
                .of(file)
                .setPlatform("aliyun-oss-1") // 使用指定的存储平台
                .upload();
    }

    /**
     * 直接读取 HttpServletRequest 中的文件进行上传，成功返回文件信息
     */
    @PostMapping("/upload-request")
    @ResponseBody
    public FileInfo uploadPlatform(HttpServletRequest request) {
        HttpServletRequestFileWrapper wrapper = (HttpServletRequestFileWrapper) fileStorageService.wrapper(request);
        MultipartFormDataReader.MultipartFormData formData = wrapper.getMultipartFormData();
        Map<String, String[]> parameterMap = formData.getParameterMap();
        log.info("parameterMap：{}", parameterMap);
        return fileStorageService.of(wrapper).upload();
    }

    /**
     * 上传文件，成功返回文件 url
     */
    @PostMapping("/upload-hash")
    @ResponseBody
    public FileInfo uploadHash(MultipartFile file) throws IOException {
        FileInfo fileInfo = fileStorageService.of(file).setHashCalculatorMd5().upload(); // 将文件上传到对应地方

        HashInfo hashInfo = fileInfo.getHashInfo();
        //        Assert.isTrue(SecureUtil.md5().digestHex(file.getBytes()).equals(hashInfo.getMd5()), "文件 MD5 对比不一致！");
        log.info("文件 MD5 对比通过");
        return fileInfo;
    }

    /**
     * 本地存储平台--临时访问链接--接口
     * @param downloadFlag 下载方式标记
     * @param key          关键信息Key(胶片报告-storageCode) (影像--storageCode_ossPath)
     * @param expire       过期时间 秒单位
     * @param signature    签名
     * @param platform     平台名称
     * @param response     响应结果对象
     * @throws Exception   异常信息对象
     */
    //    @GetMapping("/tempview")
    //    public void getUrl(
    //            @RequestParam("downloadFlag") Integer downloadFlag,
    //            @RequestParam("key") String key,
    //            @RequestParam("expire") Long expire,
    //            @RequestParam("signature") String signature,
    //            @RequestParam("platform") String platform,
    //            HttpServletResponse response)
    //            throws Exception {
    //
    //        // 参数校验
    //        if (expire == null || downloadFlag == null || StringUtils.isBlank(key) || StringUtils.isBlank(signature))
    // {
    //            throw new Exception("参数非法!");
    //        }
    //        String raw = downloadFlag + expire + key + Constant.FILE_DOWNLOAD_URI_SALT;
    //        String md5 = SecureUtil.md5(raw);
    //        if (!Objects.equals(md5, signature)) {
    //            throw new Exception("文档临时访问链接参数非法!");
    //        }
    //        // 校验过期时间
    //        if (Instant.ofEpochSecond(expire).isBefore(Instant.now())) {
    //            throw new Exception("文档临时访问链接已过期!");
    //        }
    //        response.setContentType("application/octet-stream");
    //        try {
    //
    //            String filename = key.substring(key.lastIndexOf(StringPool.SLASH) + 1);
    //            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename,
    // "UTF-8"));
    //            FileStorage fileStorage = fileStorageService.getFileStorage(platform);
    //            if (fileStorage instanceof LocalPlusFileStorage) {
    //                FileInfo fileInfo = new FileInfo();
    //
    //                fileInfo.setPath(key.replace(filename, ""));
    //                fileInfo.setFilename(filename);
    //                fileInfo.setPlatform(platform);
    //                fileInfo.setContentType("application/dicom");
    //                fileInfo.setExt("dcm");
    //                fileStorageService.download(fileInfo).outputStream(response.getOutputStream());
    //            }
    //
    //        } catch (IOException e) {
    //            log.error(String.format("【云存储】获取文件出错,key: %s", key), e);
    //            throw new Exception(e.getLocalizedMessage());
    //        }
    //    }
    @CrossOrigin(origins = "*")
    @GetMapping("/tempview")
    public void getUrl(
            @RequestParam("downloadFlag") Integer downloadFlag,
            @RequestParam("key") String key,
            @RequestParam("expire") Long expire,
            @RequestParam("signature") String signature,
            @RequestParam("platform") String platform,
            HttpServletResponse response)
            throws IOException {

        // 参数校验
        if (expire == null || downloadFlag == null || StringUtils.isBlank(key) || StringUtils.isBlank(signature)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "参数非法");
            return;
        }

        // 签名校验
        String raw = downloadFlag + expire + key + Constant.FILE_DOWNLOAD_URI_SALT;
        String md5 = SecureUtil.md5(raw);
        if (!Objects.equals(md5, signature)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "文档临时访问链接参数非法");
            return;
        }

        // 过期时间校验
        if (Instant.ofEpochSecond(expire).isBefore(Instant.now())) {
            response.sendError(HttpServletResponse.SC_GONE, "文档临时访问链接已过期");
            return;
        }

        // 设置下载响应头
        response.setContentType("application/octet-stream");
        String filename = key.substring(key.lastIndexOf(StringPool.SLASH) + 1);

        if (Objects.equals(DownloadFlagEnum.DOWNLOAD.value(), downloadFlag)) {
            // 下载模式
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        } else {
            // 预览模式（也可以省略不设置）
            response.setHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(filename, "UTF-8"));
        }

        try {
            FileStorage fileStorage = fileStorageService.getFileStorage(platform);
            if (fileStorage instanceof LocalPlusFileStorage) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setPath(key.substring(0, key.lastIndexOf(filename)));
                fileInfo.setFilename(filename);
                fileInfo.setPlatform(platform);
                fileInfo.setContentType("application/dicom");
                fileInfo.setExt("dcm");

                // 下载并写入响应
                fileStorageService.download(fileInfo).outputStream(response.getOutputStream());
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "不支持的存储平台类型");
            }

        } catch (ClientAbortException e) {
            log.warn("客户端中止下载，key: {}", key);
        } catch (IOException e) {
            log.error("文件下载失败，key: {}", key, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "文件下载失败");
        } catch (Exception e) {
            log.error("未知错误，key: {}", key, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "未知错误：" + e.getMessage());
        }
    }

    // TODO 删除
}
