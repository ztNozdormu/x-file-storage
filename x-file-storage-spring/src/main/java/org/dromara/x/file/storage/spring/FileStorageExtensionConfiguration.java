package org.dromara.x.file.storage.spring;

import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.spring.dao.FileDetailMapper;
import org.dromara.x.file.storage.spring.dao.FilePartDetailMapper;
import org.dromara.x.file.storage.spring.service.FileDetailService;
import org.dromara.x.file.storage.spring.service.FilePartDetailService;
import org.dromara.x.file.storage.spring.service.XFileExtensionService;
import org.dromara.x.file.storage.spring.service.impl.XFileExtensionServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@MapperScan("org.dromara.x.file.storage.spring.dao")
@ConditionalOnMissingBean(XFileExtensionService.class)
public class FileStorageExtensionConfiguration {

    // 自动注册分片服务
    @Bean
    @ConditionalOnMissingBean
    public FilePartDetailService filePartDetailService(FilePartDetailMapper filePartDetailMapper) {
        return new FilePartDetailService(filePartDetailMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public FileDetailService fileDetailService(
            FilePartDetailService filePartDetailService, FileDetailMapper fileDetailMapper) {
        FileDetailService service = new FileDetailService(fileDetailMapper);
        service.setFilePartDetailService(filePartDetailService);
        return service;
    }

    // 注册扩展服务，并确保依赖注入
    @Bean
    @ConditionalOnMissingBean
    public XFileExtensionService xFileExtensionServiceImpl(
            FileDetailService fileDetailService, FileStorageService fileStorageService) {
        XFileExtensionServiceImpl impl = new XFileExtensionServiceImpl();
        impl.setFileDetailService(fileDetailService);
        impl.setFileStorageService(fileStorageService);
        return impl;
    }
}
