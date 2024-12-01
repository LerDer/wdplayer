package com.wd.player;

import com.wd.player.util.SongUtil;
import com.wd.player.database.vo.SongInfoVO;
import java.io.File;
import org.junit.Test;

/**
 * @author lww
 * @date 2024-07-28 4:48 PM
 */
public class PlayTest {

	@Test
	public void getInfo() throws Exception {
		File mp3file = new File("/Users/lww/Desktop/workspace/wdplayer/src/main/resources/周杰伦 - 最长的电影.mp3");
		//File mp3file = new File("/Users/lww/Desktop/workspace/wdplayer/src/main/resources/林俊杰 - 一千年以后.flac");
		SongInfoVO songInfo = SongUtil.getSongInfo(mp3file);
	}

	@Test
	public void testPlay() throws Exception {
		File file = new File("/Users/lww/Desktop/workspace/wdplayer/src/main/resources/" + "fbodemo1.mp3");
		//SongPlayer.play(file);
	}

}
