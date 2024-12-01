package com.wd.player.observer.subscriber;

import com.wd.player.gui.MainContainer;
import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.thread.ScrollNameThread;
import java.util.Objects;

/**
 * @author lww
 * @date 2024-11-14 01:38
 */
public class ScrollNameSubscriber implements Subscriber {

	private static ScrollNameThread thread = null;

	private static final ScrollNameSubscriber INSTANCE = new ScrollNameSubscriber();

	private ScrollNameSubscriber() {
	}

	public static ScrollNameSubscriber getInstance() {
		return INSTANCE;
	}

	@Override
	public void update(MusicEvent musicEvent) throws Exception {
		PlayMessageEnum messageEnum = musicEvent.getPlayMessage();
		if (Objects.requireNonNull(messageEnum) == PlayMessageEnum.Play) {
			if (thread != null) {
				thread.stop();
				thread = null;
			}
			thread = new ScrollNameThread();
			thread.setMusicEvent(musicEvent);
			thread.setContainer(MainContainer.container);
			thread.setDaemon(true);
			thread.start();
		} else {
			messageEnum.handle(thread);
		}
	}
}
