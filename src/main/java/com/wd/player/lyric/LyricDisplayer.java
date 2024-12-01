package com.wd.player.lyric;

import com.wd.player.database.vo.LyricVO;
import com.wd.player.gui.MainContainer;
import com.wd.player.observer.thread.LyricThread;
import com.wd.player.util.SwingUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import lombok.Setter;

/**
 * 歌词展示
 *
 * @author lww
 * @date 2024-10-21 18:01
 */
public class LyricDisplayer extends JPanel {

	@Setter
	public LinkedList<LyricVO> lyricList;
	private Image image;

	@Setter
	private LyricThread thread;

	public LyricDisplayer(ImageIcon imageIcon) {
		setLayout(null);
		setBounds(0, 0, MainContainer.width, MainContainer.common_height);
		if (imageIcon != null) {
			this.image = imageIcon.getImage();
		} else {
			ImageIcon icon = new ImageIcon(getClass().getResource("/image/back.png"));
			this.image = icon.getImage();
		}

	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
		JButton sub10 = SwingUtil.getButton("-10", false, null);
		sub10.setBounds(3, 5, 23, 15);
		sub10.addActionListener(e -> {
			if (thread != null) {
				thread.addTime(-100);
			}
		});
		JButton plus10 = SwingUtil.getButton("+10", false, null);
		plus10.setBounds(30, 5, 24, 15);
		plus10.addActionListener(e -> {
			if (thread != null) {
				thread.addTime(100);
			}
		});
		this.add(sub10);
		this.add(plus10);
		if (lyricList != null) {
			if (lyricList.size() > 1) {
				for (int i = 0; i < lyricList.size(); i++) {
					LyricVO lyricVO = lyricList.get(i);
					if (lyricVO.isShow()) {
						g.setFont(new Font("楷体", Font.PLAIN, 20));
						int perHeight = g.getFontMetrics().getHeight() + 5;
						drawLineInMiddle(this.getHeight() - (MainContainer.MAX_SIZE - i) * perHeight, lyricVO.getLyric(), g, Color.BLUE);
					} else {
						g.setFont(new Font("楷体", Font.PLAIN, 15));
						int perHeight = g.getFontMetrics().getHeight() + 11;
						drawLineInMiddle(this.getHeight() - (MainContainer.MAX_SIZE - i) * perHeight, lyricVO.getLyric(), g, Color.BLACK);
					}
				}
			} else {
				LyricVO lyricVO = lyricList.get(0);
				g.setFont(new Font("楷体", Font.PLAIN, 20));
				int perHeight = g.getFontMetrics().getHeight() + 5;
				drawLineInMiddle(this.getHeight() - (MainContainer.MAX_SIZE - 1) * perHeight, lyricVO.getLyric(), g, Color.BLUE);
			}
		}
	}

	public void drawLineInMiddle(int height, String lyric, Graphics g, Color color) {
		FontMetrics fm = g.getFontMetrics();
		g.setColor(color);
		int x = (this.getWidth() - fm.stringWidth(lyric)) / 2;
		g.drawString(lyric, x, height);
	}

}
