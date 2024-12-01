package com.wd.player.database.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wd.player.database.data.DataSourceManager;
import com.wd.player.database.entity.SongPlaylist;
import com.wd.player.database.entity.SongPlaylistMid;
import com.wd.player.database.mapper.SongInfoMapper;
import com.wd.player.database.mapper.SongPlaylistMapper;
import com.wd.player.database.mapper.SongPlaylistMidMapper;
import com.wd.player.util.DateUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.ibatis.session.SqlSession;

/**
 * @author lww
 * @date 2024-10-21 00:31
 */
public class SongPlaylistService {

	private SongPlaylistService() {
	}

	public static Boolean isLikeSong(Integer currentSongId) {
		SqlSession session = DataSourceManager.getSession();
		SongPlaylistMapper songPlaylistMapper = session.getMapper(SongPlaylistMapper.class);
		List<SongPlaylist> playlists = songPlaylistMapper.selectList(new LambdaQueryWrapper<SongPlaylist>()
				.eq(SongPlaylist::getPlaylistName, "我的喜欢"));
		if (CollectionUtil.isEmpty(playlists)) {
			SongPlaylist playlist = new SongPlaylist();
			playlist.setPlaylistName("我的喜欢");
			int insert = songPlaylistMapper.insert(playlist);
			System.out.println("MusicPlayer_like_insert:{}" + insert);
			session.commit();
			session.close();
			//System.out.println("不喜欢 = " + currentSongId);
			return false;
		} else {
			Integer playListId = playlists.get(0).getId();
			SongPlaylistMidMapper songPlaylistMidMapper = session.getMapper(SongPlaylistMidMapper.class);
			List<SongPlaylistMid> songPlaylistMids = songPlaylistMidMapper.selectList(new LambdaQueryWrapper<SongPlaylistMid>()
					.eq(SongPlaylistMid::getPlaylistId, playListId)
					.eq(SongPlaylistMid::getSongId, currentSongId));
			session.commit();
			session.close();
			//System.out.println("不喜欢 = " + currentSongId);
			return CollectionUtil.isNotEmpty(songPlaylistMids);
		}
	}

	public static void likeSong(Integer currentSongId) {
		SqlSession session = DataSourceManager.getSession();
		SongPlaylistMapper songPlaylistMapper = session.getMapper(SongPlaylistMapper.class);
		List<SongPlaylist> playlists = songPlaylistMapper.selectList(new LambdaQueryWrapper<SongPlaylist>()
				.eq(SongPlaylist::getPlaylistName, "我的喜欢"));
		Integer playListId = null;
		if (CollectionUtil.isEmpty(playlists)) {
			SongPlaylist playlist = new SongPlaylist();
			playlist.setPlaylistName("我的喜欢");
			int insert = songPlaylistMapper.insert(playlist);
			playListId = playlist.getId();
			System.out.println("MusicPlayer_like_insert:{}" + insert);
		} else {
			playListId = playlists.get(0).getId();
		}
		SongPlaylistMidMapper songPlaylistMidMapper = session.getMapper(SongPlaylistMidMapper.class);
		List<SongPlaylistMid> songPlaylistMids = songPlaylistMidMapper.selectList(new LambdaQueryWrapper<SongPlaylistMid>()
				.eq(SongPlaylistMid::getPlaylistId, playListId)
				.eq(SongPlaylistMid::getSongId, currentSongId));
		if (CollectionUtil.isEmpty(songPlaylistMids)) {
			SongPlaylistMid mid = new SongPlaylistMid();
			mid.setPlaylistId(playListId);
			mid.setSongId(currentSongId);
			mid.setCreateTime(DateUtil.currentMillis());
			int insert = songPlaylistMidMapper.insert(mid);
			System.out.println("MusicPlayer_like_insert:{}" + insert);
		}
		session.commit();
		session.close();
	}

