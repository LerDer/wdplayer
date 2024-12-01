package com.wd.player.util;

import com.wd.player.database.vo.LyricVO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lww
 */
public class LyricUtil {

	public static final String REG = "\\[(\\d{2}):(\\d{2})\\.(\\d{2})]|\\[(\\d{2}):(\\d{2})]";
	public static final String START = "00:00.00";

	public static final String END = ".00";

	/**
	 * 获取歌词
	 *
	 * @param file 歌词文件
	 */
	public static LinkedList<LyricVO> getLyric(File file) {
		LinkedList<LyricVO> lyricList = new LinkedList<>();
		//File file = new File(path);
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file);
					InputStreamReader is = new InputStreamReader(fis, StandardCharsets.UTF_8);
					BufferedReader br = new BufferedReader(is)) {
				String lineLyric;
				while ((lineLyric = br.readLine()) != null) {
					LyricVO lyricVO = analyseLine(lineLyric);
					if (lyricVO != null) {
						lyricList.add(lyricVO);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			System.out.println(file.getAbsolutePath() + " 文件不存在");
		}
		return lyricList;
	}

	/**
	 * 获取歌词
	 *
	 * @param lyrics 歌词内容
	 */
	public static LinkedList<LyricVO> getLyric(String lyrics) {
		LinkedList<LyricVO> lyricList = new LinkedList<>();
		String[] split = lyrics.split("\n");
		for (String lineLyric : split) {
			LyricVO lyricVO = analyseLine(lineLyric);
			if (lyricVO != null) {
				lyricList.add(lyricVO);
			}
		}
		return lyricList;
	}

	/**
	 * 分析单行歌词，返回可能为null
	 *
	 * @param lineLyric 单行歌词
	 * @return
	 * @Nullable
	 */
	public static LyricVO analyseLine(String lineLyric) {
		// 歌曲信息
		if (lineLyric.contains("[al:")) {
			// 专辑信息
			return new LyricVO(START, "专辑: " + lineLyric.substring(lineLyric.lastIndexOf(":") + 1, lineLyric.length() - 1) + " ");
		} else if (lineLyric.contains("[ar:")) {
			// 歌手信息
			return new LyricVO(START, "歌手: " + lineLyric.substring(lineLyric.lastIndexOf(":") + 1, lineLyric.length() - 1) + " ");
		} else if (lineLyric.contains("[au:")) {
			return new LyricVO(START, "歌词-作曲: " + lineLyric.substring(lineLyric.lastIndexOf(":") + 1, lineLyric.length() - 1) + " ");
		} else if (lineLyric.contains("[wl:")) {
			// 歌词作家
			return new LyricVO(START, "歌词: " + lineLyric.substring(lineLyric.lastIndexOf(":") + 1, lineLyric.length() - 1) + " ");
		} else if (lineLyric.contains("[wm:")) {
			// 歌曲
			return new LyricVO(START, "歌曲作家: " + lineLyric.substring(lineLyric.lastIndexOf(":") + 1, lineLyric.length() - 1) + " ");
		} else if (lineLyric.contains("[by:")) {
			// 歌词制作
			return new LyricVO(START, "歌词制作: " + lineLyric.substring(lineLyric.lastIndexOf(":") + 1, lineLyric.length() - 1) + " ");
		} else {
			Pattern pattern = Pattern.compile(REG);
			Matcher matcher = pattern.matcher(lineLyric);
			while (matcher.find()) {
				//
				if (matcher.group(1) == null) {
					//[02:23]
					return new LyricVO(matcher.group(4)
							+ ":" + matcher.group(5) + END,
							lineLyric.substring(lineLyric.lastIndexOf("]") + 1));
				} else {
					//[02:23.60]
					return new LyricVO(matcher.group(1)
							+ ":" + matcher.group(2)
							+ END,
							lineLyric.substring(lineLyric.lastIndexOf("]") + 1));
				}
			}
			return null;
		}
	}
}
