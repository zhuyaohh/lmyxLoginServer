package com.cvovo.gamemanager.game.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.RandomRankConfig;
import com.cvovo.gamemanager.game.service.DateQuest;
import com.cvovo.gamemanager.game.service.RandomRankConfigService;
import com.cvovo.gamemanager.game.service.RandomRankService;
import com.cvovo.gamemanager.game.service.TeamService;

@RestController
@RequestMapping("/")
public class RankConfigController {
	private static final Logger logger = LoggerFactory.getLogger(RankConfigController.class);
	@Autowired
	private RandomRankConfigService randomRankConfigService;
	@Autowired
	private DateQuest da;
	@Autowired
	private RandomRankService randomRankService;

	@Autowired
	private TeamService teamService;

	@RequestMapping(value = "startRandomRank", method = { RequestMethod.GET, RequestMethod.POST })
	public String dotoMyRankInfo(String day) {
		logger.info("startRandomRank=====================");
		RandomRankConfig oldRrc = randomRankConfigService.getRandomRankConfig();
		Calendar ca = Calendar.getInstance();
		ca.roll(Calendar.DAY_OF_YEAR, true);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.format(ca.getTime());

		if (oldRrc != null) {
			if (oldRrc.getLastLoginTime().equals(sdf.format(ca.getTime()))) {
				teamService.updateAllStatus();
				randomRankService.updatAllStatus();
			}
			oldRrc.setIntervalDay(7);
			oldRrc.setLastLoginTime(sdf.format(ca.getTime()));
			oldRrc.setStatus(1);
		} else {
			oldRrc = new RandomRankConfig();
			oldRrc.setId((long) 1);
			oldRrc.setStatus(1);
			oldRrc.setLastLoginTime(sdf.format(ca.getTime()));
			oldRrc.setIntervalDay(7);

		}
		randomRankConfigService.saveRandomRankConfig(oldRrc);
		da.timer();
		return "OK";
	}

	@RequestMapping(value = "stopRandomRank", method = { RequestMethod.GET, RequestMethod.POST })
	public String dotoMyRankInfo() {
		da.stopTast();
		return "OK";
	}
}
