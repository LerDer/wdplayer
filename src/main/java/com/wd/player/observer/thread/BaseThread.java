package com.wd.player.observer.thread;

public abstract class BaseThread extends Thread {

	public abstract void play();

	@Override
	public void run() {
		this.play();
	}

}
