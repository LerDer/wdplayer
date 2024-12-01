package com.wd.player.observer.message;

import com.wd.player.database.service.SongInfoService;
import com.wd.player.database.vo.SongInfoVO;
import com.wd.player.observer.enums.FileTypeEnum;
import com.wd.player.observer.enums.PlayMessageEnum;
import com.wd.player.observer.enums.PlayTypeEnum;
import lombok.Data;

/**
 * @author lww
 * @date 2024-10-14 00:47
 */
@Data
public class MusicEvent {

	private PlayMessageEnum playMessage;

	private Integer songId;

	private String filePath;

	private String fileUrl;

	private String fileName;

	private FileTypeEnum fileType;

	private Long position;

	private PlayTypeEnum playType;

	public MusicEvent(PlayMessageEnum playMessage, Integer songId) {
		this(playMessage, songId, FileTypeEnum.local, PlayTypeEnum.ALL_LOOP);
	}

	public MusicEvent(PlayMessageEnum playMessage, Integer songId, FileTypeEnum fileType, PlayTypeEnum playType) {
		this.playMessage = playMessage;
		this.songId = songId;
		this.fileType = fileType;
		this.playType = playType;
		SongInfoVO infoVO = SongInfoService.getSongInfoVO(songId);
		this.filePath = infoVO.getFilePath();
		//this.fileUrl = infoVO.getFileUrl();
		this.fileName = infoVO.getFileName();
		this.position = 0L;
	}
}
