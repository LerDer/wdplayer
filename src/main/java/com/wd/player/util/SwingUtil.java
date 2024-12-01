package com.wd.player.util;

import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author lww
 * @date 2024-11-15 11:45
 */
public class SwingUtil {

	public static JLabel getLabel(String s) {
		return new JLabel(s);
	}

	public static JTextArea getTextArea(String s) {
		return new JTextArea(s);
	}

	public static JTextField getValue(String s) {
		return new JTextField(s);
	}

	public static JButton getButton(String s, boolean transparent, ImageIcon icon) {
		JButton button = new JButton(s);
		if (transparent) {
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
		}
		if (icon != null) {
			button.setIcon(icon);
		}
		Font font = new Font("楷体", Font.PLAIN, 10);
		button.setFont(font);
		return button;
	}

	/**
	 * 压缩图片
	 */
	public static ImageIcon zipCover(ImageIcon cover, int width, int height) {
		if (cover == null) {
			return null;
		}
		cover.setImage(cover.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
		return cover;
	}
}
