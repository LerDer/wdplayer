package com.wd.player.gui;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import cn.hutool.core.util.RandomUtil;
import com.apple.eawt.Application;
import com.wd.player.about.AboutPanel;
import com.wd.player.database.entity.SongInfo;
import com.wd.player.database.service.PlayerInfoService;
import com.wd.player.database.service.SongInfoService;
import com.wd.player.database.service.SongPlaylistService;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.dialog.DeleteSongDialog;
import com.wd.player.dialog.ResetDialogDialog;
import com.wd.player.lyric.LyricDisplayer;
import com.wd.player.meta.SongMeta;
import com.wd.player.monitor.SongLoadTaskOne;
import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.enums.PlayTypeEnum;
import com.wd.player.observer.message.MusicEvent;
import com.wd.player.observer.publisher.PlayerPublisher;
import com.wd.player.observer.subscriber.LyricSubscriber;
import com.wd.player.observer.subscriber.MusicPlaySubscriber;
import com.wd.player.observer.subscriber.PlaySliderSubscriber;
import com.wd.player.observer.subscriber.ScrollNameSubscriber;
import com.wd.player.observer.subscriber.Subscriber;
import com.wd.player.observer.subscriber.WaveSubscriber;
import com.wd.player.observer.thread.MainThread;
import com.wd.player.playSlider.MySliderUI;
import com.wd.player.player.MusicPlayer;
import com.wd.player.playlist.SongPlayList;
import com.wd.player.status.StatusManager;
import com.wd.player.util.JMenuBarUtil;
import com.wd.player.util.SongUtil;
import com.wd.player.util.ThreadPoolUtil;
import com.wd.player.util.WindowTool;
import com.wd.player.wave.NoBorderFrame;
import com.wd.player.wave.Spectrum;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lww
 * @date 2024-07-25 7:33 PM
 */
public class MainContainer implements Subscriber {

	public static MainContainer container;
	private JPanel manPanel;
	@Getter
	private JButton likeButton;
	private JButton lastButton;
	@Getter
	private JButton playButton;
	private JButton nextButton;
	private JButton playTypeButton;
	private JButton lyricButton;
	private JButton playListButton;
	private JButton myLikeButton;
	private JButton locationButton;
	private JButton waveButton;
	private JButton origionButton;
	@Getter
	private JLabel songName;
	@Getter
	private JLabel startTime;
	@Getter
	private JLabel endTime;
	private JButton openButton;
	private JButton importButton;
	private JButton resetButton;
	private JButton fileLocationButton;
	private JButton deleteButton;
	private JButton aboutButton;
	@Getter
	private JLabel imageLabel;
	@Getter
	private JSlider playSlider;
	public static volatile boolean isAdjustingTimeBar = false;
	public static volatile Integer current = 0;
	@Getter
	@Setter
	private JFrame frame;

	private static PlayerPublisher publisher = PlayerPublisher.getInstance();

	public static LinkedList<Integer> SONG_IDS = new LinkedList<>();

	public static volatile Integer CURRENT_SONGID;

	private static volatile Rectangle bounds;

	public static SongPlayList playList;

	public static JFrame playListFrame;

	public static SongMeta songMeta;

	public static JFrame songMetaFrame;

	public static volatile boolean showSongMeta = true;

	private static JFrame lyricDisplayFrame;

	private static NoBorderFrame waveDisplayFrame;

	public static volatile Spectrum spec;

	private static volatile boolean showWave = true;

	private static volatile boolean showLyricList = true;

	private static volatile boolean showAbout = false;

	private static JFrame about;

	public static final Integer MAX_SIZE = 3;

	public static LyricDisplayer displayer;

	private static MainThread thread = null;

	private static PlayTypeEnum playType = PlayTypeEnum.ALL_LOOP;

	public static volatile Vector<String> allSongName;

	public static volatile Vector<String> allLikeSongName;

	public static final int width = 550;
	public static final int height = 450;

	public static final int common_height = 150;

