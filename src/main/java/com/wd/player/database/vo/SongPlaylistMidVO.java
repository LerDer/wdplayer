package com.wd.player.database.vo;

import java.io.Serializable;
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
public class SongPlaylistMidVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer playlistId;

	private Integer songId;

}
