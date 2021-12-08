package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cvovo.gamemanager.game.persist.entity.RandomRank;

public interface RandomRankDao extends JpaRepository<RandomRank, Long> {
	@Query("from RandomRank r where r.rank>=?1 and r.rank<?2 and status=0")
	List<RandomRank> getTop20ByNearbyRank(int lastRank, int topRank, Pageable pageable);

	@Query(" from RandomRank r where status=0")
	List<RandomRank> getTop100ByRank(Pageable pageable);

	RandomRank findByPlayerId(int playerId);

	RandomRank findByServerId(int serverId);

	RandomRank findByServerIdAndPlayerId(int serverId, int playerId);

	@Modifying
	@Query("update RandomRank r  set  r.status =1")
	void upRandomRank();
}
