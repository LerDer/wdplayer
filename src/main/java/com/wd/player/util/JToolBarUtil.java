package com.wd.player.util;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * @author lww
 * @date 2024-07-30 4:39 PM
 */
public class JToolBarUtil extends JToolBar {

	public JToolBarUtil() {
		add(createToolButton("icon_new.png", "New", "新建"));
		setVisible(true);
	}

	protected JButton createToolButton(String filePath, String action, String toolText, int width, int height) {
		//获取资源文件
		URL url = getClass().getResource(filePath);
		JButton button = new JButton();
		button.setActionCommand(action);
		button.setToolTipText(toolText);
		button.setFocusable(false);
		//设置图标
		ImageIcon newLogo = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
		button.setIcon(newLogo);
		//设置监听事件
		button.addActionListener(e -> {
			String actionCommand = e.getActionCommand();
			System.out.println("actionCommand = " + actionCommand);
		});
		return button;
	}

	/**
	 * 创建工具栏
	 */
	protected JButton createToolButton(String filePath, String action, String toolText) {
		//获取资源文件
		URL url = getClass().getResource(filePath);
		JButton button = new JButton();
		button.setActionCommand(action);
		button.setToolTipText(toolText);
		button.setFocusable(false);
		//设置图标
		button.setIcon(new ImageIcon(url));
		//设置监听事件
		button.addActionListener(e -> {
			String actionCommand = e.getActionCommand();
			System.out.println("actionCommand = " + actionCommand);
		});
		return button;
	}

}