	public MainContainer() {
		//初始化状态
		StatusManager.paused = true;
		StatusManager.playing = false;
		StatusManager.playerInfo = PlayerInfoService.getOne();
		playType = PlayTypeEnum.getByCode(StatusManager.playerInfo.getPlayType());
		playTypeButton.setIcon(new ImageIcon(getClass().getResource(playType.getImg())));
		ThreadPoolUtil.getPool().execute(() -> {
			allSongName = SongInfoService.getAllSongNameSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
			allLikeSongName = SongPlaylistService.getAllLikeSongName();
		});
		songName.setText("聆听音乐，热爱生活");
		songName.setPreferredSize(new Dimension(340, 30));
		playTypeButton.setContentAreaFilled(false);
		playTypeButton.setBorderPainted(false);
		playTypeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				playTypeButton.setIcon(new ImageIcon(getClass().getResource(playType.getImg())));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				super.mouseClicked(e);
				int i = playType.getCode();
				i++;
				int i1 = i % 4;
				playType = PlayTypeEnum.getByCode(i1);
				playTypeButton.setIcon(new ImageIcon(getClass().getResource(playType.getEnterImg())));
				StatusManager.playerInfo.setPlayType(playType.getCode());
				PlayerInfoService.update(StatusManager.playerInfo);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				playTypeButton.setIcon(new ImageIcon(getClass().getResource(playType.getEnterImg())));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				playTypeButton.setIcon(new ImageIcon(getClass().getResource(playType.getImg())));
			}
		});
		//打开文件
		openButton.setContentAreaFilled(false);
		openButton.setBorderPainted(false);
		openButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				openButton.setIcon(new ImageIcon(getClass().getResource("/image/open_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				openButton.setIcon(new ImageIcon(getClass().getResource("/image/open.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				openButton.setIcon(new ImageIcon(getClass().getResource("/image/open.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				openButton.setIcon(new ImageIcon(getClass().getResource("/image/open_enter.png")));
			}

		});
		openButton.addActionListener(e -> openFile());
		//导入文件
		importButton.setContentAreaFilled(false);
		importButton.setBorderPainted(false);
		importButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				importButton.setIcon(new ImageIcon(getClass().getResource("/image/import_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				importButton.setIcon(new ImageIcon(getClass().getResource("/image/import.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				importButton.setIcon(new ImageIcon(getClass().getResource("/image/import.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				importButton.setIcon(new ImageIcon(getClass().getResource("/image/import_enter.png")));
			}

		});
		importButton.addActionListener(e -> importFile());
		deleteButton.setContentAreaFilled(false);
		deleteButton.setBorderPainted(false);
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				deleteButton.setIcon(new ImageIcon(getClass().getResource("/image/deletesong_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				deleteButton.setIcon(new ImageIcon(getClass().getResource("/image/deletesong.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				deleteButton.setIcon(new ImageIcon(getClass().getResource("/image/deletesong.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				deleteButton.setIcon(new ImageIcon(getClass().getResource("/image/deletesong_enter.png")));
			}

		});
		deleteButton.addActionListener(e -> deleteFile());
		playListButton.setContentAreaFilled(false);
		playListButton.setBorderPainted(false);
		playListButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				playListButton.setIcon(new ImageIcon(getClass().getResource("/image/playlist_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				playListButton.setIcon(new ImageIcon(getClass().getResource("/image/playlist.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				playListButton.setIcon(new ImageIcon(getClass().getResource("/image/playlist.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				playListButton.setIcon(new ImageIcon(getClass().getResource("/image/playlist_enter.png")));
			}

		});
		playListButton.addActionListener(e -> {
			showSongList(bounds, this.frame, allSongName);
		});
		playButton.setContentAreaFilled(false);
		playButton.setBorderPainted(false);
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				if (StatusManager.paused) {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/play_enter.png")));
				} else {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/pause_enter.png")));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				if (StatusManager.paused) {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/play.png")));
				} else {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/pause.png")));
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (StatusManager.paused) {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/play.png")));
				} else {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/pause.png")));
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if (StatusManager.paused) {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/play_enter.png")));
				} else {
					playButton.setIcon(new ImageIcon(getClass().getResource("/image/pause_enter.png")));
				}
			}
		});
		playButton.addActionListener(e -> playMusic());

		nextButton.setContentAreaFilled(false);
		nextButton.setBorderPainted(false);
		nextButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				nextButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_next_click.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				nextButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_next.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				nextButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_next.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				nextButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_next_click.png")));
			}

		});
		nextButton.addActionListener(e -> playNext());
		lastButton.setContentAreaFilled(false);
		lastButton.setBorderPainted(false);
		lastButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				lastButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_previous_click.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				lastButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_previous.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				lastButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_previous.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				lastButton.setIcon(new ImageIcon(getClass().getResource("/image/skip_previous_click.png")));
			}
		});
		lastButton.addActionListener(e -> playLast());
		//（30，30）  是你要设置按钮的大小
		//likeButton.setPreferredSize(new Dimension(30,30));
		//按钮设置为透明，这样就不会挡着后面的背景
		likeButton.setContentAreaFilled(false);
		//去掉按钮的边框的设置
		likeButton.setBorderPainted(false);
		//对JButton添加图标呢的设置
		//likeButton.setIcon(new ImageIcon(getClass().getResource("qq.png"))); //qq.png是你要添加的图片
		likeButton.addActionListener(e -> likeMusic());
		myLikeButton.setContentAreaFilled(false);
		myLikeButton.setBorderPainted(false);
		myLikeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				myLikeButton.setIcon(new ImageIcon(getClass().getResource("/image/likelist_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				myLikeButton.setIcon(new ImageIcon(getClass().getResource("/image/likelist.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				myLikeButton.setIcon(new ImageIcon(getClass().getResource("/image/likelist.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				myLikeButton.setIcon(new ImageIcon(getClass().getResource("/image/likelist_enter.png")));
			}

		});
		myLikeButton.addActionListener(e -> {
			showSongList(bounds, this.frame, allLikeSongName);
			//SONG_IDS = SongPlaylistService.getAllLikeSongIds();
		});
		lyricButton.setContentAreaFilled(false);
		lyricButton.setBorderPainted(false);
		lyricButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				lyricButton.setIcon(new ImageIcon(getClass().getResource("/image/lyrics_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				lyricButton.setIcon(new ImageIcon(getClass().getResource("/image/lyrics.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				lyricButton.setIcon(new ImageIcon(getClass().getResource("/image/lyrics.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				lyricButton.setIcon(new ImageIcon(getClass().getResource("/image/lyrics_enter.png")));
			}
		});
		lyricButton.addActionListener(e -> showLyric(bounds, this.frame));
		playSlider.setUI(new MySliderUI(this.playSlider));
		playSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				current = playSlider.getValue();
				int maximum = playSlider.getMaximum();
				SongInfo songInfo = SongInfoService.getById(CURRENT_SONGID);
				String filePath = songInfo.getFilePath();
				File file = new File(filePath);
				long length = file.length();
				long l = (long) (length * current * (1.000000000) / (maximum * 1.000000000));
				//滑动到的播放时间
				System.out.println("滑动时间 current = " + (l));
				MusicPlayer.getInstance().seekToSecond(l);
				isAdjustingTimeBar = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				isAdjustingTimeBar = true;
			}

		});
		waveButton.setContentAreaFilled(false);
		waveButton.setBorderPainted(false);
		waveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				waveButton.setIcon(new ImageIcon(getClass().getResource("/image/wave_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				waveButton.setIcon(new ImageIcon(getClass().getResource("/image/wave.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				waveButton.setIcon(new ImageIcon(getClass().getResource("/image/wave.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				waveButton.setIcon(new ImageIcon(getClass().getResource("/image/wave_enter.png")));
			}
		});
		//频谱展示
		waveButton.addActionListener(e -> {
			showWave(bounds, this.frame);
		});
		//切换播放模式
		//playTypeButton.addActionListener(e -> {
		//	int i = playType.getCode();
		//	i++;
		//	int i1 = i % 4;
		//	playType = PlayTypeEnum.getByCode(i1);
		//	playTypeButton.setText(playType.getDes());
		//});
		origionButton.setContentAreaFilled(false);
		origionButton.setBorderPainted(false);
		origionButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				origionButton.setIcon(new ImageIcon(getClass().getResource("/image/tags_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				origionButton.setIcon(new ImageIcon(getClass().getResource("/image/tags.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				origionButton.setIcon(new ImageIcon(getClass().getResource("/image/tags.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				origionButton.setIcon(new ImageIcon(getClass().getResource("/image/tags_enter.png")));
			}

		});
		origionButton.addActionListener(e -> showSongMeta());

		aboutButton.setContentAreaFilled(false);
		aboutButton.setBorderPainted(false);
		aboutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				aboutButton.setIcon(new ImageIcon(getClass().getResource("/image/info_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				aboutButton.setIcon(new ImageIcon(getClass().getResource("/image/info.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				aboutButton.setIcon(new ImageIcon(getClass().getResource("/image/info.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				aboutButton.setIcon(new ImageIcon(getClass().getResource("/image/info_enter.png")));
			}

		});
		resetButton.setContentAreaFilled(false);
		resetButton.setBorderPainted(false);
		resetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				resetButton.setIcon(new ImageIcon(getClass().getResource("/image/reset_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				resetButton.setIcon(new ImageIcon(getClass().getResource("/image/reset.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				resetButton.setIcon(new ImageIcon(getClass().getResource("/image/reset.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				resetButton.setIcon(new ImageIcon(getClass().getResource("/image/reset_enter.png")));
			}

		});
		resetButton.addActionListener(e -> resetDateBase());
		locationButton.setContentAreaFilled(false);
		locationButton.setBorderPainted(false);
		locationButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				locationButton.setIcon(new ImageIcon(getClass().getResource("/image/location_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				locationButton.setIcon(new ImageIcon(getClass().getResource("/image/location.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				locationButton.setIcon(new ImageIcon(getClass().getResource("/image/location.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				locationButton.setIcon(new ImageIcon(getClass().getResource("/image/location_enter.png")));
			}

		});
		locationButton.addActionListener(e -> {
			if (CURRENT_SONGID != null) {
				SongInfo songInfo = SongInfoService.getById(CURRENT_SONGID);
				playList.getSongList().setSelectedIndex(1);
				playList.getSongList().setSelectedValue(songInfo.getSongName(), true);
				playListFrame.setVisible(false);
				playListFrame.setVisible(true);
			}
		});
		aboutButton.addActionListener(e -> {
			if (showAbout && about != null) {
				about.setVisible(false);
				showAbout = false;
			} else {
				int dx, dy;
				dx = bounds.x + 25;
				dy = bounds.y + 75;
				about = new JFrame();
				about.setResizable(false);
				about.setTitle("关于");
				about.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				about.setSize(500, 300);
				about.setLocation(dx, dy);
				AboutPanel aboutPanel = new AboutPanel();
				about.add(aboutPanel);
				about.setVisible(true);
				showAbout = true;
			}
		});
		fileLocationButton.setContentAreaFilled(false);
		fileLocationButton.setBorderPainted(false);
		fileLocationButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				fileLocationButton.setIcon(new ImageIcon(getClass().getResource("/image/open_file_location_enter.png")));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				fileLocationButton.setIcon(new ImageIcon(getClass().getResource("/image/open_file_location.png")));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				fileLocationButton.setIcon(new ImageIcon(getClass().getResource("/image/open_file_location.png")));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				fileLocationButton.setIcon(new ImageIcon(getClass().getResource("/image/open_file_location_enter.png")));
			}

		});
		fileLocationButton.addActionListener(e -> {
			openFileLocation();
		});
	}

	private static void openFileLocation() {
		if (CURRENT_SONGID != null) {
			SongInfo songInfo = SongInfoService.getById(CURRENT_SONGID);
			String path = songInfo.getFilePath();
			File file = new File(path);
			if (file.exists()) {
				Process process = null;
				try {
					String property = System.getProperties().getProperty("os.name");
					if (property.contains("Windows")) {
						process = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "explorer /select, " + path});
					} else if (property.contains("Mac")) {
						process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "open -R '" + path + "'"});
					} else {
						process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "xdg-open '" + path + "'"});
					}
					// 获取标准输入流 process.getInputStream()
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
					// waitFor 阻塞等待 异步进程结束，并返回执行状态，0代表命令执行正常结束。
					System.out.println(process.waitFor());
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	private void resetDateBase() {
		ResetDialogDialog dialog = new ResetDialogDialog("重置数据库", "确定删除歌曲列表库吗？", new ImageIcon(getClass().getResource("/image/reset.png")));
		dialog.setVisible(true);
		if (dialog.getConfirm()) {
			allSongName = new Vector<>();
			allLikeSongName = new Vector<>();
			MusicEvent event = new MusicEvent(PlayMessageEnum.Stop, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
			Integer res = SongInfoService.deleteAllSong();
			//隐藏歌曲列表
			if (playListFrame != null) {
				playListFrame.setVisible(false);
			}
			//隐藏歌词
			if (lyricDisplayFrame != null) {
				lyricDisplayFrame.setVisible(false);
			}
			//隐藏元数据
			if (songMetaFrame != null) {
				songMetaFrame.setVisible(false);
			}
			//隐藏频谱
			if (waveDisplayFrame != null) {
				waveDisplayFrame.setVisible(false);
			}
		}
	}

	private static void showSongMeta() {
		if (CURRENT_SONGID != null) {
			if (showSongMeta) {
				SongInfoVO songInfoVO = SongInfoService.getSongInfoVO(CURRENT_SONGID);
				songMeta = new SongMeta(songInfoVO);
				songMetaFrame = new JFrame();
				songMetaFrame.add(songMeta);
				songMetaFrame.setResizable(false);
				songMetaFrame.setTitle("元信息");
				int dx, dy;
				dx = bounds.x;
				dy = bounds.y - 310;
				songMetaFrame.setSize(width, 300);
				songMetaFrame.setLocation(dx, dy);
				songMetaFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				songMetaFrame.setVisible(true);
				showSongMeta = false;
			} else {
				showSongMeta = true;
				songMetaFrame.setVisible(false);
			}
		}
	}

	private void showWave(Rectangle bounds, JFrame frame) {
		if (showWave) {
			Dimension size = frame.getSize();
			int dx = 0, dy = 0;
			dx = bounds.x;
			dy = bounds.y + (int) size.getHeight() + 3 + common_height + 3;
			showWave = false;
			waveDisplayFrame = new NoBorderFrame();
			spec = new Spectrum(width, common_height);
			waveDisplayFrame.getContentPane().add(spec);
			waveDisplayFrame.setResizable(false);
			waveDisplayFrame.pack();
			waveDisplayFrame.setSize(width, common_height);
			waveDisplayFrame.setLocation(dx, dy);
			waveDisplayFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			waveDisplayFrame.setVisible(true);
		} else {
			if (waveDisplayFrame != null) {
				waveDisplayFrame.setVisible(false);
				waveDisplayFrame.dispose();
			}
			showWave = true;
		}
	}

	private void showLyric(Rectangle gcBounds, JFrame jframe) {
		if (showLyricList) {
			Dimension size = jframe.getSize();
			int dx = 0, dy = 0;
			dx = gcBounds.x;
			dy = gcBounds.y + (int) size.getHeight() + 3;
			displayer = new LyricDisplayer(null);
			lyricDisplayFrame = new JFrame();
			lyricDisplayFrame.setResizable(false);
			lyricDisplayFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			lyricDisplayFrame.add(displayer);
			lyricDisplayFrame.setSize(width, common_height);
			lyricDisplayFrame.setLocation(dx, dy);
			lyricDisplayFrame.setVisible(true);
			showLyricList = false;
		} else {
			if (lyricDisplayFrame != null) {
				lyricDisplayFrame.setVisible(false);
				lyricDisplayFrame.dispose();
			}
			showLyricList = true;
		}

	}

	private void likeMusic() {
		if (CURRENT_SONGID == null) {
			return;
		}
		if (StatusManager.liked) {
			likeButton.setIcon(new ImageIcon(getClass().getResource("/image/unlike.png")));
			SongPlaylistService.unlikeSong(CURRENT_SONGID);
			StatusManager.liked = false;
		} else {
			likeButton.setIcon(new ImageIcon(getClass().getResource("/image/liked.png")));
			SongPlaylistService.likeSong(CURRENT_SONGID);
			StatusManager.liked = true;
		}
		ThreadPoolUtil.getPool().execute(() -> {
			allLikeSongName = SongPlaylistService.getAllLikeSongName();
			showSongList(bounds, this.frame, allLikeSongName);
		});
	}

	private void playNext() {
		if (CURRENT_SONGID == null) {
			//播放一首新的歌曲
			SONG_IDS = SongInfoService.getAllSongIdsSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
			CURRENT_SONGID = SONG_IDS.get(0);
			MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
		} else {
			CURRENT_SONGID = getLoopNext(CURRENT_SONGID);
			//下一首
			MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
		}
	}

	private void playLast() {
		if (CURRENT_SONGID == null) {
			//播放一首新的歌曲
			SONG_IDS = SongInfoService.getAllSongIdsSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
			CURRENT_SONGID = SONG_IDS.get(0);
			MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
		} else {
			CURRENT_SONGID = getLoopLast(CURRENT_SONGID);
			//上一首
			MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
		}
	}

	private Integer getLoopNext(Integer currentSongId) {
		switch (playType) {
			case RANDOM:
				int i1 = RandomUtil.randomInt(0, SONG_IDS.size() - 1);
				return SONG_IDS.get(i1);
			case ONE_LOOP:
			case ALL_LOOP:
			case SEQUENCE:
				int i = SONG_IDS.indexOf(currentSongId);
				if (i == SONG_IDS.size() - 1) {
					return SONG_IDS.get(0);
				} else {
					return SONG_IDS.get(i + 1);
				}
		}
		return currentSongId;
	}

	private Integer getLoopLast(Integer currentSongId) {
		switch (playType) {
			case RANDOM:
				int i1 = RandomUtil.randomInt(0, SONG_IDS.size() - 1);
				return SONG_IDS.get(i1);
			case ONE_LOOP:
			case ALL_LOOP:
			case SEQUENCE:
				int i = SONG_IDS.indexOf(currentSongId);
				if (i == 0) {
					return SONG_IDS.get(SONG_IDS.size() - 1);
				} else {
					return SONG_IDS.get(i - 1);
				}
		}
		return currentSongId;
	}

	private void playMusic() {
		if (StatusManager.paused) {
			if (CURRENT_SONGID == null) {
				//播放一首新的歌曲
				SONG_IDS = SongInfoService.getAllSongIdsSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
				CURRENT_SONGID = SONG_IDS.get(0);
				MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
				PlayerPublisher.getInstance().notifyObservers(event);
			} else {
				//暂停当前播放的歌曲
				MusicEvent event = new MusicEvent(PlayMessageEnum.Recover, CURRENT_SONGID);
				PlayerPublisher.getInstance().notifyObservers(event);
			}
		} else {
			MusicEvent event = new MusicEvent(PlayMessageEnum.Pause, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
		}
	}

	private static void showSongList(Rectangle gcBounds, JFrame frame, Vector<String> songNames) {
		if (playListFrame != null) {
			playListFrame.setVisible(false);
			playListFrame.dispose();
		}
		//调用了pack方法后，gcBounds中宽度不准确，要使用frame的getSize()方法
		Dimension size = frame.getSize();
		//设置窗口居中
		int dx, dy;
		dx = gcBounds.x + size.width + 10;
		dy = gcBounds.y + 30;
		playListFrame = new JFrame();
		playList = new SongPlayList(songNames);
		playListFrame.add(playList);
		playListFrame.setResizable(false);
		playListFrame.setLocation(dx, dy);
		playListFrame.setSize(250, 400);
		playListFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//songListFrame.pack();
		playListFrame.setVisible(true);
	}

	static Timer timer;

	public static void importFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setCurrentDirectory(new File("/Users/lww/Music/QQMusic/"));
		fileChooser.setCurrentDirectory(null);
		fileChooser.showOpenDialog(null);
		File selectedFile = fileChooser.getSelectedFile();
		//选择了文件
		if (selectedFile != null) {
			File[] files = selectedFile.listFiles();
			//文件不为空，导入音乐
			if (files != null) {
				ProgressMonitor dialog = new ProgressMonitor(null,
						"导入中，请不要关闭窗口...",
						"已完成：0.00%",
						0,
						files.length);
				//SongLoadTask loadTask = new SongLoadTask(Arrays.asList(files));
				//loadTask.start();
				SongLoadTaskOne loadTask = new SongLoadTaskOne(files);
				loadTask.start();

				// 创建一个计时器
				timer = new Timer(300, e -> {
					// 以任务的当前完成量设置进度对话框的完成比例
					dialog.setProgress(loadTask.getCurrent());
					dialog.setNote("已完成：" + loadTask.getPercent());
					// 如果用户单击了进度对话框的” 取消 “按钮
					if (dialog.isCanceled()) {
						// 停止计时器
						timer.stop();
						// 中断任务的执行线程
						// 系统退出
						//System.exit(0);
					}
					if (loadTask.isFinished()) {
						timer.stop();
					}
				});
				timer.start();
			}
		}
	}

	public static void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(new File("/Users/lww/Music/QQMusic/"));
		fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return SongUtil.isMusic(f);
			}

			@Override
			public String getDescription() {
				return "音乐文件";
			}
		});
		fileChooser.setCurrentDirectory(null);
		fileChooser.showOpenDialog(null);
		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile == null) {
			return;
		}
		System.out.println("MainContainer_MainContainer_selectedFile:{}" + selectedFile.getAbsolutePath());
		try {
			Integer songId = SongInfoService.saveSongInfo(selectedFile);
			CURRENT_SONGID = songId;
			SONG_IDS = SongInfoService.getAllSongIdsSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
			allSongName = SongInfoService.getAllSongNameSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
			showSongList(bounds, container.frame, allSongName);
			MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
			StatusManager.paused = false;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void deleteFile() {
		if (CURRENT_SONGID == null) {
			//弹窗提示
			JOptionPane.showMessageDialog(null, "请选择要删除的歌曲");
			return;
		}
		SongInfoVO infoVO = SongInfoService.getSongInfoVO(CURRENT_SONGID);
		DeleteSongDialog myDialog = new DeleteSongDialog("确定删除？", "确定删除 " + infoVO.getFileName() + " ?", new ImageIcon(getClass().getResource("/image/delete.png")));
		myDialog.setVisible(true);
		if (myDialog.getConfirm()) {
			Integer res = SongInfoService.deleteSong(CURRENT_SONGID);
			System.out.println("MainContainer_deleteFile_CURRENT_SONGID:{}" + CURRENT_SONGID);
			CURRENT_SONGID = getLoopNext(CURRENT_SONGID);
			SONG_IDS = SongInfoService.getAllSongIdsSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
			//隐藏
			playListFrame.setVisible(false);
			allSongName = SongInfoService.getAllSongNameSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
			showSongList(bounds, this.frame, allSongName);
			MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
			PlayerPublisher.getInstance().notifyObservers(event);
		}
	}

	public static void start() {
		JFrame jFrame = new JFrame();
		container = new MainContainer();
		container.setFrame(jFrame);
		jFrame.setContentPane(container.manPanel);
		jFrame.setResizable(false);
		jFrame.setSize(width, height);
		WindowTool.center(jFrame);
		jFrame.setJMenuBar(new JMenuBarUtil());
		jFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				Component c = e.getComponent();
				bounds = c.getBounds();
			}
		});
		//隐藏当前窗口，并释放窗体占有的其他资源。在窗口被关闭的时候会dispose这个窗口
		//jFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		jFrame.setTitle("微点音乐");
		Image icon = new ImageIcon(MainContainer.class.getResource("/image/logo.png")).getImage();
		jFrame.setIconImage(icon.getScaledInstance(32, 32, Image.SCALE_DEFAULT));
		//mac 不支持 setIconImage 可以设置 setDockIconImage
		Application.getApplication().setDockIconImage(icon);
		//Application.getApplication().setDockIconBadge("微点音乐");
		//结束窗口所在的应用程序。在窗口被关闭的时候会退出JVM
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setVisible(true);
		//jFrame.pack();

		//注册观察者 音乐播放器
		publisher.addObserver(MusicPlaySubscriber.getInstance());
		//歌词显示器
		publisher.addObserver(LyricSubscriber.getInstance());
		//频谱显示器
		publisher.addObserver(WaveSubscriber.getInstance());
		//进度条显示器
		publisher.addObserver(PlaySliderSubscriber.getInstance());
		publisher.addObserver(ScrollNameSubscriber.getInstance());
		//时间显示器
		publisher.addObserver(container);

	}

	private void clean() {
		playButton.setIcon(new ImageIcon(getClass().getResource("/image/play.png")));
		startTime.setText("0:00");
		endTime.setText("0:00");
		imageLabel.setIcon(null);
		playSlider.setMinimum(0);
		playSlider.setMaximum(100);
		songName.setText("聆听音乐");
		likeButton.setIcon(new ImageIcon(getClass().getResource("/image/unlike.png")));
	}

	@Override
	public void update(MusicEvent musicEvent) throws Exception {
		PlayMessageEnum messageEnum = musicEvent.getPlayMessage();
		switch (messageEnum) {
			case Play:
				StatusManager.playing = true;
				StatusManager.paused = false;
				if (thread != null) {
					thread.stop();
					thread = null;
				}
				thread = new MainThread(musicEvent);
				thread.setContainer(this);
				thread.setDaemon(true);
				thread.start();
				break;
			case Finish:
				Integer songId1 = musicEvent.getSongId();
				switch (playType) {
					case ONE_LOOP:
						break;
					case RANDOM:
						int i1 = RandomUtil.randomInt(0, SONG_IDS.size() - 1);
						CURRENT_SONGID = SONG_IDS.get(i1);
						break;
					case ALL_LOOP:
					case SEQUENCE:
						SONG_IDS = SongInfoService.getAllSongIdsSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
						int i = SONG_IDS.indexOf(songId1);
						int size = SONG_IDS.size();
						if (i == size - 1) {
							CURRENT_SONGID = SONG_IDS.getFirst();
						} else {
							CURRENT_SONGID = SONG_IDS.get(i + 1);
						}
						break;
				}
				ThreadPoolUtil.getPool().execute(() -> {
					MusicEvent event = new MusicEvent(PlayMessageEnum.Play, CURRENT_SONGID);
					PlayerPublisher.getInstance().notifyObservers(event);
				});
				break;
			case Pause:
				playButton.setIcon(new ImageIcon(getClass().getResource("/image/play.png")));
				messageEnum.handle(thread);
				break;
			case Stop:
				StatusManager.playing = false;
				StatusManager.paused = true;
				SongMeta.clean();
				clean();
				break;
			default:
				messageEnum.handle(thread);
		}
	}

}
