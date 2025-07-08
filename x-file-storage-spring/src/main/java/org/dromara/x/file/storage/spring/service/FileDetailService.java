package org.dromara.x.file.storage.spring.service;

import org.dromara.x.file.storage.core.FileInfo;

public interface FileDetailService {

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
}
