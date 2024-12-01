package com.wd.player.observer.subscriber;

import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.thread.PlayMusicThread;
import com.wd.player.status.StatusManager;
import java.util.Objects;

/**
 * 音乐播放器订阅者
 *
 * @author lww
 * @date 2024-10-12 18:11
 */
public class MusicPlaySubscriber implements Subscriber {

	private static PlayMusicThread thread = null;

	private static final MusicPlaySubscriber INSTANCE = new MusicPlaySubscriber();

	private MusicPlaySubscriber() {
	}

	public static MusicPlaySubscriber getInstance() {
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
			thread = new PlayMusicThread(musicEvent);
			thread.setDaemon(true);
			thread.start();
		} else {
			messageEnum.handle(thread);
		}
	}

}
