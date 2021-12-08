package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.ServerInfo;

public interface ServerInfoDao extends JpaRepository<ServerInfo, Long> {

	List<ServerInfo> findByState(int state);

	List<ServerInfo> findAll();

	List<ServerInfo> findBySign(int sign);

	ServerInfo findById(int id);

}


