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
public class GameInterfaceController {

	private static final Logger logger = LoggerFactory.getLogger(GameInterfaceController.class);
	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "onlineNum", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult gameInterface(String serverId) {
		/***
		 * c=2002&command=onlineNum return json数据 online=10
		 */
		String online = doGetOnlineNum(serverId);
		if (online != null && !online.equals("-1")) {
			return RestResult.getOnlineNum(online);
		}
		return RestResult.getOnlineNum("0");
	}

	public String doGetOnlineNum(String serverId) {
		logger.info("======doGetOnlineNum================");
		String online = "-1";
		ServerInfo serverInfo = serverService.getServerInfo(Integer.parseInt(serverId));
		if (serverInfo != null) {
			logger.info("成功获取服务器地址==============================");
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=2002&command=onlineNum");
			String[] strList = url.split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			logger.info("发送HTTP请求=========" + args.toString());
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			logger.info("拿到HTTP请求结果============" + result);
			org.json.JSONObject jsonObject;
			try {
				jsonObject = new org.json.JSONObject(result);
				if (jsonObject.has("online")) {
					online = jsonObject.get("online").toString();
					logger.info("===errorId======" + online);
					return online;

				}
			} catch (Exception ex) {
				return online;
			}
		}
		return online;

	}

}
