package com.pet.platform.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 雪花算法，用于分布式环境中生成全局唯一ID：内部采用位运算
 * 生成的结果是一个64位的整数
 * 1bit：表示正负，1为负，0为正，生成的id都是正整数，所以高位1bit固定为0
 * 41bit：精确到毫秒级，41位的长度可以使用69年，还可以根据时间排序
 * 10bit：工作机器id，10位可以支持最多1024个节点
 * 12bit：序列号->一系列的自增id，表示的最大正整数是4095
 *
 * 最后的结果是long型
 */
public class SnowFlakeUtil {

	/**
	 * 使用静态内部类的单例模式来保证线程安全
	 */
	private static class SnowFlake{
		/**
		 * 内部类对象
		 */
		private static final SnowFlakeUtil.SnowFlake SNOW_FLAKE = new SnowFlakeUtil.SnowFlake();
		/**
		 * 起始的时间戳
		 */
		private final long START_TIMESTAMP = System.currentTimeMillis();
		/**
		 * 序列号占用位数
		 */
		private final long SEQUENCE_BIT = 12;
		/**
		 * 机器标识占用位数
		 */
		private final long MACHINE_BIT = 10;
		/**
		 * 时间戳位移位数
		 */
		private final long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;
		/**
		 * 最大序列号  （4095）
		 */
		private final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
		/**
		 * 最大机器编号 （1023）
		 */
		private final long MAX_MACHINE_ID = ~(-1L << MACHINE_BIT);
		/**
		 * 生成id机器标识部分
		 */
		private long machineIdPart;
		/**
		 * 序列号
		 */
		private long sequence = 0L;
		/**
		 * 上一次时间戳
		 */
		private long lastStamp = -1L;

		private SnowFlake(){
			//模拟这里获得本机机器编码
			long localIp = 1234;
			//localIp & MAX_MACHINE_ID最大不会超过1023,在左位移12位
			machineIdPart = (localIp & MAX_MACHINE_ID) << SEQUENCE_BIT;
		}

		/**
		 * 返回以毫秒为单位的当前时间
		 */
		protected long timeGen() {
			return System.currentTimeMillis();
		}

		/**
		 * 阻塞到下一个毫秒，直到获得新的时间戳
		 */
		private long getNextMill() {
			long mill = timeGen();
			while (mill <= lastStamp) {
				mill = timeGen();
			}
			return mill;
		}

		public synchronized long nextId(){
			long currentStamp = timeGen();
			while (currentStamp < lastStamp) {
				// //服务器时钟被调整了,ID生成器停止服务.
				throw new RuntimeException(String.format("时钟已经回拨.  Refusing to generate id for %d milliseconds", lastStamp - currentStamp));
			}
			if (currentStamp == lastStamp) {
				// 每次+1
				sequence = (sequence + 1) & MAX_SEQUENCE;
				// 毫秒内序列溢出
				if (sequence == 0) {
					// 阻塞到下一个毫秒,获得新的时间戳
					currentStamp = getNextMill();
				}
			} else {
				//不同毫秒内，序列号置0
				sequence = 0L;
			}
			lastStamp = currentStamp;
			//时间戳部分+机器标识部分+序列号部分
			return (currentStamp - START_TIMESTAMP) << TIMESTAMP_LEFT | machineIdPart | sequence;
		}
	}

	/**
	 * 获取long类型雪花ID
	 */
	public static long uniqueLong() {
		return SnowFlakeUtil.SnowFlake.SNOW_FLAKE.nextId();
	}

	/**
	 * 获取String类型雪花ID
	 */
	public static String uniqueLongHex() {
		return String.format("%016x", uniqueLong());
	}

	/**
	 * 测试
	 */
	public static void main(String[] args) throws InterruptedException {

		//计时开始时间
		long start = System.currentTimeMillis();
		//让100个线程同时进行
		final CountDownLatch latch = new CountDownLatch(100);
		//判断生成的20万条记录是否有重复记录
		final Map<Long, Integer> map = new ConcurrentHashMap();
		for (int i = 0; i < 100; i++) {
			//创建100个线程
			new Thread(() -> {
				for (int s = 0; s < 2000; s++) {
					long snowID = SnowFlakeUtil.uniqueLong();
					System.out.println("生成雪花ID="+snowID);
					Integer put = map.put(snowID, 1);
					if (put != null) {
						throw new RuntimeException("主键重复");
					}
				}
				latch.countDown();
			}).start();
		}
		//让上面100个线程执行结束后，在走下面输出信息
		latch.await();
		System.out.println("生成20万条雪花ID总用时="+ (System.currentTimeMillis() - start));
	}

}
