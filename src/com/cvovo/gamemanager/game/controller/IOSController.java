package com.cvovo.gamemanager.game.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.persist.entity.IosOrderInfo;
import com.cvovo.gamemanager.game.persist.entity.ReceiptInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.IosOrderService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.test.IOS_Verify;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class IOSController {
	private static final Logger logger = LoggerFactory.getLogger(IOSController.class);
	@Autowired
	private IosOrderService iosOrdeService;
	@Autowired
	private ServerService serverService;
	static String flag_A = "APPLE_VALID";
	static String flag_B = "APPLE_VALID_OLD";
	static String flag_C = "VALID_BEFORE";
	static String flag_D = "NO_APPLE_VALID";
	public static String SANDBOX_VERIFICATION_SERVER = "Sandbox";

	private static final Map<String, String> shopItem = new HashMap<String, String>();
	static {
		shopItem.put("com.haichuang.fc.gp.01", "4.99");
		shopItem.put("com.haichuang.fc.gp.02", "14.99");
		shopItem.put("com.haichuang.fc.gp.03", "99.99");
		shopItem.put("com.haichuang.fc.gp.04", "49.99");
		shopItem.put("com.haichuang.fc.gp.05", "19.99");
		shopItem.put("com.haichuang.fc.gp.06", "9.99");
		shopItem.put("com.haichuang.fc.gp.07", "4.99");
		shopItem.put("com.haichuang.fc.gp.08", "2.99");
	}
	@RequestMapping(value = "iosReceipt", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult IOSVerify(String receipt, String sandbox, String serverId, String roleId) throws Exception {
		logger.info("===sandbox===" + sandbox);
		logger.info("===serverId===" + serverId);
		logger.info("===roleId===" + roleId);
		List<ReceiptInfo> list = new ArrayList<ReceiptInfo>();
		String[] receiptStr = receipt.split(",");
		String[] serverStr = serverId.split(",");
		String[] roleIdStr = roleId.split(",");
		for (int i = 0; i < receiptStr.length; i++) {
			ReceiptInfo receiptInfo = new ReceiptInfo();
			receiptInfo.setReceipt(receiptStr[i]);
			receiptInfo.setRoleId(roleIdStr[i]);
			receiptInfo.setServerid(serverStr[i]);
			list.add(receiptInfo);
		}

		logger.info("===启动验证===");
		for (int i = 0; i < list.size(); i++) {
			ReceiptInfo tempInfo = list.get(0);
			tempInfo.setType("IOS");
			String md5_receipt = null;
			try {
				md5_receipt = MD5.GetMD5Code(tempInfo.getReceipt());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = flag_C + "#" + md5_receipt;

			IosOrderInfo ioi = new IosOrderInfo();

			ioi.setCreateOrderDate(new Timestamp(System.currentTimeMillis()));
			ioi.setServerId(tempInfo.getServerid());
			ioi.setRoleId(tempInfo.getRoleId());
			ioi.setStatus("-99");
			ioi.setDeveloperPayload(md5_receipt);
			ioi.setType(tempInfo.getType());

			String verifyResult = IOS_Verify.buyAppVerify(tempInfo.getReceipt(), sandbox);
			logger.info("verifyResult===============" + verifyResult);
			if (verifyResult == null) {
				// 苹果服务器没有返回验证结果
				result = flag_D + "#" + md5_receipt;
			} else {
				// 跟苹果验证有返回结果------------------
				JSONObject job = JSONObject.fromObject(verifyResult);
				String states = job.getString("status");
				ioi.setStatus(states);
				logger.info("states===============" + states);
				if (states.equals("0"))// 验证成功
				{

					JSONObject inapp =job.getJSONObject("receipt").getJSONArray("in_app").getJSONObject(0);
					
					
					String original_transaction_id = inapp.getString("original_transaction_id");
					logger.info("original_transaction_id===============" + original_transaction_id);
					// 产品ID
					String product_id = inapp.getString("product_id");
					logger.info("product_id===============" + product_id);
					// 数量
					String quantity = inapp.getString("quantity");
					logger.info("quantity===============" + quantity);
					if (!iosOrdeService.findByIosOrderByMD5(original_transaction_id))// 数据库效验)
					{
						logger.info("数据库效验===============失败");
						result = flag_B + "#" + original_transaction_id;
						return RestResult.receipt(receipt);
					}
					// 跟苹果的服务器验证成功
					result = flag_A + "#" + md5_receipt + "_" + product_id + "_" + quantity;
					logger.info("result==============="+result);
					// 交易日期
					String purchase_date = inapp.getString("purchase_date");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = null;
					try {
						date = sdf.parse(purchase_date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 保存到数据库

					ioi.setMd5Receipt(original_transaction_id);
					ioi.setProductId(product_id);
					ioi.setPurchaseDate(new Timestamp(date.getTime()));
					ioi.setStatus("2");
					logger.info("serverId ===============" + ioi.getServerId());
					iosOrdeService.save(ioi);
					logger.info("db===============end");
					if (doPostIOSRecharge(tempInfo.getServerid(), tempInfo.getRoleId(), ioi, "IOS")) {
						logger.info("r_receip============回调成功==============");
					}
				} else {
					result = flag_C + "#" + md5_receipt;
				}
				// 跟苹果验证有返回结果------------------
			}
			iosOrdeService.save(ioi);
			logger.info("订单结果==" + result + "================================================");
		}

		return RestResult.receipt(receipt);
	}

	public boolean doPostIOSRecharge(String serverId, String roleId, IosOrderInfo ioi, String type) {
		logger.info("======doPostIOSRecharge================");
		int id = Integer.parseInt(serverId);
		ServerInfo serverInfo = serverService.getServerInfo(id);
		if (serverInfo != null) {
			logger.info("成功获取服务器地址==============================");
			String url = serverInfo.getUrl();
			StringBuffer args = new StringBuffer("c=1004");
			args.append("&playerId=" + ioi.getRoleId());
			args.append("&shopId=" + ioi.getProductId());
			args.append("&order=" + ioi.getMd5Receipt());
			args.append("&source=" + type);
			args.append("&price=" + shopItem.get( ioi.getProductId()));
			args.append("&goodsCount=1");
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
						ioi.setStatus("3");
						YinNiPayController.talkingDataCharge(ioi.getType(),ioi.getRoleId(), 
								ioi.getMd5Receipt(), Double.parseDouble(shopItem.get(ioi.getProductId())),
								"USD",Double.parseDouble(shopItem.get(ioi.getProductId())), 
								"DNY", "DNY", "GAME101","6D25C704ADAF48BAB5AD00589D543199");
						iosOrdeService.save(ioi);
						return true;
					}
				}
			} catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	@RequestMapping(value = "testIos", method = { RequestMethod.GET, RequestMethod.POST })
	public void testT(String sd) {
		System.out.println(iosOrdeService.findByIosOrderByMD5(sd));
	}
		public static void main(String[] args) throws JSONException {
//			String str ="\r\n" + 
//					"{\"receipt\":{\"receipt_type\":\"ProductionSandbox\", \"adam_id\":0, \"app_item_id\":0, \r\n" + 
//					"\"bundle_id\":\"com.haichuang.jcy.ios\", \"application_version\":\"1\", \"download_id\":0, \r\n" + 
//					"\"version_external_identifier\":0, \"receipt_creation_date\":\"2018-11-15 11:00:42 Etc/GMT\",\r\n" + 
//					"\"receipt_creation_date_ms\":\"1542279642000\", \"receipt_creation_date_pst\":\"2018-11-15 03:00:42 America/Los_Angeles\",\r\n" + 
//					"\"request_date\":\"2018-11-15 11:00:54 Etc/GMT\", \"request_date_ms\":\"1542279654106\", \"request_date_pst\":\"2018-11-15 03:00:54 America/Los_Angeles\", \"\r\n" + 
//					"original_purchase_date\":\"2013-08-01 07:00:00 Etc/GMT\", \"original_purchase_date_ms\":\"1375340400000\", \"original_purchase_date_pst\":\"2013-08-01 00:00:00 America/Los_Angeles\", \r\n" + 
//					"\"original_application_version\":\"1.0\", \"in_app\":[{\"quantity\":\"1\", \"product_id\":\"com.haichuang.jcy.gp.03\", \"transaction_id\":\"1000000473261118\", \"original_transaction_id\":\"1000000473261118\", \r\n" + 
//					"\"purchase_date\":\"2018-11-15 11:00:42 Etc/GMT\", \"purchase_date_ms\":\"1542279642000\", \"purchase_date_pst\":\"2018-11-15 03:00:42 America/Los_Angeles\", \"original_purchase_date\":\"2018-11-15 11:00:42 Etc/GMT\", \r\n" + 
//					"\"original_purchase_date_ms\":\"1542279642000\", \"original_purchase_date_pst\":\"2018-11-15 03:00:42 America/Los_Angeles\", \"is_trial_period\":\"false\"}]}, \"status\":0, \"environment\":\"Sandbox\"}";
//
//			org.json.JSONObject returnJson = new org.json.JSONObject(str);
//			org.json.JSONObject js =returnJson.getJSONObject("receipt").getJSONArray("in_app").getJSONObject(0);
//			//
			double dd =Double.parseDouble(shopItem.get("com.haichuang.jcy.gp.02"));
			System.out.println(dd);
 
			
			
		}
	
}
