package com.wd.player.observer.thread;

import com.wd.player.database.service.SongInfoService;
import com.wd.player.database.service.SongPlaylistService;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.gui.MainContainer;
import com.wd.player.meta.SongMeta;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.status.StatusManager;
import com.wd.player.util.SwingUtil;
import javax.swing.ImageIcon;
import lombok.Setter;

/**
 * @author lww
 * @date 2024-11-06 17:27
 */
public class MainThread extends BaseThread {

	@Setter
	private MusicEvent musicEvent;

	@Setter
	private MainContainer container;

	public MainThread(MusicEvent musicEvent) {
		this.musicEvent = musicEvent;
	}

	@Override
	public void play() {
		StatusManager.paused = false;
		Integer songId = musicEvent.getSongId();
		MainContainer.CURRENT_SONGID = songId;

		SongInfoVO infoVO = SongInfoService.getSongInfoVO(songId);
		//StringBuilder sb = new StringBuilder();
		//if (StringUtils.isNotEmpty(infoVO.getFileName())) {
		//	sb.append(infoVO.getFileName()).append(" - ");
		//} else {
		//	sb.append(infoVO.getTitle()).append(" - ");
		//	sb.append(infoVO.getArtist()).append(" - ");
		//	sb.append(".").append(infoVO.getEncodingType()).append(" - ");
		//}
		//sb.append(infoVO.getSampleRate()).append(" - ");
		//sb.append(infoVO.getBitRate());
		//container.getSongName().setText("  " + sb + "  ");
		container.getPlayButton().setIcon(new ImageIcon(getClass().getResource("/image/pause.png")));
		container.getStartTime().setText("0:00");
		container.getEndTime().setText(infoVO.getTrackLengthAsString());
		container.getImageLabel().setIcon(SwingUtil.zipCover(infoVO.getCover(), 300, 300));
		container.getPlaySlider().setMinimum(0);
		container.getPlaySlider().setMaximum(infoVO.getTrackLength());
		StatusManager.liked = SongPlaylistService.isLikeSong(songId);
		if (StatusManager.liked) {
			container.getLikeButton().setIcon(new ImageIcon(getClass().getResource("/image/liked.png")));
		} else {
			container.getLikeButton().setIcon(new ImageIcon(getClass().getResource("/image/unlike.png")));
		}

		//显示状态
		if (!MainContainer.showSongMeta) {
			SongMeta.reShow(infoVO);
		}
	}
}
