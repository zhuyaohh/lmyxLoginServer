package com.cvovo.gamemanager.game.controller;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.WorldGuildInfo;
import com.cvovo.gamemanager.game.service.WorldGuildInfoService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class WorldguildMatchController {
	private static final Logger logger = LoggerFactory.getLogger(WorldguildMatchController.class);
	@Autowired
	private WorldGuildInfoService wgis;

	@RequestMapping(value = "worldguildMatch", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult stratMaching(String serverId, String guildId, String guildName, String type, String rank, HttpServletResponse response, HttpServletRequest request) {
		logger.info("============worldguildMatch========");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		logger.info("============guildName========" + guildName);
		WorldGuildInfo wgj = wgis.findWorldGuildInfoByServerIdAndGuildId(Integer.parseInt(serverId), Integer.parseInt(guildId));
		if (wgj != null) {
			wgj.setType(Integer.parseInt(type));
			wgj.setValue(Integer.parseInt(rank));
		} else {
			wgj = new WorldGuildInfo();
			wgj.setGuildId(Integer.parseInt(guildId));
			wgj.setName(guildName);
			wgj.setServerId(Integer.parseInt(serverId));
			wgj.setType(Integer.parseInt(type));
			wgj.setValue(Integer.parseInt(rank));
			wgj.setValueTimer(new Timestamp(System.currentTimeMillis()));
		}
		wgj.setMatchTimer(new Timestamp(System.currentTimeMillis()));

		wgj.setStatus(1);
		wgis.save(wgj);
		return RestResult.result(0);
	}

	@RequestMapping(value = "worldguildMatchResult", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getMachingResult(String serverId) {
		logger.info("============worldguildMatchResult========");
		return wgis.worldGuildMatching(Integer.parseInt(serverId));
	}

	@RequestMapping(value = "worldguildBattleResult", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult guildBattleResult(String serverId, String guildId, String rank) {
		logger.info("============worldguildMatch========");
		// WorldGuildInfo wgj = wgis.findWorldGuildInfoByServerIdAndGuildId(Integer.parseInt(serverId), Integer.parseInt(guildId));
		// WorldGuildInfo wgj = wgis.findWorldGuildInfoByServerIdAndGuildId(Integer.parseInt(serverId), Integer.parseInt(guildId));
		WorldGuildInfo wgj = wgis.findWorldGuildInfoByServerIdAndGuildId(Integer.parseInt(serverId), Integer.parseInt(guildId));
		if (wgj != null && wgj.getStatus() == 1) {
			wgj.setValue(Integer.parseInt(rank));
			wgj.setValueTimer(new Timestamp(System.currentTimeMillis()));
			wgj.setStatus(0);
			wgis.save(wgj);
			return RestResult.result(0);

		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "worldguildTop", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult worldGuildTopList() {
		return wgis.getTopList();
	}

	@RequestMapping(value = "worldGuildfluch", method = { RequestMethod.GET, RequestMethod.POST })
	public void worldGuildfluch(String serverId) {
		logger.info("============worldguildMatch========");
		wgis.worldGuildMatchingFlush(Integer.parseInt(serverId));
	}
}
