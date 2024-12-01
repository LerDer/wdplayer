package com.wd.player.database.entity;

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
public class SongPlaylistMid implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer playlistId;

	private Integer songId;

	private String createTime;

}
