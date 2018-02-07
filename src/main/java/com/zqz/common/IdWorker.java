package com.zqz.common;

public class IdWorker{

    private static long workerId = 1;
    private static long datacenterId =1;
    private static long sequence =1;
    
    private static long twepoch = 1288834974657L;

    private static long workerIdBits = 5L;
    private static long datacenterIdBits = 5L;
    private static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private static long sequenceBits = 12L;

    private static long workerIdShift = sequenceBits;
    private static long datacenterIdShift = sequenceBits + workerIdBits;
    private static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private static long sequenceMask = -1L ^ (-1L << sequenceBits);

    private static long lastTimestamp = -1L;
    
    static{
    	init(1,1,1);
    }

    private static void init(long workerId, long datacenterId, long sequence){
        // sanity check for workerId
    	System.out.println(maxWorkerId);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0",maxDatacenterId));
        }
        System.out.printf("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);

        IdWorker.workerId = workerId;
        IdWorker.datacenterId = datacenterId;
        IdWorker.sequence = sequence;
    }

   

    public long getWorkerId(){
        return workerId;
    }

    public long getDatacenterId(){
        return datacenterId;
    }

    public long getTimestamp(){
        return System.currentTimeMillis();
    }

    public static synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long timeGen(){
        return System.currentTimeMillis();
    }

    //---------------测试---------------
    public static void main(String[] args) {
        for (int i = 0; i < 30; i++) {
            System.out.println(IdWorker.nextId());
        }
    }

}
