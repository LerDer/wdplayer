package com.wd.player.database.vo;

import com.wd.player.database.entity.SongInfo;
import com.wd.player.util.DateUtil;
import java.io.Serializable;
import java.util.Date;
import javax.swing.ImageIcon;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author lww
 * @since 2024-07-30
 */
@Data
public class SongInfoVO implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 作者
	 */
	private String artist;
	/**
	 * 专辑名
	 */
	private String album;
	/**
	 * 封面
	 */
	private ImageIcon cover;
	/**
	 * 年份
	 */
	private String year;
	/**
	 * 文件类型
	 */
	private String encodingType;
	/**
	 * 采样率 /1000 kHZ
	 */
	private String sampleRate;
	/**
	 * 制作格式或制作技术
	 */
	private String format;
	/**
	 * 声道
	 */
	private String channels;
	/**
	 * 比特率 Kbps
	 */
	private String bitRate;
	/**
	 * 时长
	 */
	private String trackLengthAsString;
	/**
	 * 时长
	 */
	private int trackLength;
	/**
	 * 大小
	 */
	private String songSize;
	/**
	 * 文件名称
	 */
	private String fileName;
	/**
	 * 专辑艺术家
	 */
	private String albumArtist;
	/**
	 * 音轨号
	 */
	private String track;
	/**
	 * 语言
	 */
	private String language;

	/**
	 * 位深 bit
	 */
	private String profundity;

	//当前播放时长
	private long playTrackLength;
	//文件路径
	private String filePath;
	//歌词
	private String lyric;

	public static SongInfo convert(SongInfoVO vo) {
		SongInfo songInfo = new SongInfo();
		//songInfo.setId();
		songInfo.setSongName(vo.getFileName());
		songInfo.setTitle(vo.getTitle());
		songInfo.setArtist(vo.getArtist());
		songInfo.setAlbum(vo.getAlbum());
		songInfo.setYear(vo.getYear());
		songInfo.setEncodingType(vo.getEncodingType());
		songInfo.setSampleRate(vo.getSampleRate());
		songInfo.setFormat(vo.getFormat());
		songInfo.setChannels(vo.getChannels());
		songInfo.setBitRate(vo.getBitRate());
		songInfo.setTrackLengthString(vo.getTrackLengthAsString());
		songInfo.setTrackLength(vo.getTrackLength());
		songInfo.setSongSize(vo.getSongSize());
		songInfo.setFileName(vo.getFileName());
		songInfo.setAlbumArtist(vo.getAlbumArtist());
		songInfo.setTrack(vo.getTrack());
		songInfo.setFilePath(vo.getFilePath());
		songInfo.setCreateTime(DateUtil.currentMillis());
		return songInfo;
	}
}
