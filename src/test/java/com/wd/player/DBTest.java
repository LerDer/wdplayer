package com.wd.player;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wd.player.database.data.DataSourceManager;
import com.wd.player.database.entity.SongInfo;
import com.wd.player.database.mapper.SongInfoMapper;
import com.wd.player.database.vo.LyricVO;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.util.LyricUtil;
import com.wd.player.util.SongUtil;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lww
 * @date 2024-07-30 3:04 PM
 */

public class DBTest {

	private SqlSession session;

	@Before
	public void testGetSession() throws Exception {
		this.session = DataSourceManager.getSession();
	}

	@After
	public void after() {
		this.session.commit();
		this.session.close();
	}

	@Test
	public void testInsert() {
		SongInfo songInfo = new SongInfo();
		songInfo.setSongName("测试测试测试测");
		SongInfoMapper mapper = session.getMapper(SongInfoMapper.class);
		mapper.insert(songInfo);
		System.out.println(songInfo);
	}

	@Test
	public void testDelete() {
		SongInfoMapper mapper = session.getMapper(SongInfoMapper.class);
		int result = mapper.deleteById(5);
		System.out.println(result);
	}

	@Test
	public void testUpdate() {
		SongInfoMapper mapper = session.getMapper(SongInfoMapper.class);
		SongInfo songInfo = mapper.selectById(1);
		songInfo.setSongName("测试测试");
		mapper.updateById(songInfo);
	}

	@Test
	public void testSelect() {
		SongInfoMapper mapper = session.getMapper(SongInfoMapper.class);
		List<SongInfo> infos = mapper.selectList(new LambdaQueryWrapper<>());
		System.out.println("infos = " + infos);
	}

	@Test
	public void testSelectPage() {
		SongInfoMapper mapper = session.getMapper(SongInfoMapper.class);
		Page<SongInfo> pageResult = mapper.selectPage(new Page<>(1, 10), new QueryWrapper<>());
		System.out.println(pageResult.getTotal());
		System.out.println(pageResult.getPages());
		System.out.println(pageResult.getRecords());
	}

	@Test
	public void testSong() throws Exception {
		File file = new File("/Users/lww/111/刀郎 - 2002年的第一场雪.mp3");
		SongInfoVO songInfo = SongUtil.getSongInfo(file);
		System.out.println("songInfo = " + JSONObject.toJSONString(songInfo));
	}

	@Test
	public void testShowLyric() throws Exception {

		String lyric = "[00:00.00]作曲 : 2002年的第一场雪\\n[00:27.82]2002年的第一场雪\\n[00:34.13]比以往时候来的更晚一些\\n[00:38.22]停靠在八楼的二路汽车\\n[00:43.83]带走了最后一片飘落的黄叶\\n[00:48.73]2002年的第一场雪\\n[00:53.42]是留在乌鲁木齐难舍的情结\\n[00:58.50]你像一只飞来飞去的蝴蝶\\n[01:03.08]在白雪飘飞的季节里摇曳\\n[01:08.01]忘不了把你搂在怀里的感觉\\n[01:12.66]比藏在心中那份火热更暖一些\\n[01:17.47]忘记了窗外的北风凛冽\\n[01:21.89]再一次把温柔和缠绵重叠\\n[01:29.89]\\n[01:54.34]是你的红唇粘住我的一切\\n[02:00.80]是你的体贴让我再次热烈\\n[02:04.81]是你的万种柔情融化冰雪\\n[02:09.61]是你的甜言蜜语改变季节\\n[02:14.03]2002年的第一场雪\\n[02:19.28]比以往时候来的更晚一些\\n[02:23.60]停靠在八楼的二路汽车\\n[02:29.60]带走了最后一片飘落的黄叶\\n[02:34.04]2002年的第一场雪\\n[02:38.79]是留在乌鲁木齐难舍的情结\\n[02:43.47]你像一只飞来飞去的蝴蝶\\n[02:48.19]在白雪飘飞的季节里摇曳\\n[03:21.02]是你的红唇粘住我的一切\\n[03:26.09]是你的体贴让我再次热烈\\n[03:30.66]是你的万种柔情融化冰雪\\n[03:35.56]是你的甜言蜜语改变季节\\n[03:42.60]END";

		//String[] split = lyric.split("\\\\n");
		//LyricUtil.getLyric(lyric);
		//LyricUtil.SONG_LYRIC.forEach(e -> System.out.println(e));

		String lyric1 = "[02:23]停靠在八楼的二路汽车";
		Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})]|\\[(\\d{2}):(\\d{2})]");
		Matcher matcher = pattern.matcher(lyric1);
		//while (matcher.find()) {
		//	System.out.println(matcher.group(1));
		//	System.out.println(matcher.group(2));
		//	System.out.println(matcher.group(3));
		//	System.out.println(matcher.group(4));
		//	System.out.println(matcher.group(5));
		//}
		LyricVO lyricVO = LyricUtil.analyseLine(lyric1);
		System.out.println(JSONObject.toJSONString(lyricVO));
	}
}