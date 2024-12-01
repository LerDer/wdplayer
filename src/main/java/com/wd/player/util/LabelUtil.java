package com.wd.player.util;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JLabel;

/**
 * @author lww
 * @date 2024-11-14 01:07
 */
public class LabelUtil {

	private static int getLength(JLabel label) {
		FontMetrics metrics = label.getFontMetrics(label.getFont());
		int textW = metrics.stringWidth(label.getText());
		return textW;
	}

	private static int getLabelWidth(JLabel label) {
		Dimension size = label.getSize();
		return size.width;
	}

	public static boolean isScroll(JLabel label) {
		int length = getLength(label);
		int labelWidth = getLabelWidth(label);
		return length > labelWidth - 20;
	}

	private static Timer scrollText(JLabel label) {
		if (!isScroll(label)) {
			return null;
		}
		String text = label.getText();
		label.setText("  " + text + "  ");
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				String text = label.getText();
				String s = text.substring(1, text.length()) + text.charAt(0);
				label.setText(s);
				label.repaint();
			}
			//延时100毫秒后，每300毫秒执行一次
		}, 100, 300);
		return timer;
	}

}
