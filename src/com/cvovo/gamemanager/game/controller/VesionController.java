package com.cvovo.gamemanager.game.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class VesionController {
	private static final Logger logger = LoggerFactory.getLogger(VesionController.class);
	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "start", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getRegister(String version) {
		return RestResult.versionSuccess();
	}

	@RequestMapping(value = "radomServerInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getRandomSeverInfo(String serverId) {
		int id = Integer.parseInt(serverId);
		ServerInfo serverInfo = serverService.getRadomInfo(id);
		if (serverInfo != null) {
			String[] strList = serverInfo.getUrl().split(":");
			String serverUrl = strList[0];
			StringBuffer sb = new StringBuffer("http://");
			sb.append(serverUrl);
			sb.append(":");
			sb.append(serverInfo.getPort());
			StringBuffer sb2 = new StringBuffer("http://");
			sb2.append(serverInfo.getInnerUrl());
			sb2.append(":");
			sb2.append(serverInfo.getPort());
			return RestResult.versionSuccess(sb.toString(), sb2.toString(), serverInfo.getId().toString());
		}
		return RestResult.versionSuccess(null, null, null);
	}

	@RequestMapping(value = "getServerInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getSeverInfo(String serverId) {
		logger.info("===========getServerInfo================");
		int id = Integer.parseInt(serverId);
		ServerInfo serverInfo = serverService.getServerInfo(id);
		if (serverInfo != null) {
			String[] strList = serverInfo.getUrl().split(":");
			String serverUrl = strList[0];
			StringBuffer sb = new StringBuffer("http://");
			sb.append(serverUrl);
			sb.append(":");
			sb.append(serverInfo.getPort());
			StringBuffer sb2 = new StringBuffer("http://");
			sb2.append(serverInfo.getInnerUrl());
			sb2.append(":");
			sb2.append(serverInfo.getPort());
			return RestResult.versionSuccess(sb.toString(), sb2.toString(), serverInfo.getId().toString());
		}
		return RestResult.versionSuccess(null, null, null);
	}

}
