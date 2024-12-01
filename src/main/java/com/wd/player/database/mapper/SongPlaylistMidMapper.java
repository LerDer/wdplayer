package com.wd.player.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wd.player.database.entity.SongPlaylistMid;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lww
 * @since 2024-07-30
 */
public interface SongPlaylistMidMapper extends BaseMapper<SongPlaylistMid> {

	Integer deleteAll();
}
