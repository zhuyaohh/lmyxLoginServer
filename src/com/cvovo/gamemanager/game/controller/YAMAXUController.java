package com.cvovo.gamemanager.game.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.CheckService;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;

@RestController
@RequestMapping("/")
public class YAMAXUController {
	private static final Logger logger = LoggerFactory.getLogger(YAMAXUController.class);
	@Autowired
	private CheckService checkService;
	@Autowired
	private OrderService orderServcie;

	@Autowired
	private ServerService serverService;

	@RequestMapping(value = "ymxBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getOrderr(final String userId, final String receiptId, final String roleId, final String serverId, HttpServletResponse responseBase) throws IOException, JSONException {
		logger.info("============roleId==" + roleId);
		logger.info("============serverId==" + serverId);
		String result = "-1";
		final String developerSecret = "2:443U0kPTn3vn-0SRths_0g04XplrIMvpSEpSFKnwi1l8YKA4ObTaAhhUx9arwmpS:BlKPRDLiS68fjTT21zQmlg==";
		System.out.println("Start Receipt Validation");
		// String url = "https://appstore-sdk.amazon.com/version/1.0/verifyReceiptId/developer/" + developerSecret + "/user/" + userId + "/receiptId/" + receiptId;
		// String url = "https://appstore-sdk.amazon.com/version/1.0/verifyReceiptId/developer/" + developerSecret + "/user/" + userId + "/receiptId/" + receiptId;
		// http://localhost:8080/RVSSandbox/version/1.0/verifyReceiptId/developer/developerSecret/user/99FD_DL23EMhrOGDnur9-ulvqomrSg6qyLPSD3CFE=/receiptId/q1YqVrJSSs7P1UvMTazKz9PLTCwoTswtyEktM9JLrShIzCvOzM-LL04tiTdW0lFKASo2NDEwMjCwMDM2MTC0AIqVAsUsLd1c4l18jIxdfTOK_N1d8kqLLHVLc8oK83OLgtPNCit9AoJdjJ3dXG2BGkqUrAxrAQ
		String testUrl = "http://221.242.28.73:8181/RVSSandbox/version/1.0/verifyReceiptId/developer/" + developerSecret + "/user/" + userId + "/receiptId/" + receiptId;
		if (checkService.findCheckByStr("YaMaXun") != null) {
			testUrl = "https://appstore-sdk.amazon.com/version/1.0/verifyReceiptId/developer/" + developerSecret + "/user/" + userId + "/receiptId/" + receiptId;
		}
		logger.info("Amazon Receipt Validation URL:" + testUrl);
		try {
			logger.info("Open HTTP connection to Amazon RVS");
			URL obj = new URL(testUrl);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			int responseCode = con.getResponseCode();
			logger.info("Amazon RVS Response Code:" + responseCode);
			switch (responseCode) {
			case 400:
				logger.info("Amazon RVS Error:Invalid receiptID");
				// Process Response Data locally
				// Respond to app
				break;
			case 496:
				logger.info("Amazon RVS Error:Invalid developerSecret");
				// Process Response Data locally
				// Respond to app
				break;
			case 497:
				logger.info("Amazon RVS Error:Invalid userId");
				// Process Response Data locally
				// Respond to app
				break;
			case 500:
				logger.info("Amazon RVS Error:Internal Server Error");
				// Process Response Data locally
				// Respond to app
				break;
			case 200:
				// Retrieve Amazon RVS Response
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// Log Amazon RVS Response
				logger.info("Amazon RVS Response:" + response.toString());
				// Create JSONObject for RVS Response
				JSONObject responseJson = new JSONObject(response.toString());
				// Parse RVS Response
				String receiptIdS = responseJson.getString("receiptId");
				String productTypes = responseJson.getString("productType");
				String productIdS = responseJson.getString("productId");
				String purchaseDates = responseJson.getString("purchaseDate");
				String cancelDateS = responseJson.getString("cancelDate");
				boolean testTransactionS = responseJson.optBoolean("testTransaction");
				logger.info("===============receiptIdS==========" + receiptIdS);
				logger.info("===============productType==========" + productTypes);
				logger.info("===============productId==========" + productIdS);
				logger.info("===============purchaseDates==========" + purchaseDates);
				logger.info("===============cancelDateS==========" + cancelDateS);
				logger.info("===============testTransactionS==========" + testTransactionS);
				if (orderServcie.getOrderByCpOrderId(receiptIdS) != null)// 数据库效验)
				{
					result = "-2";

				}

				Timestamp t = new Timestamp(Long.parseLong(purchaseDates));
				// Timestamp t1 = new Timestamp(Long.parseLong(cancelDateS));
				OrderInfo oi = new OrderInfo();
				oi.setOrderId(receiptIdS);
				oi.setRoleId(roleId);
				oi.setSubjectId(productIdS);
				oi.setServerId(Integer.parseInt(serverId));
				oi.setDate(t);
				// oi.setFinishDate(t1);
				oi.setStatus(2);
				oi.setType("YMX");
				orderServcie.create(oi);
				logger.info("db===============end");
				if (doPostOrderRecharge(oi)) {
					result = "1";
					logger.info("r_receip============回调成功==============");
				}
				// 保存到数据库
				PrintWriter writer = responseBase.getWriter();
				responseBase.getWriter().write(result);
				writer.close();
				break;
			default:
				System.out.println("Amazon RVS Error:Undefined Response Code From Amazon RVS");
				break;
			}
		} catch (MalformedURLException e) {
			System.out.println("Amazon RVS MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Amazon RVS IOException");
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

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date1 = new Date(0);
		Date date = new Date();
		Timestamp t = new Timestamp(1498098213615l);

		// try {
		// // date = sdf.parse(t .getTime());
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		System.out.println("===============" + date1);

		System.out.println("===============" + t);

	}
}
