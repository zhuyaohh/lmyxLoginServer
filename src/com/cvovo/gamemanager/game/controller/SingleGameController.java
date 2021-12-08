package com.cvovo.gamemanager.game.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.TeamInfo;
import com.cvovo.gamemanager.game.service.RandomRankConfigService;
import com.cvovo.gamemanager.game.service.TeamService;
import com.cvovo.gamemanager.util.RestResult;

/***
 * 淘汰赛
 * 
 * @author UnKnow
 *
 */
@RestController
@RequestMapping("/")
public class SingleGameController {
	private static final Logger logger = LoggerFactory.getLogger(SingleGameController.class);
	@Autowired
	private TeamService teamService;
	@Autowired
	private RandomRankConfigService randomRankConfigService;

	@RequestMapping(value = "getSingleGameInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getSingleGameInfo() {
		logger.info("getSingleGameInfo=====getSingleGameInfo=====getSingleGameInfo==============" + "LOGIN");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			int timer = teamService.addDate();
			List<TeamInfo> tempList = null;
			if (timer == 0) {
				tempList = teamService.getTeamData(1);
				return teamService.getTeamData(tempList);
			} else {
				if (teamService.timePK()) {
					timer = timer + 1;
				}
			}

			tempList = teamService.getTeamData(timer);
			return teamService.getTeamData(tempList);
		}
		return RestResult.result(1);

	}

	@RequestMapping(value = "gameBet", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult dotoMyRankInfo(String serverId, String teamId, String win) {
		logger.info("gameBet========================");
		if (randomRankConfigService.getRandomRankConfig() != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			List<TeamInfo> list = teamService.getTeamData(teamService.addDate() + 1);

			int index = Integer.parseInt(teamId);
			if (index <= list.size()) {
				TeamInfo info = list.get(index);

				if (Boolean.parseBoolean(win)) {
					info.addBetWinCount();
				} else {
					info.addBetFailCount();
				}
				return teamService.save(info);
			}

		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "singleGameBattle", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult battleResult(String serverId, String teamId, String data) {
		logger.info("singleGameBattle========================");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			List<TeamInfo> list = teamService.getTeamData(teamService.addDate() + 1);
			int index = Integer.parseInt(teamId);
			if (index <= list.size()) {
				TeamInfo info = list.get(index);
				info.setBattleResult(data);
				return teamService.save(info);
			}

		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "stratSingleGame", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult battleResult(String appId) {
		logger.info("stratSingleGame========================");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			// if (appId != null && appId.equals("1010")) {
			teamService.newTeams();
			// return RestResult.result(0);
			// } else {
			// teamService.updateTeam();
			// return RestResult.result(0);
			// }
		}
		return RestResult.result(1);
	}

}
