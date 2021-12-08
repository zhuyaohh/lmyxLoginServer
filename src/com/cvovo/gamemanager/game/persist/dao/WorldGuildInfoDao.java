package com.cvovo.gamemanager.game.persist.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cvovo.gamemanager.game.persist.entity.WorldGuildInfo;

public interface WorldGuildInfoDao extends JpaRepository<WorldGuildInfo, Long> {

	WorldGuildInfo findByServerIdAndGuildId(int serverId, int guildId);

	//
	// @Query(" from WorldGuildInfo r where status=1 and serverId=?1")
	// List<WorldGuildInfo> getAllWorldInfo(int serverId, Pageable pageable);

	List<WorldGuildInfo> findByServerIdAndStatus(int serverId, Pageable pageable);

	@Query(" from WorldGuildInfo r where r.matchTimer>?1 and r.matchTimer<?2  and r.type=?3 and status=1")
	List<WorldGuildInfo> getAllMatchWorldInfo(Timestamp startTimer, Timestamp endTimer, int type, Pageable pageable);

	@Query(" from WorldGuildInfo r")
	List<WorldGuildInfo> getTopAllWorldInfo(Pageable pageable);

}
