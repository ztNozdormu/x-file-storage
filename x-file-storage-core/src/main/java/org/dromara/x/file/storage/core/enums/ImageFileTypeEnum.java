package org.dromara.x.file.storage.core.enums;

/**
 * 影像业务文档分类枚举
 *
 * @author zt
 */
public enum ImageFileTypeEnum {

    /**
     * 胶片
     */
    FILM(1, "胶片"),
    /**
     * 报告
     */
    REPORT(2, "报告"),
    /**
     * dicom
     */
    DICOM(3, "影像");

    private final Integer value;
    private final String name;

    ImageFileTypeEnum(int value, String name) {
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
