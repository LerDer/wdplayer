package com.wd.player.observer.enums;

import com.wd.player.observer.thread.BaseThread;
import com.wd.player.status.StatusManager;

/**
 * 消息
 */
public enum PlayMessageEnum implements PlayMessageHandler {
	Play,
	Pause {
		@Override
		public void handle(BaseThread thread) {
			StatusManager.paused = true;
			thread.suspend();
		}
	},
	Recover {
		@Override
		public void handle(BaseThread thread) {
			thread.resume();
			StatusManager.paused = false;
		}
	},
	Exit {
		@Override
		public void handle(BaseThread thread) {
			thread.stop();
		}
	},
	Stop {
		@Override
		public void handle(BaseThread thread) {
			if (thread != null) {
				thread.stop();
			}
		}
	},
	Finish {
		@Override
		public void handle(BaseThread thread) {

		}
	};

	@Override
	public void handle(BaseThread thread) {
		System.out.println("未知消息类型");
	}

}