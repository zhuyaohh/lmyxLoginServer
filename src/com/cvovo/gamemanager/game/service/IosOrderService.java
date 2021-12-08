package com.cvovo.gamemanager.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.IosOrderDao;
import com.cvovo.gamemanager.game.persist.entity.IosOrderInfo;

@Service
@Transactional
public class IosOrderService {

	@Autowired
	private IosOrderDao iosOrderDao;

	public void save(IosOrderInfo iosOrderInfo) {
		iosOrderDao.save(iosOrderInfo);
	}

	public boolean findByIosOrderByMD5(String md5Receipt) {
		IosOrderInfo ioi = iosOrderDao.getIosOrderInfoByMd5Receipt(md5Receipt);
		if (ioi != null && ioi.getStatus().equals("3")) {
			return false;
		}
		return true;
	}

}
