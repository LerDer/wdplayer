package com.wd.player.playSlider;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * @author lww
 * @date 2024-10-22 18:08
 */
public class MySliderUI extends BasicSliderUI {

	public MySliderUI(JSlider slider) {
		super(slider);
	}

	/**
	 * 绘制指示物
	 */
	@Override
	public void paintThumb(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fillOval(thumbRect.x, thumbRect.y + 4, 10, 10);
	}

	/**
	 * 绘制刻度轨迹
	 */
	@Override
	public void paintTrack(Graphics g) {
		int cy, cw;
		Rectangle trackBounds = trackRect;

		Graphics2D g2 = (Graphics2D) g;
		cy = (trackBounds.height / 2) - 2;
		cw = trackBounds.width;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(trackBounds.x, trackBounds.y + cy);
		g2.setPaint(Color.GRAY);
		g2.drawLine(0, 0, cw, 0);
		g2.drawLine(0, 1, 0, 2);

		int trackLeft = 0;
		int trackRight;
		trackRight = trackRect.width - 1;
		int middleOfThumb;
		int fillLeft;
		int fillRight;
		// 坐标换算
		middleOfThumb = thumbRect.x + (thumbRect.width / 2);
		middleOfThumb -= trackRect.x;
		if (!drawInverted()) {
			fillLeft = !slider.isEnabled() ? trackLeft : trackLeft + 1;
			fillRight = middleOfThumb;
		} else {
			fillLeft = middleOfThumb;
			fillRight = !slider.isEnabled() ? trackRight - 1 : trackRight - 2;
		}
		g2.setPaint(new GradientPaint(0, 0, Color.BLUE, cw, 0, Color.BLUE, true));
		g2.drawLine(0, 0, fillRight - fillLeft, 0);
		g2.drawLine(0, 1, fillRight - fillLeft, 1);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.translate(-trackBounds.x, -(trackBounds.y + cy));
	}

	@Override
	protected TrackListener createTrackListener(JSlider slider) {
		return new TrackListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				slider.setValueIsAdjusting(true);
				slider.setValue(slider.getMaximum() * (e.getX() - trackRect.x) / trackRect.width);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				slider.setValueIsAdjusting(true);
				slider.setValue(slider.getMaximum() * (e.getX() - trackRect.x) / trackRect.width);
			}
		};
	}

}
