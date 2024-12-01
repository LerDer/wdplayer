package com.wd.player.observer.publisher;

import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.subscriber.Subscriber;
import java.util.ArrayList;
import java.util.List;

/**
 * 单例，确保 OBSERVERS 线程安全
 */
public class PlayerPublisher implements Publisher {

	private static final List<Subscriber> OBSERVERS = new ArrayList<>();

	private static final PlayerPublisher INSTANCE = new PlayerPublisher();

	private PlayerPublisher() {
	}

	public static PlayerPublisher getInstance() {
		return INSTANCE;
	}

	@Override
	public synchronized void addObserver(Subscriber observer) {
		OBSERVERS.add(observer);
	}

	@Override
	public synchronized void deleteObserver(Subscriber observer) {
		OBSERVERS.remove(observer);
	}

	@Override
	public synchronized void notifyObservers(MusicEvent musicEvent) {
		for (Subscriber observer : OBSERVERS) {
			try {
				observer.update(musicEvent);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
}
