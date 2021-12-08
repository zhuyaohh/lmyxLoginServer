package com.cvovo.gamemanager.game.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.alipay.RSA;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.OrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;
import com.fasterxml.jackson.core.format.InputAccessor;

@RestController
@RequestMapping("/")
public class XQLastPayController {
	private static final Logger logger = LoggerFactory.getLogger(XQLastPayController.class);
	@Autowired
	private ServerService serverService;

	@Autowired
	private OrderService orderServcie;
	private static final Map<String, String> shopItem = new HashMap<String, String>();
	static {
		shopItem.put("com.xphl.zsxsj.01", "30.00");
		shopItem.put("com.xphl.zsxsj.02", "98.00");
		shopItem.put("com.xphl.zsxsj.03", "648.00");
		shopItem.put("com.xphl.zsxsj.04", "328.00");
		shopItem.put("com.xphl.zsxsj.05", "128.00");
		shopItem.put("com.xphl.zsxsj.06", "68.00");
		shopItem.put("com.xphl.zsxsj.07", "30.00");
		shopItem.put("com.xphl.zsxsj.08", "6.00");
		shopItem.put("com.xphl.zsxsj.09", "198.00");
		shopItem.put("com.xphl.zsxsj.10", "1998.00");
		shopItem.put("com.xphl.zsxsj.11", "4998.00");
		shopItem.put("com.xphl.zsxsj.12", "9998.00");
	}

	@RequestMapping(value = "getXqOrderId", method = { RequestMethod.GET, RequestMethod.POST })
	public String getXqOrderPay(String extends_info_data, String game_area, String game_level, String game_orderid,
			String game_price, String game_role_id, String game_role_name, String notify_id, String subject,
			String game_sign, HttpServletRequest request, HttpServletResponse response) throws IOException {

		return "";

	}

	@RequestMapping(value = "xqIOSLastPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getklblIOSAndroidPay(String encryp_data, String extends_info_data, String game_area, String game_level,
			String game_orderid, String game_role_id, String game_role_name, String sdk_version, String subject,
			String xiao7_goid, String sign_data, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("===========xqIOSLastPay================");

		String result = "sign_data_verify_failed";
		String encrypData = encryp_data;
		String extendsInfoData = extends_info_data;
		String gameArea = game_area;
		String gameLevel = game_level;
		String gameOrderId = game_orderid;
		String gameRoleId = game_role_id;
		String gameRoleName = game_role_name;
		String sdkVersion = sdk_version;
		String subjecT = subject;
		String xiao7Goid = xiao7_goid;
		String signData = sign_data;
		StringBuffer sb = new StringBuffer("encryp_data=");
		sb.append(encrypData + "&extends_info_data=");
		sb.append(extendsInfoData + "&game_area=");
		sb.append(gameArea + "&game_level=");
		sb.append(gameLevel + "&game_orderid=");
		sb.append(gameOrderId + "&game_role_id=");
		sb.append(gameRoleId + "&game_role_name=");
		sb.append(gameRoleName + "&sdk_version=");
		sb.append(sdkVersion + "&subject=");
		sb.append(subjecT + "&xiao7_goid=");
		sb.append(xiao7Goid);
		logger.info("===========GO signData================" + signData);
		logger.info("===========GO sb================" + sb.toString());
		if (VerifyController.verifySign(sb.toString(), signData, VerifyController.loadIOSPublicKeyByStr())) {
			logger.info("===========GO xqIOSLastPay================");
			OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(gameOrderId);
			if (orderInfo != null) {
				logger.info("===========orderInfo is not  null================");
				orderInfo.setRoleId(gameRoleId);
				orderInfo.setOrderId(xiao7Goid);
				orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
				orderInfo.setType("XQ_IOS");
				orderInfo.setStatus(1);
				if (shopItem.containsKey(orderInfo.getSubjectId()) && Integer.parseInt(
						orderInfo.getTotalFee()) > (100 * Double.parseDouble(shopItem.get(orderInfo.getSubjectId())))) {
					result = "price error";
					logger.info("===========price error==============");
				} else {
					logger.info("===========price  sign is ok================");
					orderServcie.createStatusByGuoN(orderInfo);
					if (doPostOrderRecharge(orderInfo)) {
						result = "success";
					} else {
						result = "failed:game_orderid error";
					}
				}
			} else {
				logger.info("===========sign_data_verify_failed================");
				result = "sign_data_verify_failed";
			}
		} else {
			logger.info("===========sign_data_verify_failed 对回调参数中 sign_================");
			result = "sign_data_verify_failed 对回调参数中 sign_";
		}

		PrintWriter writer = response.getWriter();
		response.getWriter().write(result);
		writer.close();
	}

