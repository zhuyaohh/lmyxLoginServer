package com.cvovo.gamemanager.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.ForbesEntity;
import com.cvovo.gamemanager.game.service.ForbesService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class ForbesController {
	private static final Logger logger = LoggerFactory.getLogger(ForbesController.class);
	@Autowired
	public ForbesService forbesService;

	@RequestMapping(value = "saveForbesMessage", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getForbesMessage(String type, String serverId, String playerId, String name, String count) {
		ForbesEntity ef = forbesService.findForbesEntity(Integer.parseInt(type), Integer.parseInt(playerId));
		if (ef == null) {
			ef = new ForbesEntity();
			ef.setCount(Integer.parseInt(count));
			ef.setPlayerId(Integer.parseInt(playerId));
			ef.setPlayerName(name);
			ef.setServerId(serverId);
			ef.setTypeId(Integer.parseInt(type));
		} else {
			ef.setCount(Integer.parseInt(count));
			ef.setPlayerName(name);
		}
		return forbesService.saveForbesEntity(ef);
	}

	@RequestMapping(value = "rankByType", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getForbesListByType(String type) {
		return forbesService.get100Rank(Integer.parseInt(type));
	}

}
