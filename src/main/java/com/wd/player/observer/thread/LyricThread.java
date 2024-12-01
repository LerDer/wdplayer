package com.wd.player.observer.thread;

import com.wd.player.database.service.SongInfoService;
import com.wd.player.database.vo.LyricVO;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.gui.MainContainer;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.status.StatusManager;
import com.wd.player.util.LyricUtil;
import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lww
 * @date 2024-10-30 18:20
 */
public class LyricThread extends BaseThread {

	@Setter
	private MusicEvent musicEvent;

	private AtomicLong currentTime;

	public LyricThread(MusicEvent musicEvent) {
		this.musicEvent = musicEvent;
	}

	public void addTime(int time) {
		if (this.currentTime!= null) {
			this.currentTime = new AtomicLong(this.currentTime.get() + time);
		}
	}

	public void play() {
		currentTime = new AtomicLong();
		Integer songId = musicEvent.getSongId();
		LinkedList<LyricVO> showList = new LinkedList<>();
		SongInfoVO infoVO = SongInfoService.getSongInfoVO(songId);
		String lyric = infoVO.getLyric();
		String filePath = infoVO.getFilePath();
		String lyricPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".lrc";
		LinkedList<LyricVO> lyricList = new LinkedList<>();
		if (StringUtils.isNotBlank(lyric)) {
			lyricList = LyricUtil.getLyric(lyric);
		} else {
			File file = new File(lyricPath);
			if (file.exists()) {
				lyricList = LyricUtil.getLyric(file);
			}
		}
		if (lyricList.isEmpty()) {
			lyricList = new LinkedList<>();
			lyricList.add(new LyricVO("00:00.00", musicEvent.getFileName()));
		}
		while (StatusManager.playing) {
			// 播放歌词
			if (lyricList.size() <= MainContainer.MAX_SIZE) {
				//小于最大展示的数量，直接显示
				showList.clear();
				showList.addAll(lyricList);
			} else {
				for (int i = 0; i < lyricList.size(); i++) {
					LyricVO lyricVO = lyricList.get(i);
					long time = lyricVO.getTime();
					if (i == 0 && showList.isEmpty()) {
						lyricVO.setShow(true);
						showList.add(lyricVO);
						showList.add(lyricList.get(i + 1));
					}
					if (time == currentTime.get()) {
						//匹配到就清空原来的
						showList.clear();
						if (i > 0) {
							lyricList.get(i - 1).setShow(false);
							showList.add(lyricList.get(i - 1));
						}
						//当前播放行
						lyricVO.setShow(true);
						showList.add(lyricVO);
						if (i < lyricList.size() - 1) {
							lyricList.get(i + 1).setShow(false);
							showList.add(lyricList.get(i + 1));
						}
						// 播放歌词
						//System.out.println(lyricVO.getLyric());
					}
				}
			}
			if (MainContainer.displayer != null) {
				//重绘
				MainContainer.displayer.removeAll();
				MainContainer.displayer.setLyricList(showList);
				MainContainer.displayer.setThread(this);
				MainContainer.displayer.repaint();
				MainContainer.displayer.updateUI();
			}
			currentTime.addAndGet(100);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
