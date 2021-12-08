package com.cvovo.gamemanager.game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.CheckVersionDao;
import com.cvovo.gamemanager.game.persist.entity.CheckVersion;

@Service
@Transactional
public class CheckService {
	private static final Logger logger = LoggerFactory.getLogger(CheckService.class);
	@Autowired
	private CheckVersionDao checkVersionDao;

	public void saveCheck(CheckVersion check) {
		checkVersionDao.save(check);
	}

	public CheckVersion findCheckByStr(String check) {
		return checkVersionDao.findByCheckStr(check);
	}

	public void deleteCheck(String check) {
		CheckVersion t = checkVersionDao.findByCheckStr(check);
		if (t != null) {
			checkVersionDao.delete(t);
		}

	}
}
