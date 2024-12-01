package com.wd.player.observer.publisher;

import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.subscriber.Subscriber;

/**
 * 抽象主题（抽象被观察者），抽象主题角色把所有观察者对象保存在一个集合里，
 * 每个主题都可以有任意数量的观察者，抽象主题提供两个方法，即增加和删除观察者对象。
 */
public interface Publisher {

	/**
	 * 增加观察者
	 */
	void addObserver(Subscriber observer);

	//void addObserver(String topic, Observer observer);

	/**
	 * 删除观察者
	 */
	void deleteObserver(Subscriber observer);

	/**
	 * 通知观察者，发布消息
	 */
	void notifyObservers(MusicEvent musicEvent);

}
