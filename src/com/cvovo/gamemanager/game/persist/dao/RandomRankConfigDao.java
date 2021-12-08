package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.RandomRankConfig;

public interface RandomRankConfigDao extends JpaRepository<RandomRankConfig, Long> {
	List<RandomRankConfig> findByStatus(int status);

	List<RandomRankConfig> findAll();
}
