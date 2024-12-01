package com.wd.player.observer.thread;

import com.wd.player.gui.MainContainer;
import com.wd.player.status.StatusManager;
import com.wd.player.util.SongUtil;

/**
 * @author lww
 * @date 2024-10-31 11:00
 */
public class PlaySliderThread extends BaseThread {

	@Override
	public void play() {
		while (StatusManager.playing) {
			// 播放进度条
			Integer i = MainContainer.current++;
			MainContainer.container.getPlaySlider().setValue(i);
			MainContainer.container.getStartTime().setText(SongUtil.gettrackLengthString(i));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
