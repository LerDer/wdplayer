package com.wd.player.observer.subscriber;

import com.wd.player.observer.message.MusicEvent;

/**
 * 抽象观察者，是观察者者的抽象类，它定义了一个更新方法，
 * 使得在得到主题更改通知时更新自己
 */
public interface Subscriber {

	/**
	 * 更新方法
	 */
	void update(MusicEvent musicEvent) throws Exception;
}
