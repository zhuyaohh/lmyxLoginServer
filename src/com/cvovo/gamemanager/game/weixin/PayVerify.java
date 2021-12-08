package com.cvovo.gamemanager.game.weixin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cvovo.gamemanager.game.weixin.until.PayCommonUtil;

/**
 * Created by nsm on 2016/6/22.
 */
@WebServlet("/WeiXinPay")
public class PayVerify extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("===========WeiXinPayVerify=============");
		String resultMsg = "success";
		String returnMsg = "SUCCESS";
		// SendMessage sm = new SendMessage();
		// WeiXinOrder order = WeiXinOrder.getWeiXinOrder(req);
		// if (order != null) {
		// sm = WeiXinMessage.getMessage(order, sm);
		// resultMsg = ShopCheckService.check(sm, "WEIXIN");
		// } else {
		// resultMsg = "WeiXinOrder check is error!";
		// }
		System.out.println("resultMsg: " + resultMsg);
		if (!resultMsg.equals("success")) {
			returnMsg = "FAIL";
		}
		SortedMap<Object, Object> paramMap = createRequestMap(returnMsg);
		String result = PayCommonUtil.getRequestXml(paramMap);
		System.out.println("result:" + result);
		sendToClient(resp, result);
		if (resultMsg.equals("success")) {
			// sm.setStatus("0");
			// SendMessage.send(sm, "anySDK_sx", "anySDK_sx", "weixin");
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	public static SortedMap<Object, Object> getSortedMap(Map<String, String> map) {
		SortedMap<Object, Object> sortedMap = new TreeMap<>();
		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		for (Map.Entry<String, String> entry : entrySet) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static SortedMap<Object, Object> createRequestMap(String msg) {
		SortedMap<Object, Object> params = new TreeMap<>();
		params.put("return_code", msg);
		return params;
	}

	private void sendToClient(HttpServletResponse response, String content) {
		response.setContentType("text/plain;charset=utf-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.write(content);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
