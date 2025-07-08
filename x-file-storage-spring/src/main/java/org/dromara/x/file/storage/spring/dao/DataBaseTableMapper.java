package org.dromara.x.file.storage.spring.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DataBaseTableMapper {

    Map<String, String> showTableCreateSql(String tableName);

    List<String> getTableName(String tableName);

    int execSql(String sql);

    int exitsTableCreateSql(@Param("dataBaseName") String dataBaseName, @Param("tableName") String tableName);

    /**
     * 删除表
     *
     * @param tableName
     * @return
     */
    void dropTable(String tableName);
}
