package com.cvovo.gamemanager.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.NewRandomRank;
import com.cvovo.gamemanager.game.service.NewRandomRankService;
import com.cvovo.gamemanager.game.service.RandomRankConfigService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class NewRandomRankController {
	private static final Logger logger = LoggerFactory.getLogger(NewRandomRankController.class);
	@Autowired
	private NewRandomRankService newRandomRankService;

	@Autowired
	private RandomRankConfigService randomRankConfigService;

	@RequestMapping(value = "worldArenaRank", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getNewRandomRank(String serverId, String playerId, String rank, String type) {
		logger.info("getNewRandomRank=====");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			RestResult rrt = newRandomRankService.getRandomRankBy(type, Integer.parseInt(rank), Integer.parseInt(playerId), Integer.parseInt(serverId));
			return rrt;
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "worldArenaSendRankInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult dotoNewMyRankInfo(String type, String serverId, String playerId, String rank) {
		logger.info("worldArenaSendRankInfo=====");
		if (serverId == null || serverId.equals("") || playerId == null || playerId.equals("")) {
			return null;
		}
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			NewRandomRank rr = newRandomRankService.findByServerIdAndPlayerId(type, Integer.parseInt(serverId), Integer.parseInt(playerId));
			if (rr != null) {
				rr.setRank(Integer.parseInt(rank));
				newRandomRankService.save(rr);
				return RestResult.result(0);
			} else {
				rr = new NewRandomRank();
				rr.setRank(Integer.parseInt(rank));
				rr.setPlayerId(Integer.parseInt(playerId));
				rr.setServerId(Integer.parseInt(serverId));
				rr.setType(type);
				newRandomRankService.save(rr);
				return RestResult.result(0);
			}
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "worldArenaRankTop", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getNewTop100RankInfo(String type) {
		return newRandomRankService.getTop100ByRank(type);

	}
}
