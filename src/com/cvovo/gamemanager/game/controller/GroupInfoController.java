package com.cvovo.gamemanager.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.service.GroupInfoService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class GroupInfoController {
	private static final Logger logger = LoggerFactory.getLogger(GroupInfoController.class);
	@Autowired
	private GroupInfoService groupInfoService;

	@RequestMapping(value = "groupInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getGroupInfoByType(String id) {
		logger.info("=====groupInfo====");
		int type = Integer.parseInt(id);
		return groupInfoService.findGroupInfoByType(type);
	}

	@RequestMapping(value = "groupBuy", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getBuyGroupInfo(String id) {
		logger.info("=====groupBuy====");
		int type = Integer.parseInt(id);
		return groupInfoService.buyGroupInfoByType(type);
	}
}
