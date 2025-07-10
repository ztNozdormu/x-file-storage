package org.dromara.x.file.storage.spring.mybatis;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.dromara.x.file.storage.spring.MybatisPlusUtils;

/**
 * 动态创建表拦截器
 *
 * @author qc
 */
@Slf4j
public class DynamicCreateTableInterceptor implements InnerInterceptor {

    /**
     * 动态创建表
     *
     * @param sh
     * @param connection
     * @param transactionTimeout
     */
    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        String dynamicTableName = MybatisPlusUtils.getDynamicTableName();
        if (StringUtils.isBlank(dynamicTableName)) {
            return;
        }
        String baseTableName = MybatisPlusUtils.getBaseTableName(dynamicTableName);
        if (StringUtils.isNotBlank(baseTableName)) {
            try {
                String dataBase = connection.getCatalog();
                String sql = "SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = ? AND TABLE_NAME = ?";
                //                String sql = "SELECT count(1) FROM information_schema.tables WHERE table_schema=? AND
                // table_name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, dataBase);
                preparedStatement.setString(2, dynamicTableName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // 获取表是否存在
                    int count = resultSet.getInt(1);
                    close(preparedStatement, resultSet);
                    // 如果表不存在
                    if (count == 0) {
                        sql = "SHOW CREATE TABLE " + baseTableName;
                        // 获取创建表语句
                        preparedStatement = connection.prepareStatement(sql);
                        resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            String createTableSql = resultSet.getString(2);
                            close(preparedStatement, resultSet);
                            // 创建表
                            sql = createTableSql.replaceFirst(baseTableName, dynamicTableName);
                            preparedStatement = connection.prepareStatement(sql);
                            preparedStatement.executeUpdate();
                            close(preparedStatement, resultSet);
                            log.info("【动态创建表成功】表名:{}", dynamicTableName);
                        } else {
                            close(preparedStatement, resultSet);
                        }
                    }
                } else {
                    close(preparedStatement, resultSet);
                }
            } catch (Exception e) {
                log.error(String.format("【动态创建表失败】表名: %s", dynamicTableName), e);
            }
        }
    }

    /**
     * 关闭资源
     *
     * @param preparedStatement
     * @param resultSet
     */
    private void close(PreparedStatement preparedStatement, ResultSet resultSet) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                log.error("关闭PreparedStatement异常", e);
            }
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("关闭ResultSet异常", e);
            }
        }
    }
}
