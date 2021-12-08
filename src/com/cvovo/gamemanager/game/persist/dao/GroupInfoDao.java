package com.cvovo.gamemanager.game.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cvovo.gamemanager.game.persist.entity.GroupInfo;

public interface GroupInfoDao extends JpaRepository<GroupInfo, Long> {
	GroupInfo findByType(int type);
}
