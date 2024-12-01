package com.wd.player.database.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 播放器信息(PLAYER_INFO)表实体类
 *
 * @author lww
 * @since 2024-11-17 03:52:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PlayerInfo extends Model<PlayerInfo> {

	private static final long serialVersionUID = -1L;

	private Integer id;

	private Integer lastPlayId;

	private String sortColumn;

	private Integer sortType;

	private Integer playType;

}

