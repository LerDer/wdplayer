package com.wd.player.util;

import com.sun.glass.events.KeyEvent;
import com.wd.player.gui.MainContainer;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class JMenuBarUtil extends JMenuBar {

	public JMenuBarUtil() {
		//添加“文件”菜单
		add(createFileMenu());
		//添加“编辑”菜单
		//add(createEditMenu());
		setVisible(true);
	}

	//定义"文件"菜单
	private JMenu createFileMenu() {
		JMenu menu = new JMenu("文件");
		JMenuItem choseFile = createMenuItem("/assert/icon/file.png", "choseFile", "打开文件", 18, 20);
		choseFile.addActionListener(e -> {
			String actionCommand = e.getActionCommand();
			System.out.println("actionCommand = " + actionCommand);
			MainContainer.openFile();
		});
		menu.add(choseFile);
		JMenuItem importFolder = createMenuItem("/assert/icon/import.png", "importFolder", "导入文件", 18, 20);
		importFolder.addActionListener(e -> {
			String actionCommand = e.getActionCommand();
			System.out.println("actionCommand = " + actionCommand);
			MainContainer.importFile();
		});
		menu.add(importFolder);
		return menu;
	}

	/**
	 * 带压缩图标的
	 */
	protected JMenuItem createMenuItem(String filePath, String action, String text, int width, int height) {
		JMenuItem item = new JMenuItem(text);
		//ActionCommand为命令码，用于区别各个选项，后用于监听事件
		item.setActionCommand(action);
		//设置监听事件
		//item.addActionListener(e -> {
		//	String actionCommand = e.getActionCommand();
		//	System.out.println("actionCommand = " + actionCommand);
		//});
		if (filePath != null) {
			//获取资源文件
			URL url = getClass().getResource(filePath);
			//设置图标
			ImageIcon newLogo = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
			item.setIcon(newLogo);
		}
		return item;
	}

	/**
	 * 创建菜单选项
	 */
	protected JMenuItem createMenuItem(String filePath, String action, String text) {
		JMenuItem item = new JMenuItem(text);
		//ActionCommand为命令码，用于区别各个选项，后用于监听事件
		item.setActionCommand(action);
		//设置监听事件
		item.addActionListener(e -> {
			String actionCommand = e.getActionCommand();
			System.out.println("actionCommand = " + actionCommand);
		});
		if (filePath != null) {
			//获取资源文件
			URL url = getClass().getResource(filePath);
			//设置图标
			item.setIcon(new ImageIcon(url));
		}
		return item;
	}

	//定义"编辑"菜单
	private JMenu createEditMenu() {
		JMenu menu = new JMenu("编辑(E)");
		menu.setMnemonic(KeyEvent.VK_E);
		JMenuItem item = new JMenuItem("撤销(U)", KeyEvent.VK_U);
		item.setEnabled(false);
		menu.add(item);
		menu.addSeparator();
		item = new JMenuItem("剪贴(T)", KeyEvent.VK_T);
		menu.add(item);
		item = new JMenuItem("复制(C)", KeyEvent.VK_C);
		menu.add(item);
		menu.addSeparator();
		JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("自动换行");
		menu.add(cbMenuItem);
		return menu;
	}

}