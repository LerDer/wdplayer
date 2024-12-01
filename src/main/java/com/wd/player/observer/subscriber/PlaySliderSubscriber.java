package com.wd.player.observer.subscriber;

import com.wd.player.gui.MainContainer;
import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.thread.PlaySliderThread;
import com.wd.player.status.StatusManager;
import java.util.Objects;

/**
 * @author lww
 * @date 2024-10-12 18:18
 */
public class PlaySliderSubscriber implements Subscriber {

	private static PlaySliderThread thread = null;

	private static final PlaySliderSubscriber INSTANCE = new PlaySliderSubscriber();

	private PlaySliderSubscriber() {
	}

	public static PlaySliderSubscriber getInstance() {
		return INSTANCE;
	}

	@Override
	public void update(MusicEvent musicEvent) {
		PlayMessageEnum messageEnum = musicEvent.getPlayMessage();
		if (Objects.requireNonNull(messageEnum) == PlayMessageEnum.Play) {
			StatusManager.playing = true;
			if (thread != null) {
				thread.stop();
				thread = null;
			}
			MainContainer.current = 0;
			thread = new PlaySliderThread();
			thread.setDaemon(true);
			thread.start();
		} else {
			messageEnum.handle(thread);
		}

	}
}
