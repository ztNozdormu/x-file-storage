package org.dromara.x.file.storage.core.util;

import java.util.UUID;

/**
 * @author lichunguang
 */
public class UUIDUtils {

    // 起始的时间戳（自定义，例如系统上线时间）
    private final long twepoch = 1288834974657L;

    // 机器id所占的位数
    private final long workerIdBits = 5L;

    // 数据标识id所占的位数
    private final long datacenterIdBits = 5L;

    // 最大机器ID
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // 最大数据标识ID
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    // 序列在id中占的位数
    private final long sequenceBits = 12L;

    // 机器ID左移12位
    private final long workerIdShift = sequenceBits;

    // 数据标识id左移17位(12+5)
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    // 时间截左移22位(5+5+12)
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 序列的掩码，这里为4095 (0b111111111111=4095)
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 上次生成ID的时间截
    private long lastTimestamp = -1L;

    // 序列号
    private long sequence = 0L;

    // 工作机器ID
    private final long workerId;

    // 数据中心ID
    private final long datacenterId;

    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public UUIDUtils() {
        this.workerId = 1;
        this.datacenterId = 1;
    }

    public UUIDUtils(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // 生成ID
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退，抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果时间戳相同，则序列号自增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，序列号重置为0
            sequence = 0L;
        }

        // 更新最后的时间戳
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    // 获取当前时间戳
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    // 等待下一个毫秒
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        UUIDUtils idWorker = new UUIDUtils();
        for (int i = 0; i < 5; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
