package com.cvovo.gamemanager.game.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.game.service.ShopItemService;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.MD5Util;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;
import com.cvovo.gamemanager.util.XMLUtil;

@RestController
@RequestMapping("/")
public class WeiXinPayController {

	private static final Logger logger = LoggerFactory.getLogger(WeiXinPayController.class);
	@Autowired
	private OrderService orderServcie;

	@Autowired
	private ServerService serverService;

	@Autowired
	private ShopItemService shopItemService;

	@RequestMapping(value = "wxUrl", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getWxRegister(String token, HttpServletRequest request) {
		logger.info("=======welcom to getWxRegister====================");
		logger.info("token=========================" + token);

		String appId = "wx57d465cc3fb2bf5d";
		String mch_id = "1459020902";
		String[] str = token.split("&");
		Hashtable<String, String> table = new Hashtable<String, String>();
		for (int i = 0; i < str.length; i++) {
			String tempStr = str[i];
			int d = tempStr.indexOf("=");
			String key = tempStr.substring(0, d);
			String value = tempStr.substring(d + 1, tempStr.length());
			table.put(key, value);
		}
		String type = table.get("type");
		String serverid = table.get("serverid");
		String subjectId = table.get("subjectId");
		String subject = table.get("subject");
		String acturalFee = table.get("acturalFee");
		String totalFee = table.get("acturalFee");
		String accountId = table.get("accountId");
		String characterid = table.get("characterid");

		if (type == null || type.equals("")) {
			type = "未知";
		}

		String notify_url = "http://tap.game101.cn:8881/lmyxLoginServer/wxBillNo";

		String billNo = PublicMethod.getBillUniqueId();
		OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, Integer.parseInt(serverid), subjectId, type, Integer.parseInt(characterid), billNo, String.valueOf(billNo));
		orderInfo.setRoleId(characterid);
		String billIp = getIpAddress(request);
		if (orderServcie.create(orderInfo)) {
			return getWXOrderInfo(appId, subject, mch_id, CreateNoncestr(), notify_url, billNo, acturalFee, billIp);
		} else {
			return RestResult.FAILURE_USE_OR_PWD_ERROR;
		}

	}

	@RequestMapping(value = "wxBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getWxOrderr(HttpServletRequest req, HttpServletResponse response) throws IOException {
		logger.info("=======welcom to getWxOrderr====================");
		InputStream in = req.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.close();
		in.close();
		String msgxml = new String(out.toByteArray(), "utf-8");// xml数据
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			resultMap = XMLUtil.doXMLParse(msgxml);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String outTradeNo = resultMap.get("out_trade_no");
		logger.info("============outTradeNo: ===" + outTradeNo);
		OrderInfo orderInfo = orderServcie.getOrderByOrderId(outTradeNo);
		if (orderInfo != null) {
			boolean isV = getWeiXinOrderSHZ(resultMap);
			if (isV) {
				int newPrice = Integer.valueOf(orderInfo.getActuralFee()) / 100;

				doPostOrderRecharge(orderInfo);
			}
		}

	}

	// 水浒传
	public static boolean getWeiXinOrderSHZ(Map<String, String> resultMap) {
		logger.info("=======welcom to getWeiXinOrderSHZ====================" + resultMap);
		if (!resultMap.get("result_code").equals("SUCCESS")) {
			return false;
		}
		String newSign = createSign("UTF-8", getSortedMap(resultMap));
		if (newSign.equals(resultMap.get("sign"))) {
			return true;
		}
		return false;
	}

	public static String createSign(String characterEncoding, SortedMap<Object, Object> parameters) {
		StringBuffer sb = new StringBuffer();
		Set es = parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			Object v = entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=9pVHXccPfwmaqdTlwZDTfvT9PyntHN2T");
		String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
		return sign;
	}

