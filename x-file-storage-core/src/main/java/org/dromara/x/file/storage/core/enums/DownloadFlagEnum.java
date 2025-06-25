package org.dromara.x.file.storage.core.enums;


/**
 * 下载标记枚举
 *
 * @author qc
 */
public enum DownloadFlagEnum {
    /**
     * 下载
     */
    DOWNLOAD(1),
    /**
     * 不下载
     */
    NOT_DOWNLOAD(0);

    private Integer value;

    DownloadFlagEnum(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
