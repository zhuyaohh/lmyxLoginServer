package com.cvovo.gamemanager.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.service.ServerTypeService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class LoginStrart {
	@Autowired
	private ServerTypeService serverTypeService;

	@RequestMapping(value = "version", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getServerList(int type) {
		return serverTypeService.getServerMessageList(type);
	}

	@RequestMapping(value = "nextVesion", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getServerList(int type, String version) {
		return serverTypeService.getServerMessageList(type, version);
	}
}
