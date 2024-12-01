package com.wd.player.status;

import com.wd.player.database.entity.PlayerInfo;

/**
 * @author lww
 * @date 2024-10-31 10:52
 */
public class StatusManager {

	public volatile static boolean playing;
	public volatile static boolean paused;
	public volatile static boolean liked;

	public volatile static int sortType = 0;
	public volatile static String sortColumn = "CREATE_TIME";

	public static PlayerInfo playerInfo;
}
