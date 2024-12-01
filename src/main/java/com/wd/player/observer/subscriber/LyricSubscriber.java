package com.wd.player.observer.subscriber;

import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.thread.LyricThread;
import com.wd.player.status.StatusManager;
import java.util.Objects;

/**
 * @author lww
 * @date 2024-10-12 18:16
 */
public class LyricSubscriber implements Subscriber {

	private static LyricThread thread = null;

	private static final LyricSubscriber INSTANCE = new LyricSubscriber();

	private LyricSubscriber() {
	}

	public static LyricSubscriber getInstance() {
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
			thread = new LyricThread(musicEvent);
			thread.setDaemon(true);
			thread.start();
		} else {
			messageEnum.handle(thread);
		}
	}

}
