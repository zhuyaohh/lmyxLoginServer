package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.alipay.AliPayNotify;
import com.cvovo.gamemanager.game.alipay.AlipayConfig;
import com.cvovo.gamemanager.game.alipay.RSA;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.game.service.ShopItemService;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class AliPayVerifyController {

	private static final Logger logger = LoggerFactory.getLogger(AliPayVerifyController.class);

	@Autowired
	private ServerService serverService;

	@Autowired
	private ShopItemService shopItemService;

	@Autowired
	private OrderService orderServcie;

	@RequestMapping(value = "zfbUrl", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getZfbRegister(String token) {
		// String notify_url = "http://mwqyv6.dongcaisz.com:8081/lmyxLoginServer/zfbBillNo";
		String notify_url = "http://zmsj.game101.cn:8081/lmyxLoginServer/zfbBillNo";
		logger.info("======welcome to getZfbRegister===================");
		logger.info("token=========================" + token);
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
		if ((type.equalsIgnoreCase("ZM_IOS") || type.equalsIgnoreCase("ZM_IOS"))) {
			String billNo = PublicMethod.getUniqueId();
			OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, Integer.parseInt(serverid), subjectId, type, Integer.parseInt(characterid), billNo, String.valueOf(billNo));
			orderInfo.setRoleId(characterid);
			int d = (int) Float.parseFloat(totalFee) / 100;
			String orderString = getOrderInfo(String.valueOf(billNo), subject, String.valueOf(d), notify_url);
			logger.info("=========orderInfo===========" + orderString);
			if (orderServcie.create(orderInfo)) {
				return RestResult.getBillZFBSuc("1", orderString);
			} else {
				return RestResult.getBillZFBSuc("0", orderString);
			}
		}
		return RestResult.FAILURE_USE_OR_PWD_ERROR;
	}

	@RequestMapping(value = "zfbBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getZfbBillNo(HttpServletRequest req, HttpServletResponse response) throws IOException {
		logger.info("======welcome to getZfbBillNo===================");
		String ip = getIpAddress(req);
		String str = "SUCCESS";
		Map<String, String[]> requestParams = req.getParameterMap();
		Map<String, String> params = new HashMap<>();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = iter.next();
			String[] values = requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}

		String trade_status = req.getParameter("trade_status");
		if (!trade_status.equals("TRADE_FINISHED") && !trade_status.equals("TRADE_SUCCESS")) {
			sendToClient(response, "success");
			return;
		}
		String orderId = new String(req.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String buyerLoginId = new String(req.getParameter("buyer_email").getBytes("ISO-8859-1"), "UTF-8");
		String privateData = new String(req.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
		String price = new String(req.getParameter("price").getBytes("ISO-8859-1"), "UTF-8");
		logger.info("交易流水号trade_no: " + orderId);
		logger.info("交易状态：" + trade_status);
		logger.info("交易金额：" + price);
		OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(privateData);
		if (orderInfo == null) {
			return;
		}
		orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
		orderInfo.setRoleName(buyerLoginId);
		orderServcie.update(orderInfo);
		if ((!AliPayNotify.verify(params))) {

		} else {
			doPostOrderRecharge(orderInfo);
			response.getWriter().write(str);
		}

	}

	public String getOrderInfo(String billNo, String subject, String totalFee, String notifyUrl) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + AlipayConfig.partner + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + AlipayConfig.seller_id + "\"";

		orderInfo += "&out_trade_no=" + "\"" + billNo + "\"";
		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";
		// 商品详情
		orderInfo += "&body=" + "\"" + subject + "\"";
		// 商品金额
		orderInfo += "&total_fee=" + "\"" + totalFee + "\"";
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + notifyUrl + "\"";
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";
		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		String sign = RSA.sign(orderInfo, AlipayConfig.private_key, "UTF-8");
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		orderInfo += "&sign=" + "\"" + sign + "\"";
		orderInfo += "&sign_type=\"RSA\"";
		logger.info("orderInfo==========" + orderInfo);
		return orderInfo;

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
			logger.info("发送HTTP请求========================" + args.toString());
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			logger.info("拿到HTTP请求结果========================");
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
