package com.cvovo.gamemanager.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.AccountDao;
import com.cvovo.gamemanager.game.persist.dao.DeviceNoDao;
import com.cvovo.gamemanager.game.persist.entity.Account;
import com.cvovo.gamemanager.game.persist.entity.DeviceNo;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class DeviceNoService {
	@Autowired
	private DeviceNoDao deviceNoDao;

	@Autowired
	private AccountDao accountDao;

	public boolean checkDevice(String deviceNo) {
		DeviceNo no = deviceNoDao.findByDevice(deviceNo);
		if (no != null) {
			return true;
		}
		return false;
	}

	public void save(String deviceNo) {

		if (!this.checkDevice(deviceNo)) {
			this.deviceNoDao.save(new DeviceNo(deviceNo));
		}
	}

	public boolean delete(String deviceNo) {
		DeviceNo no = deviceNoDao.findByDevice(deviceNo);
		if (no != null) {
			deviceNoDao.delete(no);
			return true;
		}
		return false;
	}

	public RestResult protectedDeviceByDeviceNo(String deveiceNo) {
		this.save(deveiceNo);
		return RestResult.result(0);

	}

	public RestResult protectedDeviceByAccountId(String accountId) {
		int userId = Integer.parseInt(accountId);
		Account account = accountDao.findByUserId(userId);
		if (account != null && account.getDevice() != null && !account.getDevice().equals("")) {
			return this.protectedDeviceByDeviceNo(account.getDevice());
		}
		return RestResult.result(1);
	}

	public RestResult openDeviceByDeviceNo(String deveiceNo) {
		if (this.delete(deveiceNo)) {
			return RestResult.result(0);
		}
		return RestResult.result(1);
	}

	public RestResult openDeviceByAccountId(String accountId) {
		int userId = Integer.parseInt(accountId);
		Account account = accountDao.findByUserId(userId);
		if (account != null && account.getDevice() != null && !account.getDevice().equals("")) {
			if (this.delete(account.getDevice())) {
				return RestResult.result(0);
			}
		}
		return RestResult.result(1);
	}
}
