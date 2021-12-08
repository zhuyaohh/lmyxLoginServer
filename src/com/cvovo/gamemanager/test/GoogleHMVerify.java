package com.cvovo.gamemanager.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.controller.HttpRequest;
import com.cvovo.gamemanager.game.controller.YinNiPayController;
import com.cvovo.gamemanager.game.persist.entity.IosOrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.IosOrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.PublicMethod;
@Service
@Transactional
public class GoogleHMVerify {
	private static final Map<String, String> shopItem = new HashMap<String, String>();
	static {
		shopItem.put("com.haichuang.jcy.gp.yk", "4.99");
		shopItem.put("com.haichuang.jcy.gp.zzk", "29.99");
		shopItem.put("com.haichuang.jcy.gp.01", "0.99");
		shopItem.put("com.haichuang.jcy.gp.02", "4.99");
		shopItem.put("com.haichuang.jcy.gp.03", "9.99");
		shopItem.put("com.haichuang.jcy.gp.04", "29.99");
		shopItem.put("com.haichuang.jcy.gp.05", "49.99");
		shopItem.put("com.haichuang.jcy.gp.06", "99.99");
	}
	private static final Logger logger = LoggerFactory.getLogger(GoogleHMVerify.class);
	private static Map<String, String> cacheToken = null;
	/***
	 * 日本
	 */
	private static final String CLIENT_ID = "955673748475-4m4qmqfkoa27f43sf1bsh4tiuc0uu71e.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "C_BdnzuLenUWpNopMKhWKsT6";
	private static final String REFRESH_TOKEN = "1/CzLMXjAX9pOnK61fAV6aGPawTdTK-C6DjiLcbSNsoNI";
	/**                                          1/GTBCReHnzAJQIAGnlPTqJTHQUL_ZWTD-Kq1K81V4DqnJatW4LLCaqmSyFQJ5OM24
	 * 港澳台
	 * */

	// private static final String CLIENT_ID = "472748606971-l0d62ga63ikfgb4qseffcuvj3cob82jr.apps.googleusercontent.com";
	// private static final String CLIENT_SECRET = "-v6OrliTzQCtXI0oAnacJb9G";
	// private static final String REFRESH_TOKEN = "1/T4ZKTs8hnCyKPLPS0JQjiqJo4pzxZDkXAOCNhA_DmuE";

	@Autowired
	private IosOrderService iosOrdeService;
	@Autowired
	private ServerService serverService;

	private Map<String, String> getAccessToken() {
		Map<String, String> map = null;
		try {
			URL urlGetToken = new URL("https://accounts.google.com/o/oauth2/token");
			HttpURLConnection connectionGetToken = (HttpURLConnection) urlGetToken.openConnection();
			connectionGetToken.setRequestMethod("POST");
			connectionGetToken.setDoOutput(true);
			// 开始传送参数
			OutputStreamWriter writer = new OutputStreamWriter(connectionGetToken.getOutputStream());

			writer.write("refresh_token=" + REFRESH_TOKEN + "&");
			writer.write("client_id=" + CLIENT_ID + "&");
			writer.write("client_secret=" + CLIENT_SECRET + "&");
			writer.write("grant_type=refresh_token");
			writer.close();
			// 若响应码为200则表示请求成功
			if (connectionGetToken.getResponseCode() == HttpURLConnection.HTTP_OK) {
				StringBuilder sb = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(connectionGetToken.getInputStream(), "utf-8"));
				String strLine = "";
				while ((strLine = reader.readLine()) != null) {
					sb.append(strLine);
				}
				// 取得谷歌回传的信息(JSON格式)
				JSONObject jo = JSONObject.fromObject(sb.toString());
				String ACCESS_TOKEN = jo.getString("access_token");
				Integer EXPIRES_IN = jo.getInt("expires_in");
				map = new HashMap<String, String>();
				map.put("access_token", ACCESS_TOKEN);
				map.put("expires_in", String.valueOf(EXPIRES_IN));
				// 带入access_token的创建时间，用于之后判断是否失效
				map.put("create_time", String.valueOf((new Date().getTime()) / 1000));
				logger.info("包含access_token的JSON信息为: " + jo);
			}
		} catch (MalformedURLException e) {
			logger.error("获取access_token失败,原因是:" + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("获取access_token失败,原因是:" + e);
			e.printStackTrace();
		}
		return map;
	}

