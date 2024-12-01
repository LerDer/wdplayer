package com.wd.player.about;

import com.wd.player.util.SwingUtil;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author lww
 * @date 2024-11-15 11:39
 */
public class AboutPanel extends JPanel {

	public AboutPanel() {
		setLayout(null);
		setBounds(0, 0, 500, 300);

		JLabel emailIcon = SwingUtil.getLabel("");
		emailIcon.setBounds(120, 30, 30, 30);
		ImageIcon iconEmail = new ImageIcon(getClass().getResource("/image/email.png"));
		emailIcon.setIcon(SwingUtil.zipCover(iconEmail, 30, 30));
		JTextField email = SwingUtil.getValue("lerder@foxmail.com");
		email.setBorder(null);
		email.setEditable(false);
		email.setHorizontalAlignment(JTextField.CENTER);
		email.setBounds(160, 30, 240, 30);

		JLabel gitIcon = SwingUtil.getLabel("");
		gitIcon.setBounds(120, 70, 30, 30);
		ImageIcon iconGit = new ImageIcon(getClass().getResource("/image/github.png"));
		gitIcon.setIcon(SwingUtil.zipCover(iconGit, 30, 30));
		JTextField git = SwingUtil.getValue("https://github.com/LerDer");
		git.setBorder(null);
		git.setEditable(false);
		git.setHorizontalAlignment(JTextField.CENTER);
		git.setBounds(160, 70, 240, 30);

		JLabel wechatIcon = SwingUtil.getLabel("");
		wechatIcon.setBounds(120, 110, 30, 30);
		ImageIcon iconWechat = new ImageIcon(getClass().getResource("/image/wechat.png"));
		wechatIcon.setIcon(SwingUtil.zipCover(iconWechat, 30, 30));
		JLabel wechat = SwingUtil.getLabel("");
		wechat.setBounds(200, 110, 150, 150);
		wechat.setHorizontalAlignment(SwingConstants.CENTER);
		wechat.setVerticalAlignment(SwingConstants.CENTER);
		ImageIcon wechat1 = new ImageIcon(getClass().getResource("/image/gzh.jpg"));
		wechat.setIcon(SwingUtil.zipCover(wechat1, 150, 150));

		this.add(emailIcon);
		this.add(email);
		this.add(gitIcon);
		this.add(git);
		this.add(wechatIcon);
		this.add(wechat);
	}
}
