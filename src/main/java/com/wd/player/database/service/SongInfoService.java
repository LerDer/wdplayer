package com.wd.player.database.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.wd.player.database.data.DataSourceManager;
import com.wd.player.database.entity.SongInfo;
import com.wd.player.database.mapper.SongInfoMapper;
import com.wd.player.database.mapper.SongPlaylistMidMapper;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.util.SongUtil;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;

/**
 * @author lww
 * @date 2024-10-18 10:55
 */
public class SongInfoService {

	private SongInfoService() {
	}

	@SneakyThrows
	public static Integer saveSongInfo(File selectedFile) {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		String filePath = selectedFile.getAbsolutePath();
		List<SongInfo> songInfos = songInfoMapper.selectList(new LambdaQueryWrapper<SongInfo>()
				.eq(SongInfo::getFilePath, filePath));
		if (CollectionUtils.isEmpty(songInfos)) {
			SongInfoVO songInfo = SongUtil.getSongInfo(selectedFile);
			SongInfo convert = SongInfoVO.convert(songInfo);
			songInfoMapper.insert(convert);
			session.commit();
			session.close();
			return convert.getId();
		} else {
			return songInfos.get(0).getId();
		}
	}

	@SneakyThrows
	public static SongInfoVO getSongInfoVO(Integer songId) {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		SongInfo songInfo = songInfoMapper.selectById(songId);
		if (songInfo == null) {
			return new SongInfoVO();
		} else {
			session.commit();
			session.close();
			File file = new File(songInfo.getFilePath());
			SongInfoVO infoVO = SongUtil.getSongInfo(file);
			return infoVO;
		}
	}

	@SneakyThrows
	public static SongInfo getById(Integer songId) {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		SongInfo songInfo = songInfoMapper.selectById(songId);
		Assert.isTrue(songInfo != null, "歌曲id: " + songId + " 查询歌曲失败！");
		session.commit();
		session.close();
		return songInfo;
	}

	@SneakyThrows
	public static SongInfo getSongInfoByName(String fileName) {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		SongInfo songInfo = songInfoMapper.selectOne(new LambdaQueryWrapper<SongInfo>()
				.eq(SongInfo::getFileName, fileName));
		Assert.isTrue(songInfo != null, "歌曲名称: " + fileName + " 查询歌曲失败！");
		session.commit();
		session.close();
		return songInfo;
	}

	//public static LinkedList<Integer> getAllSongIds() {
	//	SqlSession session = DataSourceManager.getSession();
	//	SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
	//	//查出所有歌的id
	//	List<Integer> songIdList = songInfoMapper.selectAllSongIds();
	//	session.commit();
	//	session.close();
	//	LinkedList<Integer> songIds = new LinkedList<>(songIdList);
	//	return songIds;
	//}

	public static Integer deleteSong(Integer currentSongid) {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		SongInfo songInfo = songInfoMapper.selectById(currentSongid);
		if (songInfo == null) {
			return 0;
		}
		int deletedById = songInfoMapper.deleteById(currentSongid);
		System.out.println("deletedById = " + deletedById);
		String filePath = songInfo.getFilePath();
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		return 1;
	}

	//public static Vector<String> getAllSongName() {
	//	SqlSession session = DataSourceManager.getSession();
	//	SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
	//	List<String> songNameList = songInfoMapper.selectAllName();
	//	session.commit();
	//	session.close();
	//	return new Vector<>(songNameList);
	//}

	public static Integer deleteAllSong() {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		Integer res = songInfoMapper.deleteAll();
		SongPlaylistMidMapper songPlaylistMidMapper = session.getMapper(SongPlaylistMidMapper.class);
		Integer res1 = songPlaylistMidMapper.deleteAll();
		session.commit();
		session.close();
		return res;
	}

	public static Vector getAllSongNameSort(String column, Integer order) {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		List<String> songNameList = songInfoMapper.selectAllNameBy(column, order);
		session.commit();
		session.close();
		return new Vector<>(songNameList);
	}

	public static LinkedList<Integer> getAllSongIdsSort(String column, Integer order) {
		SqlSession session = DataSourceManager.getSession();
		SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
		List<Integer> songIdList = songInfoMapper.selectAllSongIdsSort(column, order);
		session.commit();
		session.close();
		LinkedList<Integer> songIds = new LinkedList<>(songIdList);
		return songIds;
	}
}
