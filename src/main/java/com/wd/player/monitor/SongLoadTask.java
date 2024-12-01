package com.wd.player.monitor;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.wd.player.database.data.DataSourceManager;
import com.wd.player.database.entity.SongInfo;
import com.wd.player.database.mapper.SongInfoMapper;
import com.wd.player.database.service.SongInfoService;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.gui.MainContainer;
import com.wd.player.status.StatusManager;
import com.wd.player.util.ImportPoolUtil;
import com.wd.player.util.SongUtil;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.SqlSession;

/**
 * @author lww
 * @date 2024-10-18 00:14
 */
public class SongLoadTask extends Thread {

	@Setter
	@Getter
	private Integer current;
	@Setter
	@Getter
	private List<File> files;

	@Getter
	private ThreadPoolExecutor pool;

	public SongLoadTask(List<File> files) {
		this.current = 0;
		this.files = files;
	}

	public boolean isFinished() {
		return this.getCurrent() >= this.getAmount();
	}

	public String getPercent() {
		return String.format("%.2f", 100.0 * this.current / this.getAmount()) + "%";
	}

	public int getAmount() {
		return files.size();
	}

	@Override
	public void run() {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		pool = ImportPoolUtil.getNewPool();
		int size = files.size();
		// 5个
		int i = size / 5;
		List<File> files1 = files.subList(0, i);
		for (File file : files1) {
			pool.submit(() -> saveFile(file, songInfoMapper));
		}
		List<File> files2 = files.subList(i, i * 2);
		for (File file : files2) {
			pool.submit(() -> saveFile(file, songInfoMapper));
		}
		List<File> files3 = files.subList(i * 2, i * 3);
		for (File file : files3) {
			pool.submit(() -> saveFile(file, songInfoMapper));
		}
		List<File> files4 = files.subList(i * 3, i * 4);
		for (File file : files4) {
			pool.submit(() -> saveFile(file, songInfoMapper));
		}
		List<File> files5 = files.subList(i * 4, size);
		for (File file : files5) {
			pool.submit(() -> saveFile(file, songInfoMapper));
		}

		this.current = (int) pool.getCompletedTaskCount();
		while (this.current != this.getAmount()) {
			this.current = (int) pool.getCompletedTaskCount();
			System.out.println("current = " + this.current + "amount = " + this.getAmount());
		}

		session.commit();
		//session.close();
		MainContainer.SONG_IDS = SongInfoService.getAllSongIdsSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
		MainContainer.allSongName = SongInfoService.getAllSongNameSort(StatusManager.playerInfo.getSortColumn(), StatusManager.playerInfo.getSortType());
		//查出所有歌的id
		//如果当前为空，说明没有当前播放的歌曲，就取第一个
		if (MainContainer.CURRENT_SONGID == null && CollectionUtil.isNotEmpty(MainContainer.SONG_IDS)) {
			MainContainer.CURRENT_SONGID = MainContainer.SONG_IDS.getFirst();
		}
		if (MainContainer.playListFrame != null) {

			MainContainer.playListFrame.setVisible(false);
		}
		MainContainer.allLikeSongName = new Vector<>();
		System.out.println("end");
	}

	private void saveFile(File file, SongInfoMapper songInfoMapper) {
		if (SongUtil.isMusic(file)) {
			List<SongInfo> songInfos = songInfoMapper.selectList(new LambdaQueryWrapper<SongInfo>()
					.eq(SongInfo::getFilePath, file.getAbsolutePath()));
			if (CollectionUtils.isNotEmpty(songInfos)) {
				return;
			}
			try {
				SongInfoVO songInfo = SongUtil.getSongInfo(file);
				SongInfo convert = SongInfoVO.convert(songInfo);
				int insert = songInfoMapper.insert(convert);
				System.out.println("insert = " + insert);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

}
