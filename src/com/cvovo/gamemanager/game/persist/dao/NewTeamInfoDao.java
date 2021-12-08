package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cvovo.gamemanager.game.persist.entity.NewTeamInfo;

public interface NewTeamInfoDao extends JpaRepository<NewTeamInfo, Long> {
	List<NewTeamInfo> findByDateAndStatusAndType(int date, int status, String type);

	@Modifying
	@Query("update NewTeamInfo t set t.status =1")
	void updateTeamInfo();

	@Modifying
	@Query("delete   from  NewTeamInfo where status =1")
	void deleteTeamInfo();

}
