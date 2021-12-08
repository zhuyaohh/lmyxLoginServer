package com.cvovo.gamemanager.game.persist.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.IosOrderInfo;

public interface IosOrderDao extends JpaRepository<IosOrderInfo, Long> {

	public IosOrderInfo getIosOrderInfoByMd5Receipt(String md5Receipt);

}
