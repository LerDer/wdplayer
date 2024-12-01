package com.wd.player.observer.enums;

import com.wd.player.observer.thread.BaseThread;

/**
 * @author lww
 * @date 2024-10-31 13:21
 */
public interface PlayMessageHandler {

	void handle(BaseThread thread);

}
