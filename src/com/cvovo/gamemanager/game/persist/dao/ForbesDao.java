package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.ForbesEntity;

public interface ForbesDao extends JpaRepository<ForbesEntity, Long> {

	List<ForbesEntity> findByTypeId(int typeId, Pageable pageable);

	ForbesEntity findForbesByTypeIdAndPlayerId(int typeId, int playerId);
}
