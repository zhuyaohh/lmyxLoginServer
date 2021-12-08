package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cvovo.gamemanager.game.persist.entity.TeamInfo;

public interface TeamInfoDao extends JpaRepository<TeamInfo, Long> {

	List<TeamInfo> findByDateAndStatus(int date, int status);

	@Modifying
	@Query("update TeamInfo t set t.status =1")
	void updateTeamInfo();

}
