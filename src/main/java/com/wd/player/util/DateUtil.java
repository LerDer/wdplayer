package com.wd.player.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lww
 * @date 2024-10-21 02:15
 */
public class DateUtil {

	private DateUtil() {
	}

	public static String date2String(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String currentMillis() {
		return System.currentTimeMillis() + "";
	}

}
