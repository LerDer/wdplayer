package com.wd.player.wave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * 频谱显示
 *
 * @author Administrator
 */
public class Spectrum extends JComponent {

	public static final int FFT_N_LOG = 9;
	public static final int FFT_N = 1 << FFT_N_LOG;
	private static final long serialVersionUID = 1L;
	private static final int MAX_COLUMS = 128;
	private static final int Y0 = 1 << ((FFT_N_LOG + 3) << 1);
	//lg((8*FFT_N)^2)
	private static final double LOG_10 = Math.log10(Y0);
	private int band;
	private int width, height;
	private int[] xplot, lastPeak, lastY;
	private int deltax;
	private long lastTimeMillis;
	private BufferedImage spectrumImage, barImage;
	private Graphics spectrumGraphics;
	private boolean isAlive;

	public Spectrum(int width, int height) {
		isAlive = true;
		band = 64;
		this.width = width;
		this.height = height;
		lastTimeMillis = System.currentTimeMillis();
		xplot = new int[MAX_COLUMS + 1];
		lastPeak = new int[MAX_COLUMS];
		lastY = new int[MAX_COLUMS];
		spectrumImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		spectrumGraphics = spectrumImage.getGraphics();
		// 设置背景透明
		//setBackground(new Color(0, 0, 0, 0));
		setPreferredSize(new Dimension(width, height));
		setPlot();
		barImage = new BufferedImage(deltax - 1, height, BufferedImage.TYPE_3BYTE_BGR);

		setColor();
	}

	public void setColor() {
		Color crPeak = new Color(0x7f7f7f);
		spectrumGraphics.setColor(crPeak);

		spectrumGraphics.setColor(Color.gray);
		Graphics2D g = (Graphics2D) barImage.getGraphics();
		Color crTop = new Color(0xf6c244);
		Color crMid = new Color(0xcb332b);
		Color crBot = new Color(0x5b79f4);
		GradientPaint gp1 = new GradientPaint(0, 0, crTop, deltax - 1, height / 2, crMid);
		g.setPaint(gp1);
		g.fillRect(0, 0, deltax - 1, height / 2);
		GradientPaint gp2 = new GradientPaint(0, height / 2, crMid, deltax - 1, height, crBot);
		g.setPaint(gp2);
		g.fillRect(0, height / 2, deltax - 1, height);
		gp1 = gp2 = null;
		crPeak = crTop = crMid = crBot = null;
	}

	private void setPlot() {
		deltax = (width - band + 1) / band + 1;

		// 0-16kHz分划为band个频段，各频段宽度非线性划分。
		for (int i = 0; i <= band; i++) {
			xplot[i] = 0;
			xplot[i] = (int) (0.5 + Math.pow(FFT_N >> 1, (double) i / band));
			if (i > 0 && xplot[i] <= xplot[i - 1]) {
				xplot[i] = xplot[i - 1] + 1;
			}
		}
	}

	/**
	 * 绘制"频率-幅值"直方图并显示到屏幕。
	 *
	 * @param amp amp[0..FFT.FFT_N/2-1]为频谱"幅值"(用复数模的平方)。
	 */
	public void drawHistogram(double[] amp) {
		spectrumGraphics.clearRect(0, 0, width, height);

		long t = System.currentTimeMillis();
		//峰值下落速度
		int speed = (int) (t - lastTimeMillis) / 30;
		lastTimeMillis = t;

		int i = 0, x = 0, y, xi, peaki, w = deltax - 1;
		double maxAmp;
		for (; i != band; i++, x += deltax) {
			// 查找当前频段的最大"幅值"
			maxAmp = 0.00;
			xi = xplot[i];
			y = xplot[i + 1];
			for (; xi < y; xi++) {
				if (amp[xi] > maxAmp) {
					maxAmp = amp[xi];
				}
			}

			// maxAmp转换为用对数表示的"分贝数"y:
			y = (int) Math.sqrt(maxAmp);
			y /= FFT_N; //幅值
			y /= 8;    //调整
			if (y > 0) {
				y = (int) (Math.log10(y) * 20 * 2);
			}
			// 为了突出幅值y显示时强弱的"对比度"，计算时作了调整。未作等响度修正。
			y = (maxAmp > Y0) ? (int) ((Math.log10(maxAmp) - LOG_10) * 20) : 0;

			// 使幅值匀速度下落
			lastY[i] -= speed << 2;
			if (y < lastY[i]) {
				y = lastY[i];
				if (y < 0) {
					y = 0;
				}
			}
			lastY[i] = y;

			if (y >= lastPeak[i]) {
				lastPeak[i] = y;
			} else {
				// 使峰值匀速度下落
				peaki = lastPeak[i] - speed;
				if (peaki < 0) {
					peaki = 0;
				}
				lastPeak[i] = peaki;
				peaki = height - peaki;
				spectrumGraphics.drawLine(x, peaki, x + w - 1, peaki);
			}

			// 画当前频段的直方图
			y = height - y;
			spectrumGraphics.drawImage(barImage, x, y, x + w, height, 0, y, w, height, null);
		}

		// 刷新到屏幕
		repaint(0, 0, width, height);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(spectrumImage, 0, 0, null);
	}

	public void stop() {
		isAlive = false;
	}
}
