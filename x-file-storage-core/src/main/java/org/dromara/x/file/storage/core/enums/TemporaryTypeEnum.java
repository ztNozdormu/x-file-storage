package org.dromara.x.file.storage.core.enums;

/**
 * 影像业务文档
 *
 * @author zt
 */
public enum TemporaryTypeEnum {

    /**
     * 永久存储
     */
    PERPETUAL(0, "永久存储"),
    /**
     * 临时存储
     */
    TEMPORARY(1, "临时存储");

    private final Integer value;
    private final String name;

    TemporaryTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }
}
