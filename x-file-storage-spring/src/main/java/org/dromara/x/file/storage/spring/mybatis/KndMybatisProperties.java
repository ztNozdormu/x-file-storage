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
public class KndMybatisProperties {
    public static final ThreadLocal<String> TABLE_NAME = new ThreadLocal<>();

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        // 动态表名配置
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        Map<String, TableNameHandler> tableNameHandlerMap = new HashMap<>();
        // 设置需要动态替换的表名
        BaseTableNameEnum.getAllBaseTableName()
                .forEach(e -> tableNameHandlerMap.put(e, (sql, tableName) -> Optional.ofNullable(TABLE_NAME.get())
                        .orElse(tableName)));
        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(tableNameHandlerMap);
        // 添加拦截器
        mybatisPlusInterceptor.addInnerInterceptor(new DynamicCreateTableInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
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
