package com.cvovo.gamemanager.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class UtilController {

	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "updateServerInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult updateServerInfoList(String id, String name, String url, String innerUrl, String state, String port) {
		ServerInfo info = new ServerInfo();
		info.setId(Long.parseLong(id));
		info.setName(name);
		info.setUrl(url);
		info.setInnerUrl(innerUrl);
		info.setState(Integer.parseInt(state));
		info.setPort(Integer.parseInt(port));
		return serverService.saveServerInfo(info);
	}

	@RequestMapping(value = "getAllServerInfoList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getServerInfoList() {
		return serverService.getAllServerList();

	}

	@RequestMapping(value = "deleteServerInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult changeServerInfoStatus(int serverId) {
		return serverService.updateServerInfo(serverId);

	}
}
