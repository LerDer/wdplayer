package com.wd.player.observer.subscriber;

import com.wd.player.observer.message.MusicEvent;

/**
 * @author lww
 * @date 2024-10-12 18:17
 */
public class WaveSubscriber implements Subscriber {

	private static final WaveSubscriber INSTANCE = new WaveSubscriber();

	private WaveSubscriber() {
	}

	public static WaveSubscriber getInstance() {
	    return INSTANCE;
	}

	@Override
	public void update(MusicEvent musicEvent) {

	}
}
