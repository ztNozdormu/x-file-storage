package org.dromara.x.file.storage.spring.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.dromara.x.file.storage.spring.BaseTableNameEnum;
import org.dromara.x.file.storage.spring.dao.DataBaseTableMapper;
import org.dromara.x.file.storage.spring.service.DataBaseTableService;
import org.dromara.x.file.storage.spring.service.impl.DataBaseTableServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus配置
 *
 * @author qc
 */
@Configuration
@MapperScan("com.knd.**.dao")
public class KndMybatisProperties {
    public static final ThreadLocal<String> TABLE_NAME = new ThreadLocal<>();

    @Autowired
    private DataSource dataSource;

    //    @Bean
    //    public MybatisPlusInterceptor mybatisPlusInterceptor() {
    //        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
    //        // 动态表名配置
    //        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new
    // DynamicTableNameInnerInterceptor();
    //        Map<String, TableNameHandler> tableNameHandlerMap = new HashMap<>();
    //        // 设置需要动态替换的表名
    //        BaseTableNameEnum.getAllBaseTableName()
    //                .forEach(e -> tableNameHandlerMap.put(e, (sql, tableName) -> Optional.ofNullable(TABLE_NAME.get())
    //                        .orElse(tableName)));
    //        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(tableNameHandlerMap);
    //        // 添加拦截器
    //        mybatisPlusInterceptor.addInnerInterceptor(new DynamicCreateTableInterceptor());
    //        mybatisPlusInterceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
    //        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    //        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    //        return mybatisPlusInterceptor;
    //    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 动态表名配置
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        Map<String, TableNameHandler> tableNameHandlerMap = new HashMap<>();

        // ✅ 判断是否是达梦数据库
        boolean isDM = isDMDatabase();

        // 遍历所有表名枚举
        BaseTableNameEnum.getAllBaseTableName().forEach(e -> {
            if (isDM) {
                // 达梦专用表名：schema.tableName
                String schemaTable = "YYX_PRE." + e;
                tableNameHandlerMap.put(
                        schemaTable,
                        (sql, tableName) -> "YYX_PRE."
                                + Optional.ofNullable(TABLE_NAME.get()).orElse(e));
            }

            // 通用表名
            tableNameHandlerMap.put(
                    e, (sql, tableName) -> Optional.ofNullable(TABLE_NAME.get()).orElse(tableName));
        });

        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(tableNameHandlerMap);
        // 添加拦截器
        //        mybatisPlusInterceptor.addInnerInterceptor(new DynamicCreateTableInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);

        if (isDM) {
            PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
            paginationInnerInterceptor.setDbType(DbType.DM);
            paginationInnerInterceptor.setOverflow(true);
            mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        } else {
            mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        }
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    private boolean isDMDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            String driverName = connection.getMetaData().getDriverName().toLowerCase();
            return driverName.contains("dm")
                    || connection.getMetaData().getURL().toLowerCase().contains("dm");
        } catch (Exception e) {
            throw new RuntimeException("无法判断数据库类型", e);
        }
    }

    // 自动注册分片服务
    @Bean
    @ConditionalOnMissingBean
    public DataBaseTableService dataBaseTableService(DataBaseTableMapper dataBaseTableMapper, DataSource dataSource) {
        return new DataBaseTableServiceImpl(dataBaseTableMapper, dataSource);
    }
}
