package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cvovo.gamemanager.game.persist.entity.NewRandomRank;

public interface NewRandomRankDao extends JpaRepository<NewRandomRank, Long> {

	@Query("from NewRandomRank r where r.type=?1 and  r.rank>=?2 and r.rank<?3 and status=0")
	List<NewRandomRank> getTop20ByNearbyRank(String type, int lastRank, int topRank, Pageable pageable);

	@Query(" from NewRandomRank r where status=0 and type =?")
	List<NewRandomRank> getTop100ByRank(String type, Pageable pageable);

	NewRandomRank findByPlayerId(int playerId);

	NewRandomRank findByServerId(int serverId);

	@Query(" from NewRandomRank r where type=?1 and serverId =?2 and playerId=?3")
	NewRandomRank findByTypeAndPlayerIdAndType(String type, int serverId, int playerId);

	@Modifying
	@Query("update NewRandomRank r  set  r.status =1")
	void upRandomRank();

}