	public IosOrderInfo getInfoFromGooglePlayServer(String puchaseTime, String packageName, String productId, String purchaseToken, String serverId, String roleId, String type) {
		logger.info("======getInfoFromGooglePlayServer================");
		IosOrderInfo buyEntity = new IosOrderInfo();
		buyEntity.setServerId(serverId);
		buyEntity.setCreateOrderDate(new Timestamp(System.currentTimeMillis()));
		buyEntity.setProductId(productId);
		buyEntity.setRoleId(roleId);
		buyEntity.setMd5Receipt(PublicMethod.getUniqueId());
		buyEntity.setStatus("-99");
		buyEntity.setType(type);
		if (null != cacheToken) {
			logger.info("====== cacheToken is not null============");
			Long expires_in = Long.valueOf(cacheToken.get("expires_in")); // 有效时长
			Long create_time = Long.valueOf(cacheToken.get("create_time")); // access_token的创建时间
			Long now_time = (new Date().getTime()) / 1000;
			if (now_time > (create_time + expires_in - 300)) { // 提前五分钟重新获取access_token
				cacheToken = getAccessToken();
			}
		} else {
			logger.info("======null == cacheToken=============");
			cacheToken = getAccessToken();
		}

		String access_token = cacheToken.get("access_token");
		logger.info("======access_token===========" + access_token);
		try {
			String url = "https://www.googleapis.com/androidpublisher/v2/applications";
			StringBuffer getURL = new StringBuffer();
			getURL.append(url);
			getURL.append("/" + packageName);
			getURL.append("/purchases/products");
			getURL.append("/" + productId);
			getURL.append("/tokens/" + purchaseToken);
			getURL.append("?access_token=" + access_token);
			logger.info("======getURL================" + getURL.toString());
			URL urlObtainOrder = new URL(getURL.toString());
			HttpURLConnection connectionObtainOrder = (HttpURLConnection) urlObtainOrder.openConnection();
			connectionObtainOrder.setRequestMethod("GET");
			connectionObtainOrder.setDoOutput(true);
			logger.info("======启动验证================");
			// 如果认证成功
			int tempCode = connectionObtainOrder.getResponseCode();
			logger.info("======启动验证结果================" + tempCode);
			if (tempCode == HttpURLConnection.HTTP_OK) {
				logger.info("======验证通过================");
				StringBuilder sbLines = new StringBuilder("");
				BufferedReader reader = new BufferedReader(new InputStreamReader(connectionObtainOrder.getInputStream(), "utf-8"));
				String strLine = "";
				while ((strLine = reader.readLine()) != null) {
					sbLines.append(strLine);
				}
				// 把上面取回來的資料，放進JSONObject中，以方便我們直接存取到想要的參數
				JSONObject jo = JSONObject.fromObject(sbLines.toString());
				logger.info("========NICK=============" + sbLines.toString());
				String status = jo.getString("purchaseState");
				if (status.equalsIgnoreCase("0")) { // 验证成功
					buyEntity.setStatus(jo.getString("consumptionState"));
					buyEntity.setDeveloperPayload(jo.getString("developerPayload"));
					logger.info("======验证通过================" + jo.getString("kind"));
					buyEntity.setStatus(status);
					buyEntity.setPurchaseDate(new Timestamp(jo.getLong("purchaseTimeMillis")));
					dotRecharge(buyEntity, "RB_GOOGLE");
				} else {
					// 购买无效
					buyEntity = new IosOrderInfo();
					buyEntity.setStatus(status);
					logger.info("从GooglePlay账单校验失败,原因是purchaseStatus为" + status);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			buyEntity = new IosOrderInfo();
			buyEntity.setStatus("-1");
		}
		iosOrdeService.save(buyEntity);
		return buyEntity;
	}
//	public static void main(String[] args) {
//		String price  =shopItem.get("com.haichuang.jcy.gp.01");
//	System.out.println(price);
//	}
	public boolean dotRecharge(IosOrderInfo orderInfo, String type) {
		logger.info("======doPostIOSRecharge================");
		int id = Integer.parseInt(orderInfo.getServerId());
		ServerInfo serverInfo = serverService.getServerInfo(id);
		if (serverInfo != null) {
			logger.info("成功获取服务器地址==============================");
			String url = serverInfo.getUrl();
			String price  =shopItem.get(orderInfo.getProductId());
			StringBuffer args = new StringBuffer("c=1004");
			args.append("&playerId=" + orderInfo.getRoleId());
			args.append("&shopId=" + orderInfo.getProductId());
			args.append("&order=" + orderInfo.getMd5Receipt());
			args.append("&source=" + type);
			args.append("&price=" + price);
			args.append("&goodsCount=1");
			
			String[] strList = url.split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			logger.info("发送HTTP请求========================" + args.toString());
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			logger.info("拿到HTTP请求结果===" + result);
			org.json.JSONObject jsonObject;
			try {
				jsonObject = new org.json.JSONObject(result);
				if (jsonObject.has("errorId")) {
					String errorId = jsonObject.get("errorId").toString();
					if (errorId != null && errorId.equals("0")) {
						orderInfo.setStatus("3");
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getRoleId(), orderInfo.getMd5Receipt(), Double.parseDouble(price), "USD", Double.parseDouble(price), 
								"HG", "HG", "JCY", "F90A9D3C60444F40940E2CFD8EA7A084");
						iosOrdeService.save(orderInfo);
						return true;
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}
}
