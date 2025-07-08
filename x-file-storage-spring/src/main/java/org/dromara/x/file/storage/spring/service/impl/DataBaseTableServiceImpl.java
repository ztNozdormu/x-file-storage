package org.dromara.x.file.storage.spring.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.spring.MybatisPlusUtils;
import org.dromara.x.file.storage.spring.dao.DataBaseTableMapper;
import org.dromara.x.file.storage.spring.service.DataBaseTableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class DataBaseTableServiceImpl implements DataBaseTableService {
    private DataBaseTableMapper dataBaseTableMapper;

    private String dataBase;

    public DataBaseTableServiceImpl(DataBaseTableMapper dataBaseTableMapper, DataSource dataSource) {
        this.dataBaseTableMapper = dataBaseTableMapper;
        try {
            this.dataBase = dataSource.getConnection().getCatalog();
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("获取数据库名出现异常", e);
        }
    }

    @Override
    public String showTableCreateSql(String tableName) {
        Map<String, String> map = dataBaseTableMapper.showTableCreateSql(tableName);
        return map.get("Create Table");
    }

    @Override
    public boolean execCreateTable(String createTableName, String baseTableName) {
        if (!existsTable(baseTableName)) {
            return false;
        }
        MybatisPlusUtils.setDynamicTableName(baseTableName);
        String createsql = showTableCreateSql(baseTableName);
        createsql = createsql.replaceFirst(baseTableName, createTableName);
        return dataBaseTableMapper.execSql(createsql) > 0;
    }

    @Override
    public boolean existsTable(String tableName) {
        int rows = dataBaseTableMapper.exitsTableCreateSql(dataBase, tableName);
        return rows > 0;
    }

    @Override
    public boolean notExistsCreateTable(String createTableName, String baseTableName) {
        // 如果表存在
        if (existsTable(createTableName)) {
            return true;
        } else {
            return execCreateTable(createTableName, baseTableName);
        }
    }

    @Override
    public List<String> findLikeTableName(String tableName) {
        return dataBaseTableMapper.getTableName(tableName);
    }

    @Override
    public void dropTable(String tableName) {
        dataBaseTableMapper.dropTable(tableName);
    }
}
