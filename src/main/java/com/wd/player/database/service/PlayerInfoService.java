package com.wd.player.database.service;

import com.wd.player.database.data.DataSourceManager;
import com.wd.player.database.entity.PlayerInfo;
import com.wd.player.database.mapper.PlayerInfoMapper;
import com.wd.player.observer.enums.PlayTypeEnum;
import org.apache.ibatis.session.SqlSession;

/**
 * @author lww
 * @date 2024-11-17 03:56
 */
public class PlayerInfoService {

	private PlayerInfoService() {
	}

	public static PlayerInfo getOne() {
		SqlSession session = DataSourceManager.getSession();
		PlayerInfoMapper playerInfoMapper = session.getMapper(PlayerInfoMapper.class);
		PlayerInfo playerInfo = playerInfoMapper.selectById(1);
		if (playerInfo == null) {
			playerInfo = new PlayerInfo();
			playerInfo.setId(1);
			playerInfo.setPlayType(PlayTypeEnum.ALL_LOOP.getCode());
			playerInfo.setSortColumn("CREATE_TIME");
			playerInfo.setSortType(0);
			playerInfoMapper.insert(playerInfo);
		}
		session.commit();
		session.close();
		return playerInfo;
	}

	public static void update(PlayerInfo playerInfo) {
		SqlSession session = DataSourceManager.getSession();
		PlayerInfoMapper playerInfoMapper = session.getMapper(PlayerInfoMapper.class);
		playerInfoMapper.updateById(playerInfo);
		session.commit();
		session.close();
	}

}
