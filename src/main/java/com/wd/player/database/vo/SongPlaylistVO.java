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
public class SongPlaylistVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;

	private String playlistName;

}
