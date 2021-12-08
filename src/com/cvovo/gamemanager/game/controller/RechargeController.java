package com.cvovo.gamemanager.game.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Hashtable;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.config.AppConfig;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.AccountService;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class RechargeController {
	private static final Logger logger = LoggerFactory.getLogger(RechargeController.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;

	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "bill", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getRegister(String token) throws UnsupportedEncodingException {
		if (appConfig.CALLBACK_PAY == 0)
			return null;
		String xq_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDAG/B5Ce6ECDtldcrhXlG7vGtxod+eoQ8//u2UOuuG7c2oZKEcMqVoKftITG8rWDjVK+2fDnaEJDll8apJNRCB9DIKtO+fPg7DhuG+u5n3gjlXGxx1fZXUiLa5LaTAQfEFAgd+O6yecnXWUnZEmBVDFdfIkaMFtlI0hCk4fyah9QIDAQAB";
		logger.info("======appConfig.CALLBACK_PAY =====" + appConfig.CALLBACK_PAY);
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
		String acturalFee = table.get("acturalFee");
		String totalFee = table.get("acturalFee");
		String roleId = table.get("characterid");

		if (type == null || type.equals("")) {
			type = "未知";
		}

		logger.info("===================进入======");
		String billNo = PublicMethod.getUniqueId();
		String guid = "";
		String ext1 = "";
		StringBuffer sb = new StringBuffer("game_area=");
		if (type != null && type.equals("XQ_IOS")) {
			xq_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDD5C3/+PUnJg7Aqfvp692sZaYHGtoDze4ShMur4E/YkU7gWJ/m9aW1cPl0fpLz9rAmz8mk3SG+Uh7FCg1gNZhosBbbtOFs2+hcky9z+aVg9M3Mw31lV55UqEBmxQEOuYaL6cCYp+gj+pup63Ljo+VG7Wctlm6MpdmU8qLXe4PlewIDAQAB";
		}
		if (type != null && type.equals("XQ_SDK") || type.equals("XQ_IOS")) {
			String price = table.get("price");
			// StringBuffer twoSb =new StringBuffer();
			// twoSb.append(table.get("characterid")+",");
			// twoSb.append(serverid+",");
			// twoSb.append(table.get("subjectId")+",");
			// twoSb.append(billNo+",");
			// twoSb.append(table.get("type"));
			// sb.append(twoSb.toString()+"&game_area=");
			sb.append(serverid + "&game_orderid=");
			// sb.append(table.get("ulevel")+"&game_orderid=");
			sb.append(billNo + "&game_price=");
			sb.append(price + ".00" + "&subject=");
			sb.append(table.get("productDesc")).append(xq_public_key);
			System.out.println("sb=========" + sb.toString());
			ext1 = MD5.GetMD5Code(sb.toString());
			System.out.println("gamesign=========" + ext1);
			OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, Integer.parseInt(serverid), subjectId, type, table.get("accountId"), billNo, String.valueOf(billNo));
			logger.info("=========对象存储=========");
			if (orderServcie.create(orderInfo)) {
				return RestResult.getBillSuc("1", String.valueOf(billNo), guid, ext1, "");
			} else {
				return RestResult.getBillSuc("0", String.valueOf(billNo), guid, ext1, "");
			}
		} else {
			OrderInfo orderInfo = new OrderInfo(billNo, totalFee, acturalFee, Integer.parseInt(serverid), subjectId, type, table.get("accountId"), billNo, String.valueOf(billNo));
			orderInfo.setRoleId(roleId);
			logger.info("=========对象存储=========");
			if (orderServcie.create(orderInfo)) {
				return RestResult.getBillSuc("1", String.valueOf(billNo), guid);
			} else {
				return RestResult.getBillSuc("0", String.valueOf(billNo), guid);
			}

		}

	}

	@RequestMapping(value = "dnyBillNo", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getOrder(String userId, String appId, String serverId, String cpOrderId, String orderId, String goodsId, String goodsCount, String price, String priceUnit, String extParams, String createdTime, String finishTime, String roleId, String sign) {
		logger.info("=========appId回调数据============" + appId);
		logger.info("=========createTime回调数据=========" + createdTime);
		logger.info("=========cpOrderId回调数据=========" + cpOrderId);
		logger.info("=========extParams回调数据==========" + extParams);
		logger.info("=========finishTime回调数据=========" + finishTime);

		logger.info("=========goodsCount回调数据=======" + goodsCount);
		logger.info("=========googsId回调数据===========" + goodsId);
		logger.info("=========orderId回调数据===========" + orderId);
		logger.info("=========pirce回调数据============" + price);
		logger.info("=========priceUnit回调数据==========" + priceUnit);

		logger.info("=========roleId回调数据============" + roleId);
		logger.info("=========serverId回调数据===========" + serverId);
		logger.info("=========userId回调数据=========" + userId);

		logger.info("=========sign回调数据=============" + sign);
		OrderInfo orderInfo = null;
		int positon = 1;
		if (cpOrderId != null && !cpOrderId.equals("")) {
			positon = 0;
			orderInfo = orderServcie.getOrderByCpOrderId(cpOrderId);
			if (orderInfo == null) {
				return RestResult.billDNY("2", "账单不存在");
			} else if (orderInfo != null && orderInfo.getStatus() == 3) {
				orderInfo.setDnyOrderId(orderId);
				orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
				orderInfo.setPriceUnit(priceUnit);
				orderServcie.update(orderInfo);
				return RestResult.billDNY("0", "ok");
			} else if (orderInfo != null && orderInfo.getStatus() == 1) {
				orderInfo.setStatus(2);
				orderInfo.setDnyOrderId(orderId);
				orderInfo.setPriceUnit(priceUnit);
				orderServcie.update(orderInfo);
			}
		} else {
			if (orderId != null && !orderId.equals("")) {
				orderInfo = orderServcie.getOrderByOrderId(orderId);
			}
		}

		logger.info("==获取订单信息====================");
		String type = "DNY_IOS";
		String appsecret = "1cd5249f81e61fce042520e399568007";// appID="1009"
		if (appId.equals("1010")) {
			type = "DNY_ANDROID";
			appsecret = "5ce1f955a862de63f5081b399e55c015";
		} else if (appId != null && appId.equals("1013")) {
			type = "DNYE_IOS";
			appsecret = "971fb9fe699bc62f31ae71f6eca92f2c";
		} else if (appId != null && appId.equals("1014")) {
			type = "DNYE_ANDROID";
			appsecret = "b836eb10b4ba4c7534c1977818a00c56";
		}
		StringBuffer tempSign = new StringBuffer(appId);
		if (cpOrderId != null && !cpOrderId.equals("")) {
			tempSign.append(cpOrderId);
		}
		if (createdTime != null && !createdTime.equals("")) {
			tempSign.append(createdTime);
		}
		if (extParams != null && !extParams.equals("")) {
			tempSign.append(extParams);
		}
		if (finishTime != null && !finishTime.equals("")) {
			tempSign.append(finishTime);
		}
		if (goodsCount != null && !goodsCount.equals("")) {
			tempSign.append(goodsCount);
		}
		if (goodsId != null && !goodsId.equals("")) {
			tempSign.append(goodsId);
		}
		if (orderId != null && !orderId.equals("")) {
			tempSign.append(orderId);
		}
		if (price != null && !price.equals("")) {
			tempSign.append(price);
		}
		if (priceUnit != null && !priceUnit.equals("")) {
			tempSign.append(priceUnit);
		}
		if (roleId != null && !roleId.equals("")) {
			tempSign.append(roleId);
		}
		if (serverId != null && !serverId.equals("")) {
			tempSign.append(serverId);
		}
		if (userId != null && !userId.equals("")) {
			tempSign.append(userId);
		}
		tempSign.append(appsecret);
		logger.info("===tempSign==加密前==" + tempSign.toString());
		String key = "";
		try {
			key = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("===tempSign==加密后==" + key);
		logger.info("===sign==回调=" + sign);
		if (orderInfo == null && positon == 1) {
			String newPrice = price;
			if (priceUnit.equalsIgnoreCase("TWD")) {
				Float value = Float.parseFloat(price) * 100;
				newPrice = String.valueOf(value);
			}
			orderInfo = new OrderInfo(orderId, newPrice, newPrice, (int) Float.parseFloat(serverId), goodsId, type, Integer.parseInt(userId), orderId, orderId);
			orderInfo.setStatus(2);
			orderInfo.setPriceUnit(priceUnit);
			orderServcie.update(orderInfo);
		}
		logger.info("==启动验证=================");
		if (key.equals(sign)) {

			int id = Integer.parseInt(serverId);
			ServerInfo serverInfo = serverService.getServerInfo(id);
			if (serverInfo != null) {
				logger.info("==验证通过=================启动游戏服务器");
				String url = serverInfo.getUrl();
				StringBuffer args = new StringBuffer("c=4001");
				args.append("&playerId=" + roleId);
				if (goodsId != null && !goodsId.equals("")) {
					args.append("&shopId=" + goodsId);
				} else {
					args.append("&shopId=" + "0000");
				}
				args.append("&order=" + orderId);
				args.append("&price=" + price);
				args.append("&source=" + type);
				args.append("&priceUnit=" + priceUnit);
				args.append("&goodsCount=" + goodsCount);
				String[] strList = url.split(":");
				StringBuffer title = new StringBuffer("http://");
				StringBuffer ip = new StringBuffer(strList[0]);
				title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
				logger.info("发送HTTP请求========================" + args.toString());
				String result = HttpRequest.sendGet(title.toString(), args.toString());
				logger.info("拿到HTTP请求结果========================");
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if (jsonObject.has("errorId")) {
						String errorId = jsonObject.get("errorId").toString();
						if (errorId != null && errorId.equals("0")) {
							logger.info("游戏服务器返回正确结果========================");
							orderInfo.setStatus(3);
							orderInfo.setFinishDate(new Timestamp(System.currentTimeMillis()));
							orderServcie.update(orderInfo);
							logger.info("保存结果========================");
							return RestResult.billDNY("0", "ok");
						} else {
							String msg = jsonObject.get("msg").toString();
							logger.info("msg=======" + msg);
							return RestResult.billDNY("1", msg);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}

		return RestResult.billDNY("1", "验证不通过");
	}

	@RequestMapping(value = "billNo", method = { RequestMethod.GET, RequestMethod.POST })
	public int getOrderr(String orderId, String totalFee, String acturalFee, String areaId, String serverId, String characterId, String subjectId, String sign) {

		int finalResult = 0;
		logger.info("=========orderId回调数据===========" + orderId);
		String appSecret = "90d3acae381847b687c02b570a75f68c";
		OrderInfo tempOrder = orderServcie.getOrderByOrderId(orderId);
		if (tempOrder == null) {
			return 2;
		}
		if (tempOrder != null && tempOrder.getStatus() == 3) {
			return 0;
		} else if (tempOrder != null && tempOrder.getStatus() == 1) {
			tempOrder.setStatus(2);
			tempOrder.setRoleId(characterId);
			orderServcie.update(tempOrder);
		}
		logger.info("保存订单信息====================");
		StringBuffer contents = new StringBuffer(acturalFee).append("#");
		contents.append(areaId).append("#");
		contents.append(characterId).append("#");
		contents.append(orderId).append("#");
		contents.append(serverId).append("#");
		contents.append(subjectId).append("#");
		contents.append(totalFee).append("#");
		contents.append(appSecret);
		String tempSign = null;
		try {
			tempSign = MD5.GetMD5Code(contents.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tempSign != null && tempSign.equals(sign)) {
			logger.info("MD5验证通过==============================");
			if (serverId != null && !serverId.equals("")) {
				int id = Integer.parseInt(serverId);
				ServerInfo serverInfo = serverService.getServerInfo(id);
				if (serverInfo != null) {
					logger.info("成功获取服务器地址==============================");
					String url = serverInfo.getUrl();
					StringBuffer args = new StringBuffer("c=1004");
					args.append("&playerId=" + characterId);
					args.append("&shopId=" + subjectId);
					args.append("&order=" + orderId);
					args.append("&price=" + (int) Float.parseFloat(totalFee));
					args.append("&source=" + tempOrder.getType());
					String[] strList = url.split(":");
					StringBuffer title = new StringBuffer("http://");
					StringBuffer ip = new StringBuffer(strList[0]);
					title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
					logger.info("发送HTTP请求========================" + args.toString());
					String result = HttpRequest.sendGet(title.toString(), args.toString());
					logger.info("拿到HTTP请求结果========================");
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result);
						if (jsonObject.has("errorId")) {
							String errorId = jsonObject.get("errorId").toString();
							if (errorId != null && errorId.equals("0")) {
								logger.info("游戏服务器返回正确结果========================");
								tempOrder.setStatus(3);
								tempOrder.setFinishDate(new Timestamp(System.currentTimeMillis()));
								orderServcie.update(tempOrder);
								logger.info("保存结果========================");
								logger.info(String.valueOf(finalResult));
								return finalResult;
							} else {
								String msg = jsonObject.get("msg").toString();
								finalResult = 1;
								logger.info("msg=======" + msg);
								return finalResult;
							}

						}

					} catch (Exception ex) {

					}

				}

			}
		}
		return 2;
	}

	@ResponseBody
	@RequestMapping(value = "billSo", method = { RequestMethod.GET, RequestMethod.POST })
	public int getOrderr(String a) {
		return 0;
	}

	@ResponseBody
	@RequestMapping(value = "total", method = { RequestMethod.GET, RequestMethod.POST })
	public int getTotalByServerId(String serverId) {
		if (serverId != null) {
			return orderServcie.getTotalMoneyByServerId(Integer.parseInt(serverId));
		}
		return 0;
	}

}
