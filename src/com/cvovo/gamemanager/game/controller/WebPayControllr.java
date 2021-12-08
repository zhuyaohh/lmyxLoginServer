package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.ServerService;

@RestController
@RequestMapping("/")
public class WebPayControllr {
	private static final Logger logger = LoggerFactory.getLogger(WebPayControllr.class);
	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "getRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getRoleByQuest(String serverId, String roleId, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		logger.info("======getRoleInfo==================");
		logger.info("======serverId=========" + serverId);
		logger.info("======roleId===========" + roleId);

		int id = Integer.parseInt(serverId);
		ServerInfo serverInfo = serverService.getServerInfo(id);
		String[] strList = serverInfo.getUrl().split(":");
		StringBuffer title = new StringBuffer("http://");
		StringBuffer ip = new StringBuffer(strList[0]);
		title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
		StringBuffer args = new StringBuffer("c=1010");
		args.append("&roleId=" + roleId);
		args.append("&serverId=" + id);
		String result = HttpRequest.sendGet(title.toString(), args.toString());
		logger.info("======result===========" + result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			org.json.JSONObject jasonObject1 = new org.json.JSONObject();
			if (jsonObject.has("errorCode")) {
				String state = jsonObject.getString("errorCode");
				if (state.equals("0")) {
					String roleName = jsonObject.getString("name");
					String userName = jsonObject.getString("platAccount");
					jasonObject1.put("state", "0");
					jasonObject1.put("userName", userName);
					jasonObject1.put("roleName", roleName);
				} else {
					String msg = jsonObject.getString("errorMessage");
					jasonObject1.put("state", "-99");
					jasonObject1.put("msg", msg);
				}
				PrintWriter writer = response.getWriter();
				writer.append(jasonObject1.toString());
				writer.close();
				return;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter writer = response.getWriter();
		writer.append(result);
		writer.close();
	}
}
