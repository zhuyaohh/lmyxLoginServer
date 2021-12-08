package com.cvovo.gamemanager.game.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.CheckVersion;

public interface CheckVersionDao extends JpaRepository<CheckVersion, Long> {
	CheckVersion findByCheckStr(String check);
}
