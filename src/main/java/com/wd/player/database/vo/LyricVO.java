package com.wd.player.database.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * @author lww
 * @date 2024-10-21 17:31
 */
@Data
public class LyricVO implements Serializable {

	private static final long serialVersionUID = 1L;

	//时间, 单位为10ms
	private long time;

	//歌词
	private String lyric;

	//是否展示
	private boolean show;

	/*
	 * 设置时间
	 * time: 被设置成的时间字符串, 格式为mm:ss.ms
	 */
	public void setTime(String time) {
		String[] str = time.split("[:.]");
		this.time = Integer.parseInt(str[0]) * 6000L + Integer.parseInt(str[1]) * 100L +
				Integer.parseInt(str[2]);
	}

	public LyricVO(String time, String lyric) {
		String[] str = time.split("[:.]");
		this.time = Integer.parseInt(str[0]) * 6000L + Integer.parseInt(str[1]) * 100L + Integer.parseInt(str[2]);
		this.lyric = lyric;
	}
}
