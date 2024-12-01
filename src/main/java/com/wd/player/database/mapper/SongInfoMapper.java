package com.wd.player.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wd.player.database.entity.SongInfo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lww
 * @since 2024-07-30
 */
public interface SongInfoMapper extends BaseMapper<SongInfo> {

	List<Integer> selectAllSongIds();

	List<String> selectAllName();

	List<String> selectAllLikeSongName(@Param("songIds") List<Integer> songIds);

	Integer deleteAll();

	List<String> selectAllNameBy(@Param("columnName") String columnName, @Param("orderType") Integer orderType);

	List<Integer> selectAllSongIdsSort(@Param("columnName") String columnName, @Param("orderType") Integer orderType);
}
