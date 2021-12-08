package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.Account;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.AccountService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;

@RestController
@RequestMapping("/")
public class YinNiGetRoleController {
	private static final Logger logger = LoggerFactory.getLogger(YinNiGetRoleController.class);
	@Autowired
	private ServerService serverService;
	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "getYinNiRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getYinNiRoleInfo(String serverCode, String passport, String t, String ck, HttpServletResponse response) throws IOException {
		getRoleByQuest(serverCode, passport, t, ck, response, "%YSK!&^E0SagVhH&&UNiLHxq*");
	}

	@RequestMapping(value = "getYinNiYRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getYinNiYRoleInfo(String serverCode, String passport, String t, String ck, HttpServletResponse response) throws IOException {
		getRoleByQuest(serverCode, passport, t, ck, response, "Uepbfh74pe0VdZmJgBk1");
	}

	@RequestMapping(value = "getLunplayerGATRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getLunplayerGATRoleInfo(String serverCode, String passport, String t, String ck, HttpServletResponse response) throws IOException {
		getRoleByQuest(serverCode, passport, t, ck, response, "d#r%RVqiDJSyc7Tp&d9u");
	}

	public void getRoleByQuest(String serverCode, String passport, String t, String ck, HttpServletResponse response, String key) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append(passport);
		sb.append(t);
		sb.append(key);
		sb.append(serverCode);
		String ckCheck = MD5.GetMD5Code(sb.toString());
		if (ckCheck.equals(ck)) {
			Account account = accountService.findByUin(passport);
			if (account != null) {
				int id = Integer.parseInt(serverCode);
				ServerInfo serverInfo = serverService.getServerInfo(id);
				String[] strList = serverInfo.getUrl().split(":");
				StringBuffer title = new StringBuffer("http://");
				StringBuffer ip = new StringBuffer(strList[0]);
				title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
				StringBuffer args = new StringBuffer("c=1010");
				args.append("&platAccount=" + account.getUin());
				args.append("&serverId=" + id);
				String result = HttpRequest.sendGet(title.toString(), args.toString());
				String resultT = result.replaceAll("name", "playerName");
				String resultTA = resultT.replaceAll("roleId", "roleid");
				String resultTB = resultTA.replaceAll("level", "playerLevel");
				response.setHeader("content-type", "text/plain; charset=UTF-8");
				PrintWriter writer = response.getWriter();
				writer.append(resultTB);
				writer.close();
			} else {
				PrintWriter writer = response.getWriter();
				writer.append("errorCode:1");
				writer.close();
			}
		}
	}

	public static void main(String[] args1) {
		int id = 1001;
		String[] strList = new String[] { "203.74.101.141", "55000" };
		StringBuffer title = new StringBuffer("http://");
		StringBuffer ip = new StringBuffer(strList[0]);
		title.append(ip).append(":").append(String.valueOf(55000));
		StringBuffer args = new StringBuffer("c=1010");
		args.append("&platAccount=9000137041939462675");
		args.append("&serverId=" + id);
		String result = HttpRequest.sendGet(title.toString(), args.toString());
		String resultT = result.replaceAll("name", "playerName");
		String resultTA = resultT.replaceAll("roleId", "roleid");
		String resultTB = resultTA.replaceAll("level", "playerLevel");

		System.out.println(resultTB);

	}
}
