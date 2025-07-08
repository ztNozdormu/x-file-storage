package org.dromara.x.file.storage.spring;

import org.dromara.x.file.storage.spring.mybatis.KndMybatisProperties;

/**
 * MybatisPlus工具类
 *
 * @author qc
 */
public class MybatisPlusUtils {
    /**
     * 获取动态表名
     *
     * @return
     */
    public static String getDynamicTableName() {
        return KndMybatisProperties.TABLE_NAME.get();
    }

    /**
     * 设置动态表名
     */
    public static void setDynamicTableName(String tableName) {
        KndMybatisProperties.TABLE_NAME.set(tableName);
    }

    /**
     * 清空当前线程设置的动态表名
     *
     * @return
     * @author wk
     * @date 2022/2/9 10:37
     */
    public static void emptyDaynamicTableName() {
        KndMybatisProperties.TABLE_NAME.remove();
    }

    /**
     * 获取基础表
     *
     * @return
     */
    public static String getBaseTableName(String dynamicTableName) {
        return BaseTableNameEnum.getBaseTableName(dynamicTableName);
    }
}
