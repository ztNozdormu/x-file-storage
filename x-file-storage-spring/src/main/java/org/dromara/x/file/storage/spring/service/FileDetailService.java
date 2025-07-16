package org.dromara.x.file.storage.spring.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.spring.domain.FileDetail;

public interface FileDetailService extends IService<FileDetail> {

    /**
     * 保存文档信息到指定表
     * @param info         文档信息对象
     * @param tableSuffix  表后缀名称 hospitalId+"_"+year
     * @return
     */
    boolean save(FileInfo info, String tableSuffix);

    /**
     * 更新指定表文件记录，可以根据文件 ID 或 URL 来更新文件记录，
     * 主要用在手动分片上传文件-完成上传，作用是更新文件信息
     *
     * @param fileInfo         文档信息对象
     * @param tableSuffix  表后缀名称 hospitalId+"_"+year
     */
    void update(FileInfo fileInfo, String tableSuffix);

    /**
     * 根据 url 获取指定表文件记录
     * @param url         文档信息对象
     * @param tableSuffix  表后缀名称 hospitalId+"_"+year
     */
    FileInfo getByUrl(String url, String tableSuffix);

    /**
     * 根据 url 删除指定表文件记录
     * @param url         文档信息对象
     * @param tableSuffix  表后缀名称 hospitalId+"_"+year
     */
    boolean delete(String url, String tableSuffix);

    /**
     * 文档信息对象--文档具体信息转换
     * @param info FileInfo 文档信息对象
     * @return FileDetail   文档具体信息对象
     * @throws JsonProcessingException
     */
    FileDetail toFileDetail(FileInfo info) throws JsonProcessingException;

    /**
     * 文档具体信息--文档信息对象转换
     * @param detail
     * @return
     * @throws JsonProcessingException
     */
    FileInfo toFileInfo(FileDetail detail) throws JsonProcessingException;

    /**
     * 将指定值转换为json字符串
     * @param value 指定的数据对象
     * @return
     * @throws JsonProcessingException
     */
    String valueToJson(Object value) throws JsonProcessingException;
}
