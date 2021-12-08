package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cvovo.gamemanager.game.persist.entity.ServerType;

public interface ServerTypeDao extends JpaRepository<ServerType, Long> {
	   List<ServerType> findByChannel(int channel);
}
