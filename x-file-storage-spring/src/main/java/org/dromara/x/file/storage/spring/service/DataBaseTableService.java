package org.dromara.x.file.storage.spring.service;

import java.util.List;

public interface DataBaseTableService {
    /**
     * 查询创建表的sql
     *
     * @param tableName
     * @return
     */
    String showTableCreateSql(String tableName);

    /**
     * 创建表
     *
     * @param createTableName 需要创建的表名
     * @param baseTableName   基础表名
     * @return
     */
    boolean execCreateTable(String createTableName, String baseTableName);

    /**
     * 检查表是否存在 true为存在
     *
     * @param tableName
     * @return
     */
    boolean existsTable(String tableName);

    /**
     * 如果表不存在就创建表 否则就什么都不做
     *
     * @param createTableName 需要创建的表名
     * @param baseTableName   基础表名
     */
    boolean notExistsCreateTable(String createTableName, String baseTableName);

    /**
     * 模糊查询表名
     *
     * @param tableName
     * @return
     */
    List<String> findLikeTableName(String tableName);

    void dropTable(String tableName);
}
