package com.wd.player.playlist;

import com.wd.player.database.entity.SongInfo;
import com.wd.player.database.service.PlayerInfoService;
import com.wd.player.database.service.SongInfoService;
import com.wd.player.gui.MainContainer;
import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.publisher.PlayerPublisher;
import com.wd.player.status.StatusManager;
import com.wd.player.util.SwingUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;

/**
 * @author lww
 * @date 2024-10-18 18:12
 */
public class SongPlayList extends JPanel {

	private String songName;

	@Getter
	private JList<String> songList;

	public SongPlayList(Vector<String> songNames) {
		setLayout(null);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JLabel sort = SwingUtil.getLabel("排序:");
		sort.setBounds(5, 5, 30, 15);
		JButton name = new JButton("名称");//FILE_NAME
		name.setBounds(35, 5, 30, 15);
		name.addActionListener(e -> {
			StatusManager.playerInfo.setSortType(1);
			StatusManager.playerInfo.setSortColumn("FILE_NAME");
			PlayerInfoService.update(StatusManager.playerInfo);
			Vector fileName = SongInfoService.getAllSongNameSort("FILE_NAME", 1);
			songList.setListData(fileName);
			this.repaint();
			MainContainer.SONG_IDS = SongInfoService.getAllSongIdsSort("FILE_NAME", 1);
		});

		JButton singer = new JButton("歌手");//ARTIST
		singer.setBounds(65, 5, 30, 15);
		singer.addActionListener(e -> {
			StatusManager.playerInfo.setSortType(1);
			StatusManager.playerInfo.setSortColumn("ARTIST");
			PlayerInfoService.update(StatusManager.playerInfo);
			Vector artist = SongInfoService.getAllSongNameSort("ARTIST", 1);
			songList.setListData(artist);
			this.repaint();
			MainContainer.SONG_IDS = SongInfoService.getAllSongIdsSort("ARTIST", 1);
		});
		JButton album = new JButton("专辑");//ALBUM
		album.setBounds(95, 5, 30, 15);
		album.addActionListener(e -> {
			StatusManager.playerInfo.setSortType(1);
			StatusManager.playerInfo.setSortColumn("ALBUM");
			PlayerInfoService.update(StatusManager.playerInfo);
			Vector albumName = SongInfoService.getAllSongNameSort("ALBUM", 1);
			songList.setListData(albumName);
			this.repaint();
			MainContainer.SONG_IDS = SongInfoService.getAllSongIdsSort("ALBUM", 1);
		});

		JButton createTime = new JButton("创建时间");//CREATE_TIME
		createTime.setBounds(125, 5, 65, 15);
		createTime.addActionListener(e -> {
			StatusManager.playerInfo.setSortType(1);
			StatusManager.playerInfo.setSortColumn("CREATE_TIME");
			PlayerInfoService.update(StatusManager.playerInfo);
			Vector createTimeName = SongInfoService.getAllSongNameSort("CREATE_TIME", 1);
			songList.setListData(createTimeName);
			this.repaint();
			MainContainer.SONG_IDS = SongInfoService.getAllSongIdsSort("CREATE_TIME", 1);
		});

		JButton location = new JButton("定位");
		location.setBounds(210, 5, 30, 15);
		location.addActionListener(e -> {
			songList.setSelectedIndex(1);
			Integer currentSongid = MainContainer.CURRENT_SONGID;
			SongInfo songInfo = SongInfoService.getById(currentSongid);
			songName = songInfo.getSongName();
			songList.setSelectedValue(songName, true);
		});
		songList = new JList<>();
		//限制只能选择一个元素
		songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		songList.setListData(songNames);
		//在滚动面板中显示列表
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(songList);
		scrollPane.setBounds(5, 20, 240, 370);

		add(sort);
		add(name);
		add(singer);
		add(album);
		add(location);
		add(createTime);
		add(scrollPane, BorderLayout.CENTER);

		songList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
			}
		});
		songList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					songName = songList.getSelectedValue();
					SongInfo songInfo = SongInfoService.getSongInfoByName(songName);
					//System.out.println("songName = " + songName);
					MusicEvent event = new MusicEvent(PlayMessageEnum.Play, songInfo.getId());
					PlayerPublisher.getInstance().notifyObservers(event);
				}
			}
		});
	}

}
