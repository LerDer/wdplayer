package com.wd.player.meta;

import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.gui.MainContainer;
import com.wd.player.util.SongUtil;
import com.wd.player.util.SwingUtil;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lww
 * @date 2024-11-11 17:29
 */
public class SongMeta extends JPanel {

	static JLabel label;
	static JTextArea lyricArea;
	static JTextField titleValue, artistValue, albumValue, yearValue,
			sampleRateValue, bitRateValue, trackLengthValue,
			trackValue, channelsValue, formatValue;
	static String imagePath;

	public SongMeta(SongInfoVO songInfoVO) {
		// 设置该窗体的标题
		//setTitle("元信息");
		setLayout(null);
		setBounds(0, 0, 500, 300);
		//Container c = getContentPane();
		//ImageIcon icon = new ImageIcon(getClass().getResource("/image/tu.png"));
		//icon.setImage(icon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
		ImageIcon cover = songInfoVO.getCover();
		if (cover != null) {
			cover.setImage(cover.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
		}
		label = SwingUtil.getLabel("");
		label.setToolTipText("点击更改封面");
		label.setText("");
		label.setIcon(cover);
		label.setBounds(30, 10, 100, 100);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 弹出文件选择对话框
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setCurrentDirectory(new File(""));
				fileChooser.setFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return SongUtil.isPic(f);
					}

					@Override
					public String getDescription() {
						return "图片";
					}
				});
				fileChooser.setCurrentDirectory(null);
				fileChooser.showOpenDialog(null);
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile == null) {
					return;
				}
				System.out.println("MainContainer_MainContainer_selectedFile:{}" + selectedFile.getAbsolutePath());
				imagePath = selectedFile.getAbsolutePath();
				label.setIcon(SwingUtil.zipCover(new ImageIcon(selectedFile.getAbsolutePath()), 100, 100));
				MainContainer.container.getImageLabel().setIcon(SwingUtil.zipCover(new ImageIcon(selectedFile.getAbsolutePath()), 300, 300));
			}
		});

		lyricArea = SwingUtil.getTextArea("");
		lyricArea.setLineWrap(true);
		lyricArea.setText(songInfoVO.getLyric());
		//创建滚动条面板
		JScrollPane scrollpane = new JScrollPane();
		//自定义该面板位置并设置大小为100*50
		//scrollpane.setSize(500, 200);
		//（这是关键！不是用add）把text1组件放到滚动面板里
		scrollpane.setViewportView(lyricArea);
		scrollpane.setBounds(10, 115, 130, 150);

		JLabel title = SwingUtil.getLabel("标  题:");
		title.setBounds(160, 30, 45, 30);
		titleValue = SwingUtil.getValue(songInfoVO.getTitle());
		titleValue.setBounds(200, 30, 120, 30);

		JLabel artist = SwingUtil.getLabel("作  者:");
		artist.setBounds(350, 30, 50, 30);
		artistValue = SwingUtil.getValue(songInfoVO.getArtist());
		artistValue.setBounds(390, 30, 100, 30);

		JLabel album = SwingUtil.getLabel("专 辑:");
		album.setBounds(160, 70, 35, 30);
		albumValue = SwingUtil.getValue(songInfoVO.getAlbum());
		albumValue.setBounds(200, 70, 120, 30);

		JLabel year = SwingUtil.getLabel("年 份:");
		year.setBounds(350, 70, 35, 30);
		yearValue = SwingUtil.getValue(songInfoVO.getYear());
		yearValue.setBounds(390, 70, 100, 30);

		JLabel sampleRate = SwingUtil.getLabel("采样率:");
		sampleRate.setBounds(160, 110, 50, 30);
		sampleRateValue = SwingUtil.getValue(songInfoVO.getSampleRate());
		sampleRateValue.setEditable(false);
		sampleRateValue.setBounds(200, 110, 120, 30);

		JLabel bitRate = SwingUtil.getLabel("比特率:");
		bitRate.setBounds(350, 110, 50, 30);
		bitRateValue = SwingUtil.getValue(songInfoVO.getBitRate());
		bitRateValue.setEditable(false);
		bitRateValue.setBounds(390, 110, 100, 30);

		JLabel trackLength = SwingUtil.getLabel("时  长:");
		trackLength.setBounds(160, 150, 50, 30);
		trackLengthValue = SwingUtil.getValue(songInfoVO.getTrackLengthAsString());
		trackLengthValue.setEditable(false);
		trackLengthValue.setBounds(200, 150, 120, 30);

		JLabel track = SwingUtil.getLabel("音  轨:");
		track.setBounds(350, 150, 50, 30);
		trackValue = SwingUtil.getValue(songInfoVO.getTrack());
		trackValue.setEditable(false);
		trackValue.setBounds(390, 150, 100, 30);

		JLabel channels = SwingUtil.getLabel("声 道:");
		channels.setBounds(160, 190, 50, 30);
		channelsValue = SwingUtil.getValue(songInfoVO.getChannels());
		channelsValue.setEditable(false);
		channelsValue.setBounds(200, 190, 120, 30);

		JLabel format = SwingUtil.getLabel("格 式:");
		format.setBounds(350, 190, 50, 30);
		formatValue = SwingUtil.getValue(songInfoVO.getFormat());
		formatValue.setEditable(false);
		formatValue.setBounds(390, 190, 100, 30);

		JButton button = new JButton();
		button.setBounds(390, 230, 60, 30);
		button.setText("保 存");
		button.addActionListener(e -> {
			String filePath = songInfoVO.getFilePath();
			File file = new File(filePath);
			try {
				byte[] bytes = null;
				if (StringUtils.isNotEmpty(imagePath)) {
					ImageIcon imageIcon = new ImageIcon(imagePath);
					Image image = imageIcon.getImage();
					String imageType = SongUtil.getImageType(imagePath);
					bytes = SongUtil.imageToByteArray(image, imageType);
				}
				System.out.println("filePath = " + filePath);
				SongUtil.setTag(file, lyricArea.getText(), titleValue.getText(), artistValue.getText(), albumValue.getText(), yearValue.getText(), bytes);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		this.add(label);
		this.add(scrollpane);
		this.add(title);
		this.add(titleValue);
		this.add(artist);
		this.add(artistValue);
		this.add(album);
		this.add(albumValue);
		this.add(year);
		this.add(yearValue);
		this.add(sampleRate);
		this.add(sampleRateValue);
		this.add(bitRate);
		this.add(bitRateValue);
		this.add(trackLength);
		this.add(trackLengthValue);
		this.add(track);
		this.add(trackValue);
		this.add(channels);
		this.add(channelsValue);
		this.add(format);
		this.add(formatValue);
		this.add(button);

		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500, 300);
		//setResizable(false);
		//setLocationRelativeTo(null);
		//setVisible(true);
	}

	public static void clean() {
		if (label != null) {
			label.setIcon(null);
		}
		if (lyricArea != null) {
			lyricArea.setText("");
		}
		if (titleValue != null) {
			titleValue.setText("");
		}
		if (artistValue != null) {
			artistValue.setText("");
		}
		if (albumValue != null) {
			albumValue.setText("");
		}
		if (yearValue != null) {
			yearValue.setText("");
		}
		if (sampleRateValue != null) {
			sampleRateValue.setText("");
		}
		if (bitRateValue != null) {
			bitRateValue.setText("");
		}
		if (trackLengthValue != null) {
			trackLengthValue.setText("");
		}
		if (trackValue != null) {
			trackValue.setText("");
		}
		if (channelsValue != null) {
			channelsValue.setText("");
		}
		if (formatValue != null) {
			formatValue.setText("");
		}
	}

	public static void reShow(SongInfoVO songInfoVO) {
		ImageIcon cover = songInfoVO.getCover();
		if (cover != null) {
			cover.setImage(cover.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
		}
		label.setIcon(cover);
		lyricArea.setText(songInfoVO.getLyric());
		titleValue.setText(songInfoVO.getTitle());
		artistValue.setText(songInfoVO.getArtist());
		albumValue.setText(songInfoVO.getAlbum());
		yearValue.setText(songInfoVO.getYear());
		sampleRateValue.setText(songInfoVO.getSampleRate());
		bitRateValue.setText(songInfoVO.getBitRate());
		trackLengthValue.setText(songInfoVO.getTrackLengthAsString());
		trackValue.setText(songInfoVO.getTrack());
		channelsValue.setText(songInfoVO.getChannels());
		formatValue.setText(songInfoVO.getFormat());
	}

}
