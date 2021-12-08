package com.cvovo.gamemanager.game.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.NewTeamInfo;
import com.cvovo.gamemanager.game.persist.entity.TeamInfoTemp;
import com.cvovo.gamemanager.game.service.NewTeamService;
import com.cvovo.gamemanager.game.service.RandomRankConfigService;
import com.cvovo.gamemanager.util.RestResult;

/***
 * 淘汰赛
 * 
 * @author UnKnow
 *
 */
@RestController
@RequestMapping("/")
public class NewSingleGameController {
	private static final Logger logger = LoggerFactory.getLogger(NewSingleGameController.class);
	@Autowired
	private NewTeamService teamService;
	@Autowired
	private RandomRankConfigService randomRankConfigService;

	@RequestMapping(value = "worldArenaSingleGameInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getNewSingleGameInfo(String type) {
		logger.info("getSingleGameInfo=====getSingleGameInfo=====getSingleGameInfo======" + type);
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			logger.info("into getData");
			int timer = teamService.addDate();
			logger.info("==timer===" + timer);
			List<NewTeamInfo> tempList = null;
			if (timer == 0) {
				tempList = teamService.getTeamData(1, type);
				logger.info("==tempList===" + tempList.size());
				List<TeamInfoTemp> list = teamService.getNewTeamData(tempList);
				return RestResult.successTeamInfoTemp(list);
			} else {
				if (teamService.timePK()) {
					timer = timer + 1;
				}
				logger.info("==timer===" + timer + "==========type==" + type);
				tempList = teamService.getTeamData(timer, type);
				logger.info("==tempList===" + tempList.size());
				List<TeamInfoTemp> list = teamService.getNewTeamData(tempList);
				return RestResult.successTeamInfoTemp(list);
			}
		}
		return RestResult.result(1);

	}

	@RequestMapping(value = "worldArenaGameBet", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult dotoNewMyRankInfo(String type, String teamId, String win) {
		logger.info("gameBet========================");
		if (randomRankConfigService.getRandomRankConfig() != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			List<NewTeamInfo> list = teamService.getTeamData(teamService.addDate() + 1, type);

			int index = Integer.parseInt(teamId);
			if (index <= list.size()) {
				NewTeamInfo info = list.get(index);

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

	@RequestMapping(value = "worldArenaSendBattleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult toNewBattleResult(String serverId, String type, String teamId, String data) {
		logger.info("singleGameBattle========================");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {

			List<NewTeamInfo> list = teamService.getTeamData(teamService.addDate() + 1, type);
			int index = Integer.parseInt(teamId);
			if (index <= list.size()) {
				NewTeamInfo info = list.get(index);
				info.setBattleResult(data);
				return teamService.save(info);
			}

		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "newStartSingleGame", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult battleResult(String appId, String type) {
		logger.info("stratSingleGame========================");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			teamService.newTeams(type);
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "testSingleGame", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult battleResult(String test) {
		logger.info("stratSingleGame========================");
		if (randomRankConfigService != null && randomRankConfigService.getRandomRankConfig().getStatus() == 1) {
			// if (appId != null && appId.equals("1010")) {
			//
			// return RestResult.result(0);
			// } else {
			// teamService.updateTeam();
			// return RestResult.result(0);
			// }
			// for (int i = 1; i < 4; i++) {
			teamService.newTeams("1");
			teamService.newTeams("2");
			teamService.newTeams("3");
			// }
		}
		return RestResult.result(1);
	}
}