	public static void unlikeSong(Integer currentSongId) {
		SqlSession session = DataSourceManager.getSession();
		SongPlaylistMapper songPlaylistMapper = session.getMapper(SongPlaylistMapper.class);
		List<SongPlaylist> playlists = songPlaylistMapper.selectList(new LambdaQueryWrapper<SongPlaylist>()
				.eq(SongPlaylist::getPlaylistName, "我的喜欢"));
		if (CollectionUtil.isEmpty(playlists)) {
			SongPlaylist playlist = new SongPlaylist();
			playlist.setPlaylistName("我的喜欢");
			int insert = songPlaylistMapper.insert(playlist);
			System.out.println("MusicPlayer_like_insert:{}" + insert);
			session.commit();
			session.close();
		} else {
			Integer playListId = playlists.get(0).getId();
			SongPlaylistMidMapper songPlaylistMidMapper = session.getMapper(SongPlaylistMidMapper.class);
			List<SongPlaylistMid> songPlaylistMids = songPlaylistMidMapper.selectList(new LambdaQueryWrapper<SongPlaylistMid>()
					.eq(SongPlaylistMid::getPlaylistId, playListId)
					.eq(SongPlaylistMid::getSongId, currentSongId));
			if (CollectionUtil.isEmpty(songPlaylistMids)) {
				session.commit();
				session.close();
			} else {
				int delete = songPlaylistMidMapper.delete(new LambdaQueryWrapper<SongPlaylistMid>()
						.eq(SongPlaylistMid::getPlaylistId, playListId)
						.eq(SongPlaylistMid::getSongId, currentSongId));
				System.out.println("MusicPlayer_like_delete:{}" + delete);
				session.commit();
				session.close();
			}
		}
	}

	public static Vector<String> getAllLikeSongName() {
		List<String> songNames = new ArrayList<>();
		SqlSession session = DataSourceManager.getSession();
		SongPlaylistMapper songPlaylistMapper = session.getMapper(SongPlaylistMapper.class);
		List<SongPlaylist> playlists = songPlaylistMapper.selectList(new LambdaQueryWrapper<SongPlaylist>()
				.eq(SongPlaylist::getPlaylistName, "我的喜欢"));
		if (CollectionUtil.isEmpty(playlists)) {
			SongPlaylist playlist = new SongPlaylist();
			playlist.setPlaylistName("我的喜欢");
			int insert = songPlaylistMapper.insert(playlist);
			System.out.println("MusicPlayer_like_insert:{}" + insert);
			//我的喜欢是空的，返回一个空集合
			session.commit();
			session.close();
		} else {
			Integer playListId = playlists.get(0).getId();
			SongPlaylistMidMapper songPlaylistMidMapper = session.getMapper(SongPlaylistMidMapper.class);
			//查中间表，获取所有的歌曲id
			List<SongPlaylistMid> songPlaylistMids = songPlaylistMidMapper.selectList(new LambdaQueryWrapper<SongPlaylistMid>()
					.eq(SongPlaylistMid::getPlaylistId, playListId)
					.orderByAsc(true, SongPlaylistMid::getCreateTime));
			if (CollectionUtil.isNotEmpty(songPlaylistMids)) {
				List<Integer> songIds = songPlaylistMids
						.stream()
						.map(SongPlaylistMid::getSongId)
						.distinct()
						.collect(Collectors.toList());
				SongInfoMapper songInfoMapper = session.getMapper(SongInfoMapper.class);
				//查歌曲表，获取所有的歌曲名称
				songNames = songInfoMapper.selectAllLikeSongName(songIds);
				session.commit();
				session.close();
			}
		}
		return new Vector<>(songNames);
	}

	public static LinkedList<Integer> getAllLikeSongIds() {
		SqlSession session = DataSourceManager.getSession();
		SongPlaylistMapper songPlaylistMapper = session.getMapper(SongPlaylistMapper.class);
		List<SongPlaylist> playlists = songPlaylistMapper.selectList(new LambdaQueryWrapper<SongPlaylist>()
				.eq(SongPlaylist::getPlaylistName, "我的喜欢"));
		if (CollectionUtil.isEmpty(playlists)) {
			SongPlaylist playlist = new SongPlaylist();
			playlist.setPlaylistName("我的喜欢");
			int insert = songPlaylistMapper.insert(playlist);
			System.out.println("MusicPlayer_like_insert:{}" + insert);
			//我的喜欢是空的，返回一个空集合
			session.commit();
			session.close();
			return new LinkedList<>();
		} else {
			Integer playListId = playlists.get(0).getId();
			SongPlaylistMidMapper songPlaylistMidMapper = session.getMapper(SongPlaylistMidMapper.class);
			//查中间表，获取所有的歌曲id
			List<SongPlaylistMid> songPlaylistMids = songPlaylistMidMapper.selectList(new LambdaQueryWrapper<SongPlaylistMid>()
					.eq(SongPlaylistMid::getPlaylistId, playListId)
					.orderByAsc(true, SongPlaylistMid::getCreateTime));
			List<Integer> songIds = songPlaylistMids
					.stream()
					.map(SongPlaylistMid::getSongId)
					.distinct()
					.collect(Collectors.toList());
			session.commit();
			session.close();
			return new LinkedList<>(songIds);
		}
	}
}
