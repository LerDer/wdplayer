package com.wd.player.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * @author lww
 * @date 2024-10-18 17:03
 */
public class DeleteSongDialog extends JDialog implements ActionListener {

	String title;
	String content;
	String ok = "确定";
	String cancel = "取消";
	Boolean Confirm;

	public DeleteSongDialog(String title, String content, ImageIcon icon) {
		this.title = title;
		this.content = content;
		int width = 45, height = 45;
		// 创建1个图标实例,注意image目录要与src同级
		if (icon != null) {
			icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
			// 1个图片标签,显示图片
			JLabel jlImg = new JLabel(icon);
			jlImg.setSize(width, height);
			jlImg.setBounds(20, 44, width, height);
			add(jlImg);
			// 窗口左上角的小图标
			setIconImage(icon.getImage());
		}
		// 1个文字标签,显示文本
		JLabel jLabel = new JLabel(content);
		jLabel.setFont(new Font("", Font.PLAIN, 14));
		// 设置文字的颜色为蓝色
		jLabel.setForeground(Color.black);
		jLabel.setBounds(75, 43, 180, 45);
		JButton okBut = new JButton(ok);
		JButton cancelBut = new JButton(cancel);
		okBut.setBackground(Color.YELLOW);
		okBut.setBorderPainted(true);
		okBut.setBounds(65, 126, 98, 31);
		cancelBut.setBounds(175, 126, 98, 31);
		cancelBut.setBackground(Color.YELLOW);
		cancelBut.setBorderPainted(true);
		// 给按钮添加响应事件
		okBut.addActionListener(this);
		cancelBut.addActionListener(this);
		// 向对话框中加入各组件
		add(jLabel);
		add(okBut);
		add(cancelBut);
		// 对话框流式布局
		setLayout(null);
		// 设置标题
		setTitle(title);
		// 设置为模态窗口,此时不能操作父窗口
		setModal(true);
		// 设置对话框大小
		setSize(300, 210);
		// 对话框局域屏幕中央
		setLocationRelativeTo(null);
		// 对话框不可缩放
		setResizable(false);
		// 点击对话框关闭按钮时,销毁对话框
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 判断是不是确定按钮被点击
		if (ok.equals(e.getActionCommand())) {
			Confirm = true;
			// 对话框不可见
			this.setVisible(false);
			//System.out.println("我退出程序了...");
			//System.exit(0);
		}
		if (cancel.equals(e.getActionCommand())) {
			Confirm = false;
			this.setVisible(false);
			this.dispose();
		}
	}

	public Boolean getConfirm() {
		return Confirm;
	}
}