	@RequestMapping(value = "xqLastPay", method = { RequestMethod.GET, RequestMethod.POST })
	public void getklblAndroidPay(String encryp_data, String extends_info_data, String game_area, String game_level,
			String game_orderid, String game_role_id, String game_role_name, String sdk_version, String subject,
			String xiao7_goid, String sign_data, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("===========xqLastPay================");

		String result = "sign_data_verify_failed";
		String encrypData = encryp_data;
		String extendsInfoData = extends_info_data;
		String gameArea = game_area;
		String gameLevel = game_level;
		String gameOrderId = game_orderid;
		String gameRoleId = game_role_id;
		String gameRoleName = game_role_name;
		String sdkVersion = sdk_version;
		String subjecT = subject;
		String xiao7Goid = xiao7_goid;
		String signData = sign_data;
		StringBuffer sb = new StringBuffer("encryp_data=");
		sb.append(encrypData + "&extends_info_data=");
		sb.append(extendsInfoData + "&game_area=");
		sb.append(gameArea + "&game_level=");
		sb.append(gameLevel + "&game_orderid=");
		sb.append(gameOrderId + "&game_role_id=");
		sb.append(gameRoleId + "&game_role_name=");
		sb.append(gameRoleName + "&sdk_version=");
		sb.append(sdkVersion + "&subject=");
		sb.append(subjecT + "&xiao7_goid=");
		sb.append(xiao7Goid);
		logger.info("===========GO signData================" + signData);
		logger.info("===========GO sb================" + sb.toString());
		if (VerifyController.verifySign(sb.toString(), signData, VerifyController.loadPublicKeyByStr())) {
			logger.info("===========GO xqLastPay================");
			OrderInfo orderInfo = orderServcie.getOrderByCpOrderId(gameOrderId);
			if (orderInfo != null) {
				logger.info("===========orderInfo is not  null================");
				orderInfo.setRoleId(gameRoleId);
				orderInfo.setOrderId(xiao7Goid);
				orderInfo.setDate(new Timestamp(System.currentTimeMillis()));
				orderInfo.setType("XQ_ANDROID");
				orderInfo.setStatus(1);
				if (shopItem.containsKey(orderInfo.getSubjectId()) && Double.parseDouble(
						orderInfo.getTotalFee()) >(100 * Double.parseDouble(shopItem.get(orderInfo.getSubjectId())))) {
					result = "price error";
					double  d =100 * Double.parseDouble(shopItem.get(orderInfo.getSubjectId()));
					double  M =Double.parseDouble(
							orderInfo.getTotalFee());
					logger.info("===========price error================d="+d+"=====M="+M);
				} else {
					logger.info("===========price  sign is ok================");
					orderServcie.createStatusByGuoN(orderInfo);
					if (doPostOrderRecharge(orderInfo)) {
						result = "success";
					} else {
						result = "failed:game_orderid error";
					}
				}
			} else {
				logger.info("===========sign_data_verify_failed================");
				result = "sign_data_verify_failed";
			}
		} else {
			logger.info("===========sign_data_verify_failed 对回调参数中 sign_================");
			result = "sign_data_verify_failed 对回调参数中 sign_";
		}

		PrintWriter writer = response.getWriter();
		response.getWriter().write(result);
		writer.close();
	}
//
//	public static void main(String[] args) throws Exception {
//		String sb = "encrypData=Y7Hrpya92PhQjB6HJLao8vconGfaBh1bo8QAquhrbB5+ApncTnSh7h+MJsJeqwHTCDW2Q6NNMLNlufR1pbwZwNf4w5SUm+aKZyMT+mZhanVDbk3jD+zxS0XNm1dsFoOAAMyVN1117eoSEO4YDXD8N7OcO//50BcS4x8fIecI46U=&extends_info_data=601349,6,com.xphl.zsxsj.08,122152823_4197,XQ_SDK&game_area=6&game_level=1&game_orderid=122152823_4197&game_role_id=601349&game_role_name=输出祭司&sdk_version=2.0&subject=6元60钻礼包&xiao7_goid=28491652MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDi3BXRMCCLS+oDwAskgedIOmmxImpZIUz2qPd6LBq4YtZmOqG3bTFh9ii0TDTMPpvQ9Q\"\r\n"
//				+ "				+ \"7vUrjSqLJyiKdz4MSOT4Yknn49Kk3u1q8xrUgSxZzFW02Yl7JGZ8lOyknFjDMaWFPcRMHMqEdhitwzb0OtwyveQMwbSczvxLlZTMJncwIDAQAB\"";
//		String signData = "xcJcjwA1fj+tRvFD7RBArJ36+OkJ4Pup7l308k1gNW6EW6S/+SVpUXbShjHop2CM2EOqWNkkV76MD66XV8LkSxKZF5nx5QxLwIk8LUZPFCABzBfpSxHyVqBAsupBNNeo/KiezNN5sOnoTHDjm/s05+hEtQzrA57AtibTDy7I420=";
//		String xq_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDi3BXRMCCLS+oDwAskgedIOmmxImpZIUz2qPd6LBq4YtZmOqG3bTFh9ii0TDTMPpvQ9Q"
//				+ "7vUrjSqLJyiKdz4MSOT4Yknn49Kk3u1q8xrUgSxZzFW02Yl7JGZ8lOyknFjDMaWFPcRMHMqEdhitwzb0OtwyveQMwbSczvxLlZTMJncwIDAQAB";
//
//		boolean is = RSA.verify(sb, signData, xq_public_key, "UTF-8");
//		System.out.println(is);
//
//	}

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
			args.append("&priceUnit=CN");
			args.append("&source=" + orderInfo.getType());
			args.append("&goodsCount=1");
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
						logger.info("talkingData参数============" +orderInfo.getType()+"===RID="+orderInfo.getRoleId()+"==ORDERid="+orderInfo.getOrderId()+"="
								+ "total="+orderInfo.getTotalFee());
						YinNiPayController.talkingDataCharge(orderInfo.getType(), orderInfo.getRoleId(),
								orderInfo.getOrderId(), Double.parseDouble(orderInfo.getTotalFee()), "CN",
								Double.parseDouble(orderInfo.getTotalFee()), "ZS", "GN", "GAME101",
								"024880CF069441D68317BD9186F4C432");
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
//		String d ="6.00";
//		if(Double.parseDouble(d)*100 >= 600.0)
//		{
//			System.out.println(Double.parseDouble(d)*100);	
//		}
//
		YinNiPayController.talkingDataCharge("XQ_IOS", "37104228_552",
"3710412_300", Double.parseDouble("600"), "CN",
				Double.parseDouble("600"), "ZS", "GN", "GAME101",
				"024880CF069441D68317BD9186F4C432");
		
	}
	
}
