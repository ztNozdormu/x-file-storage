package org.dromara.x.file.storage.spring;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.tika.utils.StringUtils;

/**
 * 分表基础表名 枚举
 */
public enum BaseTableNameEnum {
    HOS_COLLECT_TASK("hos_collect_task", true),

    HOS_COLLECT_TASK_ACCNO("hos_collect_task_accno", true),

    HOS_COLLECT_TASK_FILE("hos_collect_task_file", true),

    HOS_COLLECT_TASK_INFO("hos_collect_task_info", true),

    PATIENT_REUPLOAD_TASK("patient_reupload_task", true),

    HOS_TASK_FILE_RECORD("hos_task_file_record", true),

    HOS_FILE_DETAIL("file_detail", true);

    /**
     * 基础表名
     */
    private final String tableName;
    /**
     * 是否动态创建
     */
    private final boolean dynamicCreate;

    BaseTableNameEnum(String tableName, boolean dynamicCreate) {
        this.tableName = tableName;
        this.dynamicCreate = dynamicCreate;
    }

    /**
     * 获取所有基础表名
     *
     * @return
     */
    public static Set<String> getAllBaseTableName() {
        return Arrays.stream(values()).map(e -> e.tableName).collect(Collectors.toSet());
    }

    /**
     * 根据动态表名获取基础表
     *
     * @param dynamicTableName
     * @return
     */
    public static String getBaseTableName(String dynamicTableName) {
        if (StringUtils.isBlank(dynamicTableName)) {
            return null;
        }
        Optional<String> baseTableNameOptional = Arrays.stream(values())
                .map(e -> e.tableName)
                .sorted((o1, o2) -> Integer.compare(o2.length(), o1.length()))
                .filter(dynamicTableName::startsWith)
                .findFirst();
        if (baseTableNameOptional.isPresent()) {
            if (Arrays.stream(values())
                    .filter(e -> Objects.equals(e.tableName, baseTableNameOptional.get()))
                    .anyMatch(e -> e.dynamicCreate)) {
                return baseTableNameOptional.get();
            }
        }
        return null;
    }

    public String tableName() {
        return tableName;
    }
}