	public static SortedMap<Object, Object> getSortedMap(Map<String, String> map) {
		SortedMap<Object, Object> sortedMap = new TreeMap<>();
		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		for (Map.Entry<String, String> entry : entrySet) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public boolean doPostOrderRecharge(OrderInfo orderInfo) {
		logger.info("======doPostIOSRecharge================");
		int id = orderInfo.getServerId();
		ServerInfo serverInfo = serverService.getServerInfo(id);
		if (serverInfo != null) {
			logger.info("成功获取服务器地址==============================");
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=1004");
			args.append("&playerId=" + orderInfo.getRoleId());
			args.append("&shopId=" + orderInfo.getSubjectId());
			args.append("&order=" + orderInfo.getOrderId());
			args.append("&price=" + orderInfo.getTotalFee());
			args.append("&source=" + orderInfo.getType());
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
				if (jsonObject.has("errorId")) {
					String errorId = jsonObject.get("errorId").toString();
					if (errorId != null && errorId.equals("0")) {
						orderInfo.setStatus(3);
						orderServcie.update(orderInfo);
						return true;
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	public static String CreateNoncestr() {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String res = "";
		for (int i = 0; i < 30; i++) {
			Random rd = new Random();
			res += chars.charAt(rd.nextInt(chars.length() - 1));
		}
		return res;
	}

	public RestResult getWXOrderInfo(String appid, String body, String mch_id, String nonce_str, String notify_url, String out_trade_no, String total_fee, String billIp) {
		StringBuffer sb = new StringBuffer("appid=");
		sb.append(appid);
		sb.append("&body=");
		sb.append(body);
		sb.append("&mch_id=");
		sb.append(mch_id);
		sb.append("&nonce_str=");
		sb.append(nonce_str);
		sb.append("&notify_url=");
		sb.append(notify_url);
		sb.append("&out_trade_no=");
		sb.append(out_trade_no);
		sb.append("&sign_type=MD5");
		sb.append("&spbill_create_ip=" + billIp);
		sb.append("&total_fee=" + total_fee);
		sb.append("&trade_type=APP");
		sb.append("&key=9pVHXccPfwmaqdTlwZDTfvT9PyntHN2T");

		String sign = "";

		try {
			sign = MD5.GetMD5Code(sb.toString()).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("==sign=====" + sign);

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("appid", appid);
		map.put("body", body);
		map.put("mch_id", mch_id);
		map.put("nonce_str", nonce_str);
		map.put("notify_url", notify_url);
		map.put("out_trade_no", out_trade_no);
		map.put("sign_type", "MD5");
		map.put("spbill_create_ip", billIp);
		map.put("total_fee", total_fee);
		map.put("trade_type", "APP");
		map.put("sign", sign);
		String orderInfo = getRequestXml(map);
		String result = HttpRequest.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", orderInfo);
		logger.info("result=====" + result);
		Map mapResult = null;
		try {
			mapResult = XMLUtil.doXMLParse(result);
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String return_code = (String) mapResult.get("return_code");
		logger.info("return_code=====" + return_code);
		if (return_code != null && return_code.equals("SUCCESS")) {
			String result_code = (String) mapResult.get("result_code");
			if (result_code != null && result_code.equals("SUCCESS")) {
				String prepay_id = (String) mapResult.get("prepay_id");
				String radom = CreateNoncestr();
				String timeT = String.valueOf(System.currentTimeMillis() / 1000);
				String signTemp = tosign(appid, mch_id, prepay_id, radom, timeT);
				logger.info("signTemp====" + signTemp);
				logger.info("prepay_id====" + prepay_id);
				logger.info("radom====" + radom);
				logger.info("timeT====" + timeT);
				return RestResult.getBillWXSuc("1", prepay_id, radom, signTemp, timeT);
			}
		} else {
			logger.info("return_msg==" + mapResult.get("return_msg"));
		}
		return RestResult.FAILURE_INVALID_USE_ERROR;
	}

	public String tosign(String appid, String partnerid, String prepayid, String noncestr, String time) {
		StringBuffer sb = new StringBuffer("appid=");
		sb.append(appid);
		sb.append("&noncestr=");
		sb.append(noncestr);// package
		sb.append("&package=");
		sb.append("Sign=WXPay");
		sb.append("&partnerid=");
		sb.append(partnerid);
		sb.append("&prepayid=");
		sb.append(prepayid);
		sb.append("&timestamp=");
		sb.append(time);
		sb.append("&key=9pVHXccPfwmaqdTlwZDTfvT9PyntHN2T");
		String sign = "";

		try {
			sign = MD5.GetMD5Code(sb.toString()).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}

	public static String getRequestXml(Map<Object, Object> map) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml version=\"1.0\" encoding=\"UTF-8\" >");
		Set es = map.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
				sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
			} else {
				sb.append("<" + k + ">" + v + "</" + k + ">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	private String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
