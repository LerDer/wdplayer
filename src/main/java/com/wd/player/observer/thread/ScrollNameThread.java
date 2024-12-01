package com.wd.player.observer.thread;

import com.wd.player.database.service.SongInfoService;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.gui.MainContainer;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.status.StatusManager;
import com.wd.player.util.LabelUtil;
import javax.swing.JLabel;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lww
 * @date 2024-11-14 01:30
 */
public class ScrollNameThread extends BaseThread {

	@Setter
	private MainContainer container;

	@Setter
	private MusicEvent musicEvent;

	@Override
	public void play() {
		Integer songId = musicEvent.getSongId();
		SongInfoVO infoVO = SongInfoService.getSongInfoVO(songId);
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(infoVO.getFileName())) {
			sb.append(infoVO.getFileName()).append(" - ");
		} else {
			sb.append(infoVO.getTitle()).append(" - ");
			sb.append(infoVO.getArtist()).append(" - ");
			sb.append(".").append(infoVO.getEncodingType()).append(" - ");
		}
		sb.append(infoVO.getSampleRate()).append(" - ");
		sb.append(infoVO.getBitRate());
		container.getSongName().setText("  " + sb + "  ");
		JLabel songName = container.getSongName();
		boolean scroll = LabelUtil.isScroll(songName);
		if (scroll) {
			while (StatusManager.playing){
				String text = songName.getText();
				String s = text.substring(1, text.length()) + text.charAt(0);
				songName.setText(s);
				songName.repaint();
				try {
					Thread.sleep(300L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
