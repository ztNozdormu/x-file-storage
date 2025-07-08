package org.dromara.x.file.storage.spring.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.spring.BaseTableNameEnum;
import org.dromara.x.file.storage.spring.dao.DataBaseTableMapper;
import org.dromara.x.file.storage.spring.service.DataBaseTableService;
import org.dromara.x.file.storage.spring.service.impl.DataBaseTableServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
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
@Slf4j
public class KndMybatisProperties {
    public static final ThreadLocal<String> TABLE_NAME = new ThreadLocal<>();

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        // 动态表名配置
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        Map<String, TableNameHandler> tableNameHandlerMap = new HashMap<>();
        // 设置需要动态替换的表名-MySQL模式
        //        BaseTableNameEnum.getAllBaseTableName().forEach(e ->
        //                tableNameHandlerMap.put(e, (sql, tableName) ->
        //                        Optional.ofNullable(TABLE_NAME.get()).orElse(tableName)));
        BaseTableNameEnum.getAllBaseTableName().forEach(e -> {
            // 1. 处理带模式名的表 (YYX_PRE.hos_collect_task)
            String schemaTable = "YYX_PRE." + e; // 达梦专用格式
            tableNameHandlerMap.put(
                    schemaTable,
                    (sql, tableName) ->
                            "YYX_PRE." + Optional.ofNullable(TABLE_NAME.get()).orElse(e));

            // 2. 处理普通表名 (hos_collect_task)
            tableNameHandlerMap.put(
                    e, (sql, tableName) -> Optional.ofNullable(TABLE_NAME.get()).orElse(tableName));
            log.info("表名替换：{} -> {}", e, Optional.ofNullable(TABLE_NAME.get()).orElse(e));
        });

        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(tableNameHandlerMap);
        // 添加拦截器
        mybatisPlusInterceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.DM);
        paginationInnerInterceptor.setOverflow(true);
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        mybatisPlusInterceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return mybatisPlusInterceptor;
    }

    // 自动注册分片服务
    @Bean
    @ConditionalOnMissingBean
    public DataBaseTableService dataBaseTableService(DataBaseTableMapper dataBaseTableMapper) {
        return new DataBaseTableServiceImpl(dataBaseTableMapper);
    }
}
