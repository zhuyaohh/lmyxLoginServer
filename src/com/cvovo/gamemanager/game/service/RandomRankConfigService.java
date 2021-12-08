package com.cvovo.gamemanager.game.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.RandomRankConfigDao;
import com.cvovo.gamemanager.game.persist.entity.RandomRankConfig;

@Service
@Transactional
public class RandomRankConfigService {

	@Autowired
	private RandomRankConfigDao randomRankConfigDao;

	public RandomRankConfig getRandomRankConfig() {
		List<RandomRankConfig> list = randomRankConfigDao.findAll();
		if (list.size() > 0) {
			RandomRankConfig tt = list.get(0);
			return tt;
		}
		return null;
	}

	public void saveRandomRankConfig(RandomRankConfig randomRankConfig) {

		randomRankConfigDao.save(randomRankConfig);
	}

	public void stopRandomRankConfig() {
		RandomRankConfig t = this.getRandomRankConfig();
		if (t != null) {
			t.setStatus(0);
			randomRankConfigDao.save(t);
		}
	}
}
