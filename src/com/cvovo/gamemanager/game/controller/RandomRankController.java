package com.cvovo.gamemanager.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.RandomRank;
import com.cvovo.gamemanager.game.service.RandomRankConfigService;
import com.cvovo.gamemanager.game.service.RandomRankService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class RandomRankController {
	private static final Logger logger = LoggerFactory.getLogger(RandomRankController.class);
	@Autowired
	private RandomRankService randomRankService;

	@Autowired
	private RandomRankConfigService randomRankConfigService;

	@RequestMapping(value = "randomRank", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getRandomRank(String serverId, String playerId, String rank) {
		logger.info("randomRank=====");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			RestResult rrt = randomRankService.getRandomRankBy(Integer.parseInt(rank), Integer.parseInt(playerId), Integer.parseInt(serverId));
			return rrt;
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "myRankInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult dotoMyRankInfo(String serverId, String playerId, String rank) {
		logger.info("myRankInfo=====");
		if (serverId == null || serverId.equals("") || playerId == null || playerId.equals("")) {
			return null;
		}
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			RandomRank rr = randomRankService.findByServerIdAndPlayerId(Integer.parseInt(serverId), Integer.parseInt(playerId));
			if (rr != null) {
				rr.setRank(Integer.parseInt(rank));
				randomRankService.save(rr);
				return RestResult.result(0);
			} else {
				rr = new RandomRank();
				rr.setRank(Integer.parseInt(rank));
				rr.setPlayerId(Integer.parseInt(playerId));
				rr.setServerId(Integer.parseInt(serverId));
				randomRankService.save(rr);
				return RestResult.result(0);
			}
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "getRankInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getTop100RankInfo() {
		return randomRankService.getTop100ByRank();

	}

}
