package com.wd.player.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 非阻塞线程池
 *
 * @author lww
 * @date 2019-05-27 11:53 AM
 */
public class ThreadPoolUtil {

	/*
	 * execute()方法只能接收实现Runnable接口类型的任务，
	 * submit()方法则既可以接收Runnable类型的任务，也可以接收Callable类型的任务。
	 * Runnable和Callable的区别是 前者没有返回值，后者可以有返回值
	 * */

	private static ThreadPoolExecutor pool = null;

	private static void init() {
		pool = new ThreadPoolExecutor(8, 12, 30,
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(1000),
				new CustomThreadFactory(),
				new CustomRejectedExecutionHandler());
	}

	public static void destory() {
		if (pool != null) {
			pool.shutdownNow();
		}
	}

	public static ThreadPoolExecutor getPool() {
		if (pool == null) {
			init();
		}
		return pool;
	}

	private static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		}
	}

	private static class CustomThreadFactory implements ThreadFactory {

		private AtomicInteger count = new AtomicInteger(0);

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			String threadName = ThreadPoolUtil.class.getSimpleName() + count.addAndGet(1);
			System.out.println(threadName);
			t.setName(threadName);
			return t;
		}
	}

}
