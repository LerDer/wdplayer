package com.wd.player.database.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author lww
 * @since 2024-07-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SongInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Integer id;

	private String songName;

	private String title;

	private String artist;

	private String album;

	private String year;

	private String encodingType;

	private String sampleRate;

	private String format;

	private String channels;

	private String bitRate;

	private String trackLengthString;

	private Integer trackLength;

	private String songSize;

	private String fileName;

	private String albumArtist;

	private String track;

	private String filePath;

	private String createTime;

}
