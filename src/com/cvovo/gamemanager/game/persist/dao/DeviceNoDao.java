package com.cvovo.gamemanager.game.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.DeviceNo;

public interface DeviceNoDao extends JpaRepository<DeviceNo, Long> {
	DeviceNo findByDevice(String device);
}
