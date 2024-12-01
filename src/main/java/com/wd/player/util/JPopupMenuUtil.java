package com.wd.player.util;

import javax.swing.JMenu;

/**
 * @author lww
 * @date 2024-07-30 4:47 PM
 */
public class JPopupMenuUtil extends JMenuBarUtil {

	public JPopupMenuUtil() {
		//添加“文件”菜单
		add(createPopMenuList());
		add(createMenuItem("/assert/icon/file.png", "choseFile", "选择文件", 18, 20));
		setVisible(true);
	}

	//定义"文件"菜单
	private JMenu createPopMenuList() {
		JMenu menu = new JMenu("文件");
		menu.add(createMenuItem("/assert/icon/file.png", "choseFile", "选择文件", 18, 20));
		menu.add(createMenuItem("/assert/icon/import.png", "importFolder", "导入文件夹", 18, 20));
		return menu;
	}

}
