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
import com.wd.player.util.SongUtil;
import java.io.File;
import java.util.List;
import java.util.Vector;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;

/**
 * @author lww
 * @date 2024-10-18 00:14
 */
public class SongLoadTaskOne extends Thread {

	// 任务的当前完成量
	@Getter
	private volatile int current;
	// 总任务量
	@Getter
	private int amount;

	File[] files;

	public SongLoadTaskOne(File[] files) {
		this.current = 0;
		this.files = files;
		this.amount = files.length;
	}

	public String getPercent() {
		return String.format("%.2f", 100.0 * current / amount) + "%";
	}

	@SneakyThrows
	@Override
	public void run() {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		for (File file : files) {
			if (SongUtil.isMusic(file)) {
				List<SongInfo> songInfos = songInfoMapper.selectList(new LambdaQueryWrapper<SongInfo>()
						.eq(SongInfo::getFilePath, file.getAbsolutePath()));
				if (CollectionUtils.isNotEmpty(songInfos)) {
					current++;
					continue;
				}
				SongInfoVO songInfo = SongUtil.getSongInfo(file);
				SongInfo convert = SongInfoVO.convert(songInfo);
				int insert = songInfoMapper.insert(convert);
				System.out.println("insert = " + insert);
			}
			current++;
		}
		session.commit();
		session.close();
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

	public boolean isFinished() {
		return this.getCurrent() >= this.getAmount();
	}
}
