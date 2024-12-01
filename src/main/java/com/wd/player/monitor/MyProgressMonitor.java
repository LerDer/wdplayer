package com.wd.player.monitor;

import java.io.File;
import java.util.Arrays;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;

/**
 * @author lww
 * @date 2024-10-18 00:20
 */
public class MyProgressMonitor {

	Timer timer;

	public void init(File[] files) {
		final SongLoadTask loadTask = new SongLoadTask(Arrays.asList(files));
		// 以启动一条线程的方式来执行一个耗时的任务
		//loadTask.start();
		final Thread loadThread = new Thread(loadTask);

		loadThread.start();
		// 创建进度对话框
		ProgressMonitor dialog = new ProgressMonitor(null,
				"导入中，请不要关闭窗口...",
				"已完成：0.00%",
				0,
				loadTask.getAmount());
		// 创建一个计时器
		timer = new Timer(300, e -> {
			// 以任务的当前完成量设置进度对话框的完成比例
			dialog.setProgress(loadTask.getCurrent());
			dialog.setNote("已完成：" + loadTask.getPercent());
			// 如果用户单击了进度对话框的” 取消 “按钮
			if (dialog.isCanceled()) {
				// 停止计时器
				timer.stop();
				// 中断任务的执行线程
				// 系统退出
				//System.exit(0);
			}
			if (loadTask.isFinished()) {
				//timer.stop();
			}
		});
		timer.start();
	}

}
