package com.wd.player.wave;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JFrame;

/**
 * 无边框窗口
 */
public class NoBorderFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	Point pressedPoint;

	public NoBorderFrame() {
		// 取消窗体修饰效果
		this.setUndecorated(true);
		// 设置窗体背景颜色
		this.setBackground(new Color(0, 0, 0, 0));
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//鼠标按下事件
				//记录鼠标坐标
				pressedPoint = e.getPoint();
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				// 鼠标拖拽事件
				// 获取当前坐标
				Point point = e.getPoint();
				Point locationPoint = getLocation();
				// 获取窗体坐标
				int x = locationPoint.x + point.x - pressedPoint.x;
				// 计算移动后的新坐标
				int y = locationPoint.y + point.y - pressedPoint.y;
				// 改变窗体位置
				setLocation(x, y);
			}
		});
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}