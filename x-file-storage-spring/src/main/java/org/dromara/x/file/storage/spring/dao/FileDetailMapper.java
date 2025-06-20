package org.dromara.x.file.storage.spring.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.x.file.storage.spring.domain.FileDetail;

@Mapper
public interface FileDetailMapper extends BaseMapper<FileDetail> {}
