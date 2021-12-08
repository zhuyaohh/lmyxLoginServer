package com.cvovo.gamemanager.game.controller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cvovo.gamemanager.game.alipay.Base64;
import com.cvovo.gamemanager.game.persist.entity.Account;
import com.cvovo.gamemanager.game.persist.entity.CheckVersion;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.service.AccountService;
import com.cvovo.gamemanager.game.service.CheckService;
import com.cvovo.gamemanager.game.service.DateQuest;
import com.cvovo.gamemanager.game.service.DeviceNoService;
import com.cvovo.gamemanager.game.service.FacebookAuthService;
import com.cvovo.gamemanager.game.service.RandomRankConfigService;
import com.cvovo.gamemanager.game.service.ServerService;
import com.cvovo.gamemanager.util.MD5;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@RestController
@RequestMapping("/")
public class IndexController {
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	@Autowired
	private DateQuest da;
	@Autowired
	private RandomRankConfigService randomRankConfigService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private ServerService serverService;
	@Autowired
	private FacebookAuthService faceBookAuth;
	@Autowired
	private CheckService checkService;
	@Autowired
	private DeviceNoService deviceNoService;
	@Autowired
	private DateQuest dq;

	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

		} };
		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String HttpPost(String URL, String param) {
		byte[] postData = param.getBytes();
		InputStream instr = null;
		try {
			URL url = new URL(URL);
			trustAllHosts();
			HttpsURLConnection urlCon = (HttpsURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestProperty("content-Type", "application/x-www-form-urlencoded");
			urlCon.setRequestProperty("charset", "utf-8");
			urlCon.setRequestProperty("Content-length", String.valueOf(postData.length));
			DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
			printout.write(postData);
			printout.flush();
			printout.close();
			instr = urlCon.getInputStream();
			int len = instr.available();
			byte[] bis = new byte[len];
			instr.read(bis, 0, len);
			String ResponseString = new String(bis, "UTF-8");
			return ResponseString;

		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		} finally {
			try {
				instr.close();

			} catch (Exception ex) {
				return "0";
			}
		}
	}

	@RequestMapping(value = "ckLogin", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginByChuangKu(String uid, String token, String device, String channelId, String gameId, String check, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {
		// String checkLoginUrl = "http://ol.haifurong.cn/ck/online/game/loginValidate";
		String checkLoginUrl = "http://ol.gzndhk.comnd/online/game/loginValidate";
		int flag = 0;
		if (checkService.findCheckByStr(check) != null) {
			flag = 1;
		} else if (checkService.findCheckByStr(uid) != null) {
			flag = 3;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uid);
		}
		boolean pass = true;
		org.json.JSONObject jsonObject = new JSONObject();
		jsonObject.append("uid", uid);
		jsonObject.append("token", token);
		jsonObject.append("channelId", channelId);
		jsonObject.append("gameId", gameId);
		String sResponseFromServer = HttpPost(checkLoginUrl, jsonObject.toString());
		if (sResponseFromServer != null && sResponseFromServer.indexOf("\"resultCode\":1") == 0) {
			pass = true;
		} else {
			logger.info("sResponseFromServer:" + sResponseFromServer);
			pass = false;
		}
		if (!pass)
			return RestResult.FAILURE_SDK_ERROR;
		Account account = accountService.findByUin(uid);
		if (account != null) {
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.updateAccount(account);
		} else {
			account = new Account(uid, PublicMethod.getUniqueId(), "", "", "chuagku", device);
			account.setNick("chuagku");
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.createYYB(account);
		}
		return this.serverService.getServerList(uid, flag);
	}

	@RequestMapping(value = "HTBT", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginByBT(String uid, String accessToken, String device, String check, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {

		int flag = 0;
		if (checkService.findCheckByStr(check) != null) {
			flag = 1;
		} else if (checkService.findCheckByStr(uid) != null) {
			flag = 3;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uid);
		}
		boolean pass = true;
		String sResponseFromServer = HttpPost(uid, accessToken);
		if (sResponseFromServer != null && sResponseFromServer.indexOf("\"code\":1") == 0) {
			pass = true;
		} else {
			logger.info("sResponseFromServer:" + sResponseFromServer);
			pass = false;
		}
		if (!pass)
			return RestResult.FAILURE_SDK_ERROR;
		Account account = accountService.findByUin(uid);
		if (account != null) {
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.updateAccount(account);
		} else {
			account = new Account(uid, PublicMethod.getUniqueId(), "", "", "ht", device);
			account.setNick("ht");
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.createYYB(account);
		}
		return this.serverService.getServerList(uid, flag);
	}

	@RequestMapping(value = "validtokenBT996ios", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginByBT996(String accessToken, String device, String check, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {
		String ip = getIpAddress(request);
		logger.info(String.format("validtokenBT996ios--->device:{%s},check:{%s},accessToken{%s},ip:{%s}", device, check, accessToken, ip));
		if (device != null && !device.equals("") && deviceNoService.checkDevice(device)) {
			return RestResult.FAILURE_USE_OR_PWD_ERROR;
		}
		String[] str = accessToken.split(",");
		Hashtable<String, String> table = new Hashtable<String, String>();
		for (int i = 0; i < str.length; i += 2) {
			String key = str[i];
			String value = str[i + 1];
			table.put(key, value);
		}
		String uin = table.get("userId");// userId
		String token = table.get("token");// token
		int flag = 0;
		if (checkService.findCheckByStr(check) != null) {
			flag = 1;
		} else if (checkService.findCheckByStr(uin) != null) {
			flag = 3;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uin);
		}
		boolean pass = true;
		if (table.containsKey("type")) {
			String type = table.get("type");
			if (type.equalsIgnoreCase("31game784")) {
				String App_Id = "784";
				String Login_Key = "a1645b2ed4802b171d3bd4692eb82915";
				String Login_Url = "https://api.nkyouxi.com/sdkapi.php";
				try {
					int time = (int) (Calendar.getInstance().getTimeInMillis() / 1000);
					String param = String.format("ac=check&appid=%s&sdkversion=2.1.6&sessionid=%s&time=%s", App_Id, URLEncoder.encode(token, "utf-8"), time);
					String code = String.format("ac=check&appid=%s&sdkversion=2.1.6&sessionid=%s&time=%s%s", App_Id, URLEncoder.encode(token, "utf-8"), time, Login_Key);
					String sign = MD5.GetMD5Code(code);
					param += "&sign=" + sign;
					String sResponseFromServer = HttpPost(Login_Url, param);
					if (sResponseFromServer != null && sResponseFromServer.indexOf("\"code\":1") >= 0) {
						pass = true;
					} else {
						logger.info("sResponseFromServer:" + sResponseFromServer);
						pass = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					pass = false;
				}
			} else if (type.equalsIgnoreCase("31game783")) {
				String App_Id = "783";
				String Login_Key = "ac73e19617c21f5e1334c6cfc4717de6 ";
				String Login_Url = "https://api.nkyouxi.com/sdkapi.php";
				try {
					int time = (int) (Calendar.getInstance().getTimeInMillis() / 1000);
					String param = String.format("ac=check&appid=%s&sdkversion=2.1.6&sessionid=%s&time=%s", App_Id, URLEncoder.encode(token, "utf-8"), time);
					String code = String.format("ac=check&appid=%s&sdkversion=2.1.6&sessionid=%s&time=%s%s", App_Id, URLEncoder.encode(token, "utf-8"), time, Login_Key);
					String sign = MD5.GetMD5Code(code);
					param += "&sign=" + sign;
					String sResponseFromServer = HttpPost(Login_Url, param);
					if (sResponseFromServer != null && sResponseFromServer.indexOf("\"code\":1") >= 0) {
						pass = true;
					} else {
						logger.info("sResponseFromServer:" + sResponseFromServer);
						pass = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					pass = false;
				}
			}
		}
		if (!pass)
			return RestResult.FAILURE_SDK_ERROR;
		Account account = accountService.findByUin(uin);
		if (account != null) {
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.updateAccount(account);
		} else {
			account = new Account(uin, PublicMethod.getUniqueId(), "", "", "bt996", device);
			account.setNick("bt996");
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.createYYB(account);
		}
		return this.serverService.getServerList(uin, flag);
	}

	@RequestMapping(value = "validtokenYJ", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginByYJSDK(String sdk, String app, String uin, String sess, String device, String check, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {
		String ip = getIpAddress(request);
		logger.info(String.format("validtokenYJ--->device:{%s},check:{%s},sdk{%s},app:{%s},uin:{%s},sess:{%s},ip:{%s}", device, check, sdk, app, uin, sess, ip));
		if (device != null && !device.equals("") && deviceNoService.checkDevice(device)) {
			return RestResult.FAILURE_USE_OR_PWD_ERROR;
		}
		int flag = 0;
		if (checkService.findCheckByStr(check) != null) {
			flag = 1;
		} else if (checkService.findCheckByStr(uin) != null) {
			flag = 3;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uin);
		}
		String arg = "sdk=" + URLEncoder.encode(sdk, "UTF-8") + "&app=" + URLEncoder.encode(app, "UTF-8") + "&uin=" + URLEncoder.encode(uin, "UTF-8") + "&sess=" + URLEncoder.encode(sess, "UTF-8");
		String returnValue = MyHttpClient.sendGet("http://sync.1sdk.cn/login/check.html", arg);
		// if (!returnValue.equals("0")) {
		logger.info("returnValue=" + returnValue + "---------------------------");
		// return RestResult.FAILURE_SDK_ERROR;
		// }
		Account account = accountService.findByUin(uin);
		if (account != null) {
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.updateAccount(account);
		} else {
			account = new Account(uin, PublicMethod.getUniqueId(), "", "", "yjsdk", device);
			account.setNick("yjsdk");
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.createYYB(account);
		}
		return this.serverService.getServerList(uin, flag);
	}

	@RequestMapping(value = "validtoken", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginByCN(String type, String device, String check, String uin, String code, String time, String level, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {
		String ip = getIpAddress(request);
		logger.info(String.format("type:{%s},device:{%s},check:{%s},uin{%s},code:{%s},time:{%s},level:{%s},ip:{%s}", type, device, check, uin, code, time, level, ip));
		if (device != null && !device.equals("") && deviceNoService.checkDevice(device)) {
			return RestResult.FAILURE_USE_OR_PWD_ERROR;
		}
		int flag = 0;
		if (checkService.findCheckByStr(check) != null || checkService.findCheckByStr(uin) != null) {
			flag = 1;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uin);
		}
		try {
			code = URLDecoder.decode(code, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			uin = URLDecoder.decode(uin, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		switch (type) {
		case "QSCS_Android":
		case "QSCS_IOS": {
			String url = "http://platform.btwangame.com/Adapter/CheckUserInfo";
			String param = "token=" + code + "&product_code=20190708001" + "&uid=" + uin + "&channel_code=" + level;
			String result = HttpRequest.sendGet(url, param);
			if (!result.equals("1")) {
				logger.info("QSCS login:" + result.toString());
				return RestResult.FAILURE_SDK_ERROR;
			}
			if (level.equals("1203"))
				level = "1";
			uin = level + "_" + uin;
		}
			break;

		case "GAT_HR_A":
		case "GAT_HR_I": {
			// String url = "https://testapi.hrgame.com.hk/v3/user/loginAuth";// Test
			String url = "https://api.hrgame.com.hk/v3/user/loginAuth";
			String sign = MD5.GetMD5Code(code + "|" + HuoRongController.APPID + "|" + HuoRongController.KEY);
			String param = "token=" + code + "&app_id=" + HuoRongController.APPID + "&sign=" + sign;
			JSONObject result1 = new JSONObject(HttpRequest.sendPost(url, param));
			if (result1.isNull("code") || result1.getInt("code") != 0) {
				logger.info("GAT_HR login:" + result1.toString());
				return RestResult.FAILURE_SDK_ERROR;
			}
		}
			break;
		case "IOS_CK":
		case "Android_CK":
			String checkLoginUrl = "http://ol.gzndhk.comnd/online/game/loginValidate";
			boolean pass = true;
			org.json.JSONObject jsonObject = new JSONObject();
			jsonObject.append("uid", uin);
			jsonObject.append("token", code);
			jsonObject.append("channelId", level);
			jsonObject.append("gameId", time);
			if (!pass)
				return RestResult.FAILURE_SDK_ERROR;
			Account account = accountService.findByUin(uin);
			if (account != null) {
				account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
				accountService.updateAccount(account);
			} else {
				account = new Account(uin, PublicMethod.getUniqueId(), "", "", type, device);
				account.setNick("chuagku");
				account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
				accountService.createYYB(account);
			}
			return this.serverService.getServerList(uin, flag);
		case "TG_I":
		case "TG_A": {
			String tgHttpURL = "https://sdk.joy8899.com/sdk/verifySession";
			JSONObject data = new JSONObject();
			data.put("appId", "88021");
			data.put("sid", uin);
			data.put("sign", MD5.GetMD5Code(String.format("appId=88021&sid=%s||29a49eec9ad1281a2e799e", uin)));
			JSONObject result = new JSONObject(MyHttpClient.sendPost(tgHttpURL, data.toString()));
			if (result.isNull("status") || result.getInt("status") != 0) {
				return RestResult.FAILURE_SDK_ERROR;
			}
			uin = result.getString("uid");
		}
			break;
		case "TG2_I":
		case "TG2_A": {
			String tgHttpURL = "https://sdk.joy8899.com/sdk/verifySession";
			JSONObject data = new JSONObject();
			data.put("appId", "88034");
			data.put("sid", uin);
			data.put("sign", MD5.GetMD5Code(String.format("appId=88034&sid=%s||f7648c0f82f2d78f57341c", uin)));
			JSONObject result = new JSONObject(MyHttpClient.sendPost(tgHttpURL, data.toString()));
			if (result.isNull("status") || result.getInt("status") != 0) {
				return RestResult.FAILURE_SDK_ERROR;
			}
			uin = result.getString("uid");
		}
			break;
		case "GS_OM_A":
			return oMLogin("2124929491168212", uin, code, type, device, device, flag);
		case "GS_OM_I":
			return oMLogin("2124929491168212", uin, code, type, device, device, flag);
		case "GAT_IOS":
			String ck = level;
			String checkStr = MD5.GetMD5Code(code + time + "Qhvmv8wPOTRxzCzW&3mN" + uin);
			logger.info("==============checkStr==================" + checkStr);
			if (!ck.equals(checkStr)) {
				return RestResult.FAILURE_USE_OR_PWD_ERROR;
			}
			break;
		case "JD_BC_I":
			try {
				JSONObject data = new JSONObject(code);
				StringBuffer sb = new StringBuffer();
				String headurl = data.getString("headurl");
				String nickname = data.getString("nickname");
				String token = data.getString("token");
				String data_time = data.getString("time");
				String user_id = data.getString("user_id");
				String sign = data.getString("sign");
				uin = user_id;

				if (headurl != null && !headurl.isEmpty()) {
					sb.append("headurl=" + headurl + "&");
				}
				if (nickname != null && !nickname.isEmpty()) {
					sb.append("nickname=" + nickname + "&");
				}
				if (data_time != null && !data_time.isEmpty()) {
					sb.append("time=" + data_time + "&");
				}
				if (token != null && !token.isEmpty()) {
					sb.append("token=" + token + "&");
				}
				if (user_id != null && !user_id.isEmpty()) {
					sb.append("user_id=" + user_id);
				}
				sb.append("b2382203c63475a4600d12f0dc4c8710");
				String mySign = MD5.GetMD5Code(sb.toString().toLowerCase());
				if (!mySign.equals(sign)) {
					return RestResult.FAILURE_SDK_ERROR;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return RestResult.FAILURE_SDK_ERROR;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return RestResult.FAILURE_SDK_ERROR;
			}
			break;
		case "JD_BC": {
			try {
				JSONObject data = new JSONObject(code);
				StringBuffer sb = new StringBuffer();
				String channel = data.getString("channel");
				String channel_flag = data.getString("channel_flag");
				String channel_headurl = data.getString("channel_headurl");
				String channel_openid = data.getString("channel_openid");
				String channel_nickname = data.getString("channel_nickname");
				String token = data.getString("token");
				String data_time = data.getString("time");
				String user_id = data.getString("user_id");
				String sign = data.getString("sign");
				uin = user_id;
				if (channel != null && !channel.isEmpty()) {
					sb.append("channel=" + channel + "&");
				}
				if (channel_flag != null && !channel_flag.isEmpty()) {
					sb.append("channel_flag=" + channel_flag + "&");
				}
				if (channel_headurl != null && !channel_headurl.isEmpty()) {
					sb.append("channel_headurl=" + channel_headurl + "&");
				}
				if (channel_nickname != null && !channel_nickname.isEmpty()) {
					sb.append("channel_nickname=" + channel_nickname + "&");
				}
				if (channel_openid != null && !channel_openid.isEmpty()) {
					sb.append("channel_openid=" + channel_openid + "&");
				}
				if (data_time != null && !data_time.isEmpty()) {
					sb.append("time=" + data_time + "&");
				}
				if (token != null && !token.isEmpty()) {
					sb.append("token=" + token + "&");
				}
				if (user_id != null && !user_id.isEmpty()) {
					sb.append("user_id=" + user_id);
				}
				sb.append("427a4b8f996f11064756edbc9fa97f91");
				String mySign = MD5.GetMD5Code(sb.toString().toLowerCase());
				if (!mySign.equals(sign)) {
					return RestResult.FAILURE_SDK_ERROR;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return RestResult.FAILURE_SDK_ERROR;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return RestResult.FAILURE_SDK_ERROR;
			}
		}
			break;
		default:
			return RestResult.FAILURE_SDK_ERROR;
		}
		Account account = accountService.findByUin(uin);
		if (account != null) {
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.updateAccount(account);
		} else {
			account = new Account(uin, PublicMethod.getUniqueId(), "", "", type, device);
			account.setNick(type + "_d");
			account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
			accountService.createYYB(account);
		}
		return this.serverService.getServerList(uin, flag);
	}

	@RequestMapping(value = "test", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getdoubi(String accessToken) {
		dq.testG();
		return RestResult.result(1);
	}

	@RequestMapping(value = "getAllServer", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getAllServerList(String token) {
		return serverService.getAllServerList();
	}

	@RequestMapping(value = "closeServerList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getTestServerList(String accessToken) {
		CheckVersion cv = checkService.findCheckByStr("closeServer");
		if (cv == null) {
			checkService.saveCheck(new CheckVersion("closeServer"));
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "openServerList", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getOpenServerList(String accessToken) {
		if (accessToken == null || accessToken.equals("")) {
			accessToken = "closeServer";
		}
		checkService.deleteCheck(accessToken);
		return RestResult.result(1);
	}

	@RequestMapping(value = "testYYB", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult testYYB() {
		String uid = "B296F398B13E02D6739986D4D7A2AFA7";
		Account account = new Account(uid, PublicMethod.getUniqueId(), "", "AN", "GSDF", "");
		try {
			logger.info("============entry==============");
			accountService.createYYB(account);
		} catch (Exception e) {
			logger.info(e.toString());
			return RestResult.result(0);
		}
		return RestResult.result(1);
	}

	@RequestMapping(value = "testFb", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult testFb(String test) {
		String uin = "1634718643510591";
		String type = "HG_Android";
		Account account = new Account();
		account.setFbUserId(uin);
		account.setsUid(PublicMethod.getUniqueId());
		account.setType(type);

		accountService.updateAccount(account);
		return RestResult.result(1);
	}

	@RequestMapping(value = "validtokenHGLY", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginHGLY(String accessToken, String device, String check, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {

		String ip = getIpAddress(request);
		logger.info(String.format("validtokenHGLY{%s},device:{%s},ip:{%s},check:{%s}", accessToken, device, ip, check));
		JSONObject data = new JSONObject(accessToken);
		if (device != null && !device.equals("") && deviceNoService.checkDevice(device)) {
			return RestResult.FAILURE_USE_OR_PWD_ERROR;
		}
		String code = data.get("access_token").toString();
		String uin = data.get("user_id").toString();
		logger.info("uin=======" + uin + "=====================");
		logger.info("code======" + code + "=====================");
		int flag = 0;
		if (checkService.findCheckByStr(check) != null || checkService.findCheckByStr(uin) != null) {
			flag = 1;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uin);
		}
		try {
			code = URLDecoder.decode(code, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return faceBookAuth.checkLoginWithToken(code, "HGLY", uin, flag);
	}

	@RequestMapping(value = "validtokenLY", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginLY(String accessToken, String device, String check, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {

		String ip = getIpAddress(request);
		logger.info(String.format("validtokenLY{%s},device:{%s},ip:{%s},check:{%s}", accessToken, device, ip, check));
		JSONObject data = new JSONObject(accessToken);
		if (device != null && !device.equals("") && deviceNoService.checkDevice(device)) {
			return RestResult.FAILURE_USE_OR_PWD_ERROR;
		}
		String code = data.get("access_token").toString();
		String uin = data.get("user_id").toString();
		logger.info("uin=======" + uin + "=====================");
		logger.info("code======" + code + "=====================");
		int flag = 0;
		if (checkService.findCheckByStr(check) != null || checkService.findCheckByStr(uin) != null) {
			flag = 1;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uin);
		}
		try {
			code = URLDecoder.decode(code, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return faceBookAuth.checkLoginWithToken(code, "LY", uin, flag);
	}

	@RequestMapping(value = "validtokenOld", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLogin(String accessToken, String device, String check, HttpServletRequest request) throws JSONException, UnsupportedEncodingException {
		String ip = getIpAddress(request);
		logger.info(String.format("validtokenOld:accessToken{%s},device:{%s},ip:{%s}", accessToken, check, ip));
		String[] str = accessToken.split("&");
		Hashtable<String, String> table = new Hashtable<String, String>();
		for (int i = 0; i < str.length; i++) {
			String tempStr = str[i];
			int d = tempStr.indexOf("=");
			String key = tempStr.substring(0, d);
			String value = tempStr.substring(d + 1, tempStr.length());
			table.put(key, value);
		}
		if (device != null && !device.equals("") && deviceNoService.checkDevice(device)) {
			return RestResult.FAILURE_USE_OR_PWD_ERROR;
		}
		String code = table.get("code");// open_key
		String type = table.get("type");// OM2_IOS
		String channel = table.containsKey("channel") ? table.get("channel") : "";// OM2_IOS
		String uin = table.get("uin");// open_id
		String ext1 = table.get("ext1");
		logger.info(String.format("ext1{%s},code:{%s},type:{%s}", ext1, code, type));
		int flag = 0;
		if (checkService.findCheckByStr(check) != null || checkService.findCheckByStr(uin) != null) {
			flag = 1;
		}
		CheckVersion t = checkService.findCheckByStr("closeServer");
		if (t != null && t.getCheckStr().equalsIgnoreCase("closeServer")) {
			return serverService.changeServerStatus(uin);
		}
		if (type.equalsIgnoreCase("XQ_SDK")) {
			String appKey = "751102b77cdd227f1d2dcef03715532f";
			String url = "https://api.x7sy.com/user/check_v4_login";
			StringBuffer sb = new StringBuffer("tokenkey=");
			sb.append(code + "&sign=");
			sb.append(MD5.GetMD5Code(new StringBuffer(appKey).append(code).toString().toLowerCase()));

			String result = HttpRequest.sendPost(url, sb.toString());

			JSONObject jsonObject = new JSONObject(result);
			logger.info("=====result=========" + result);
			if (jsonObject.getInt("errorno") == 0) {
				JSONObject jo = (JSONObject) jsonObject.get("data");
				String guid = jo.getString("guid");
				String username = jo.getString("username");
				String is_real_user = jo.getString("is_real_user");
				String is_eighteen = jo.getString("is_eighteen");
				Account account = new Account(guid, PublicMethod.getUniqueId(), "", "", type, device);
				account.setNick(username);
				account.setSex(is_real_user);
				account.setMail(is_eighteen);
				account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
				accountService.createYYB(account);
				return this.serverService.getServerList(guid, flag);

			} else {
				String msg = jsonObject.getString("errormsg");
				logger.info("=====msg==========" + msg);
			}
		}
		if (type.equalsIgnoreCase("QuickSDK")) {
			String product_code = "87229808166382745213630291787595";
			String url = "http://checkuser.sdk.quicksdk.net/v2/checkUserInfo";
			String data = String.format("token=%s&uid=%s&product_code=%s", code, uin, product_code);
			String result = HttpRequest.sendPost(url, data);
			if (result.equals("1")) {
				Account account = new Account(uin, PublicMethod.getUniqueId(), "", "", type, device);
				account.setNick("gn");
				account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
				accountService.createYYB(account);
				return this.serverService.getServerList(uin, flag);
			}

		}
		if (type.equalsIgnoreCase("LKL_IOS") || type.equalsIgnoreCase("LKL_ANDROID")) {
			logger.info("=====LKL_IOS=============");
			String url = "https://www.xphlyx.com/sdk.php/LoginNotify/login_verify";

			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put("user_id", uin);
			jsonObject1.put("token", code);
			logger.info(url + "====args==" + jsonObject1.toString());
			String result = HttpRequest.sendPost(url, jsonObject1.toString());
			logger.info("===" + result);
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.getInt("status") == 1) {
				Account account = new Account(uin, PublicMethod.getUniqueId(), "", "", type, device);
				account.setNick("gn");
				account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
				accountService.createYYB(account);
				return this.serverService.getServerList(uin, flag);
			}

		}
		if (type.equalsIgnoreCase("XPHL_IOS")) {
			logger.info(String.format("ext1{%s},code:{%s},type:{%s}", ext1, code, type));
			String SECRETKEY = "bGlnS3RieFE=";
			String CP_ID = "1041";
			String SERVERURL = "http://game.9so123.com/core/service/checkonlineuser.html";

			String key = new String(Base64.decode(SECRETKEY), "utf-8");
			StringBuffer sb = new StringBuffer("cpid=");
			sb.append(CP_ID + "&");
			sb.append("ticket=" + code + "&userid=" + uin);
			sb.append(key);
			StringBuffer sb1 = new StringBuffer("cpid=");
			sb1.append(CP_ID);
			sb1.append("&ticket=" + code + "");
			sb1.append("&userid=" + uin);
			sb1.append("&sign=" + MD5.GetMD5Code(sb.toString()));
			String result = HttpRequest.sendPost(SERVERURL, sb1.toString());
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.getBoolean("businessResult")) {
				Account account = accountService.findByUin(uin);
				if (account != null) {
					account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
					accountService.updateAccount(account);
				} else {
					account = new Account(uin, PublicMethod.getUniqueId(), "", "", type, device);
					account.setNick("chuagku");
					account.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
					accountService.createYYB(account);
				}
				return this.serverService.getServerList(uin, flag);
			}

		}

		if (type.equalsIgnoreCase("KJ_IOS") || channel.equalsIgnoreCase("KJ_IOS")) {
			String appKey = "467f565add20ed465843c9c9404e0e0e";
			String url = "http://api.kaijia.com/sdkapi.php";
			String appid = URLEncoder.encode("87", "utf-8");
			String sdkversion = URLEncoder.encode(ext1, "utf-8");
			String sessionid = URLEncoder.encode(code.replaceAll(" ", "+"), "utf-8");
			String time = URLEncoder.encode("" + System.currentTimeMillis() / 1000, "utf-8");
			String data = String.format("ac=check&appid=%s&sdkversion=%s&sessionid=%s&time=%s", appid, sdkversion, sessionid, time);
			String sign = MD5.GetMD5Code(data + appKey);
			data = data + "&sign=" + sign;
			String result = HttpRequest.sendPost(url, data);
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.getInt("code") == 1) {
				return this.serverService.getServerList(uin, flag);
			}
			logger.error("result:" + result);
		}
		if (type.equalsIgnoreCase("HT_IOS") || channel.equalsIgnoreCase("HT_IOS") || type.equalsIgnoreCase("HT_ANDROID") || channel.equalsIgnoreCase("HT_ANDROID")) {
			String url = "http://smi.51sfsy.com/mwqy/auth";
			String uid = URLEncoder.encode(uin, "utf-8");
			String token = URLEncoder.encode(code, "utf-8");
			String result = HttpRequest.sendGet(url, String.format("uid=%s&token=%s", uid, token));
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.getInt("errno") == 0) {
				return this.serverService.getServerList(uin, flag);
			}
			logger.error("HT_SDK result:" + result);
		}

		if (type.equalsIgnoreCase("YN_ANDROID") || type.equalsIgnoreCase("YN_IOS")) {
			return this.serverService.getServerList(uin, flag);
		}
		return RestResult.FAILURE_SDK_ERROR;
	}

	@RequestMapping(value = "register", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getLoginT(String usr, String pwd) {
		logger.info("usr===register=======" + usr);
		logger.info("pwd===register=======" + pwd);
		Account account = new Account();
		account.setNick(usr);
		account.setPassword("123456");
		account.setSex("1");
		account.setsUid(accountService.getsUserId());
		account.setUserId(11111);
		account.setCoinNft(0.00);
		account.setCoinWin(0.00);
		account.setType("PC");
		if (accountService.register(account)) {
			return RestResult.registerSuccess("账号创建成功");
		} else {
			return RestResult.registerSuccess("账号重复");
		}

	}

	@RequestMapping(value = "registerStroe", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getRegisterIOS(String usr, String pwd, String device) {
		logger.info("===registerStroe=======" + usr + "=========" + pwd + "=======" + device);
		Account account = new Account();
		if (usr.getBytes().length > 32 || usr.getBytes().length < 6) {
			return RestResult.failure();
		}
		if (usr.equals("") || pwd.equals("") || usr == null || pwd == null) {
			return RestResult.errorF();
		}
		account.setNick(usr);
		account.setPassword(pwd);
		account.setSex("1");
		account.setsUid(PublicMethod.getUniqueId());
		account.setUserId(11111);
		account.setCoinNft(0.00);
		account.setCoinWin(0.00);
		account.setType("IOS");
		if (device != null && !device.endsWith("")) {
			account.setDevice(device);
		}

		if (accountService.registerT(account)) {
			return RestResult.success();
		} else {
			return RestResult.failure();
		}

	}

	@RequestMapping(value = "bindUser", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getBingUser(String usr, String pwd, String uniqueId) {
		logger.info("===bindUser===usr==" + usr + "======pwd===" + pwd + "===uniqueId====" + uniqueId);
		Account exist = accountService.findByNick(uniqueId);
		if (exist != null) {
			Account exist1 = accountService.findByNick(usr);
			if (exist1 != null) {
				return RestResult.failure();
			}
			exist.setNick(usr);
			exist.setPassword(pwd);
			accountService.updateAccount(exist);
			return RestResult.success();
		}
		return RestResult.failure();

	}

	@RequestMapping(value = "bindSmUser", method = { RequestMethod.GET, RequestMethod.POST })
	public int getRegister(String visitorUsr, String fbUserId, String type, String code) {
		logger.info("===visitorUsr===" + visitorUsr + "======fbUserId===" + fbUserId + "===type====" + type);
		Account visitorAccount = accountService.getAccountBySuid(visitorUsr);
		if (visitorAccount != null) {
			logger.info("===visitorAccount===" + visitorAccount);
			visitorAccount.setType(type);
			visitorAccount.setFbUserId(fbUserId);
			visitorAccount.setNick("");
			accountService.bindFB(visitorAccount);
			return 0;
		}
		return 1;
	}

	/**
	 *
	 * @param usr 		昵称
	 * @param pwd		密码
	 * @param visitor	来访模式
	 * @param check
	 * @param device
	 * @return
	 */
	@RequestMapping(value = "login", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getRegister(String usr, String pwd, String visitor, String check, String device, String platform_id) {
		logger.info("===login=====");
		logger.info("===usr=====" + usr);
		logger.info("===pwd=====" + pwd);
		logger.info("===visitor=====" + visitor);
		logger.info("===check=====" + check);
		logger.info("===device=====" + device);

		int flag = 0;
		if (checkService.findCheckByStr(check) != null) {
			flag = 1;
		}
		if (checkService.findCheckByStr("zhifubao") != null) {
			flag = 2;
		}
		Account account = accountService.findByNick(usr);
		if (visitor != null && visitor.equals("1")) {
			logger.info("===visitor==true=====");
			if (account == null || account.getNick() == null || (account != null && account.getNick().equals(""))) {
				account = new Account();
				account.setNick(usr);
				account.setPassword(pwd);
				account.setSex("1");
				account.setsUid(PublicMethod.getUniqueId());
				account.setUserId(11111);
				account.setCoinNft(0.00);
				account.setCoinWin(0.00);
				account.setPlatformId(platform_id);
				account.setType("VISITOR");
			}
			account = accountService.createVisitor(account);
			return accountService.loginLy(account, flag, (byte) 0);
		}
		if(account != null){
			if (!account.getPassword().equals(account.getPassword())) {
				return RestResult.USE_OR_PWD_ERROR;
			}
		}

		return accountService.loginLy(account, flag, (byte) 1);
	}

	/**
	 * 根据s_uid获取account对象实例
	 * @param sUid
	 * @return
	 */
	@RequestMapping(value = "getAccountBySuid", method = { RequestMethod.GET, RequestMethod.POST })
	public HashMap<String, Object> getAccountBySuid(String sUid){
		Account account = accountService.getAccountBySuid(sUid);
		HashMap<String, Object> result = new HashMap<>();

		if(account != null){
			result.put("code", 200);
			result.put("msg", "获取成功");
			result.put("account", account);
		}else{
			result.put("code", 0);
			result.put("msg", "获取失败");
			result.put("account", null);
		}

		return result;
	}

	/**
	 * 根据sUid 设置该对象coinNft数量
	 * @param sUid
	 * @param coinNft
	 * @return
	 */
	@RequestMapping(value = "setCoinNftBySuid", method = { RequestMethod.GET, RequestMethod.POST })
	public HashMap<String, Object> setCoinNftBySuid(String sUid, Double coinNft){
		Account account = null;										// account实例
		HashMap<String, Object> result = new HashMap<>();			// 返回值
		int code = 200;												// 返回状态码
		String msg = "设置成功";										// 返回提示语

		while (true){

			if(sUid == null || sUid == ""){ code=1001; msg="sUid不能为空或null，也可能是loginServer根本没有接收到这个字段"; break;}
			if(coinNft == null ){ code=1002; msg="coinNft不能为空或null，也可能是loginServer根本没有接收到这个字段"; break;}

			account = accountService.getAccountBySuid(sUid);
			if(account==null){ code=2001; msg="没能根据这个sUid获取到account对象"; break; }

			// 更新nft数据
			account.setCoinNft(coinNft);
			accountService.updateAccount(account);

			break;
		}

		// 设置最终返回值
		result.put("code", code);
		result.put("msg", msg);
		result.put("account", account);

		return result;
	}

	/**
	 * 根据sUid 设置该对象coinWin数量
	 * @param sUid
	 * @param coinWin
	 * @return
	 */
	@RequestMapping(value = "setCoinWinBySuid", method = { RequestMethod.GET, RequestMethod.POST })
	public HashMap<String, Object> setCoinWinBySuid(String sUid, Double coinWin){
		Account account = null;										// account实例
		HashMap<String, Object> result = new HashMap<>();			// 返回值
		int code = 200;												// 返回状态码
		String msg = "设置成功";										// 返回提示语

		while (true){

			if(sUid == null || sUid == ""){ code=1001; msg="sUid不能为空或null，也可能是loginServer根本没有接收到这个字段"; break;}
			if(coinWin == null ){ code=1003; msg="coinWin不能为空或null，也可能是loginServer根本没有接收到这个字段"; break;}

			account = accountService.getAccountBySuid(sUid);
			if(account==null){ code=2001; msg="没能根据这个sUid获取到account对象"; break; }

			// 更新nft数据
			account.setCoinWin(coinWin);
			accountService.updateAccount(account);

			break;
		}

		// 设置最终返回值
		result.put("code", code);
		result.put("msg", msg);
		result.put("account", account);

		return result;
	}

	@RequestMapping(value = "servers", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getServerList(String userId) {
		return serverService.getServerList(userId);
	}


	/**
	 * 不知作用的接口
	 * GameServer已注释
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "check", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult checkType(String userId) {
		try {
			int accountd = Integer.parseInt(userId);
			return accountService.findAccountByUserId(accountd);
		} catch (Exception ex) {
			return RestResult.checkUserIdError();
		}

	}

	@RequestMapping(value = "stop", method = { RequestMethod.GET, RequestMethod.POST })
	public void stop() {

	}

	public RestResult doYingYongBao(String appId, String openid, String openkey, String type, String device, int flag, String channel) {
		String url = "http://ysdktest.qq.com/auth/qq_check_token";
		String appkey = "Bm7isfhd1yuEAZW0";
		if (!channel.equalsIgnoreCase("1")) {
			logger.info("==============YYB_ANDROID===============W===");
			appkey = "c3eeab5311de1579b7a9cd11cf24b290";
			url = "http://ysdktest.qq.com/auth/wx_check_token";
		}

		StringBuffer tempSign = new StringBuffer();
		tempSign.append(appkey);
		tempSign.append(System.currentTimeMillis() / 1000);

		String sign = "";
		try {
			sign = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringBuffer contents = new StringBuffer("timestamp=" + System.currentTimeMillis() / 1000);
		contents.append("&appid=" + appId);
		contents.append("&sig=" + sign);
		contents.append("&openid=" + openid);
		contents.append("&openkey=" + openkey);

		logger.info("contents==========" + contents.toString());
		String result = HttpRequest.sendGet(url, contents.toString());
		logger.info("result=========" + result.toString());

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(result);
			if (jsonObject.has("ret")) {
				String ret = jsonObject.get("ret").toString();
				logger.info("ret===========" + ret);
				if (ret != null && ret.equals("0")) {
					return serverService.verification(openid, flag);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return RestResult.FAILURE_USE_OR_PWD_ERROR;
	}

	public RestResult doGangAoTai(String appId, String appKey, String userId, String token, String type, String device, int flag) {
		logger.info("======login DNY==================");
		StringBuffer tempSign = new StringBuffer();
		if (appId != null && !appId.equals("")) {
			tempSign.append(appId);
			tempSign.append(token);
			tempSign.append(userId);
		}
		tempSign.append(appKey);
		String sign = "";
		try {
			sign = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuffer contents = new StringBuffer("appId=" + appId);
		contents.append("&userId=" + userId);
		contents.append("&token=" + token);
		contents.append("&sign=" + sign);
		String result = HttpRequest.sendPost("http://api.raycreator.com/user/checklogin", contents.toString());
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.has("errorCode")) {
				String errorCode = jsonObject.get("errorCode").toString();
				if (errorCode != null && errorCode.equals("0")) {
					String errorMessage = jsonObject.get("errorMessage").toString();
					logger.info("errorMessage========" + errorMessage);
					String userTid = jsonObject.get("userId").toString();
					String userName = jsonObject.get("username").toString();
					Account account = new Account(Integer.parseInt(userTid), userName, userTid, "1", "2", type, device);
					accountService.create(account);
					return serverService.verification(userId, flag);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return RestResult.FAILURE_USE_OR_PWD_ERROR;
	}

	@RequestMapping(value = "dnyServers", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult getServerList() {
		return serverService.getServerListByHmt();
	}

	@RequestMapping(value = "getDongRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getDongRoleByQuest(String gameId, String serverId, String roleId, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		logger.info("======getDongRoleInfo==================");
		logger.info("======gameId==========" + gameId);
		logger.info("======roleId==========" + roleId);
		logger.info("======serverId=========" + serverId);

		if (serverId != null && !serverId.equals("")) {

			int id = Integer.parseInt(serverId);
			ServerInfo serverInfo = serverService.getServerInfo(id);
			String[] strList = serverInfo.getUrl().split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			StringBuffer args = new StringBuffer("c=1010");
			args.append("&gameId=" + gameId);
			args.append("&serverId=" + serverId);
			args.append("&roleId=" + roleId);
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			PrintWriter writer = response.getWriter();
			writer.append(result);
			writer.close();
		} else {
			PrintWriter writer = response.getWriter();
			writer.append("state:-1");
			writer.close();
		}

	}

	@RequestMapping(value = "dnyGetRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getRoleByQuest(String rcuid, String appId, String serverId, String time, String sign, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		logger.info("======dnyGetRoleInfo==================");
		logger.info("======rcuid==========" + rcuid);
		logger.info("======appId==========" + appId);
		logger.info("======serverId=========" + serverId);
		logger.info("======time===========" + time);
		logger.info("======sign===========" + sign);

		String appKey = "6428197728131dc2ca29ee14efae76d7";
		if (appId != null && appId.equals("1009")) {
			appKey = "04113623aa67cf9585b8ce3f7349c1ce";
		} else if (appId != null && appId.equals("1013")) {
			appKey = "bda010e7c4feca1e652f2ca19b51e235";
		} else if (appId != null && appId.equals("1014")) {
			appKey = "ba863b0dc2f29e7d4fd7e21cadc0578b";
		}
		/**
		 * String appId = "1013"; String appKey = "bda010e7c4feca1e652f2ca19b51e235"; try { code = URLDecoder.decode(code, "UTF-8"); } catch (UnsupportedEncodingException e) {
		 * 
		 * e.printStackTrace(); } return doGangAoTai(appId, appKey, uin, code, type, device, flag); } else if (type.equals("DNYE_ANDROID")) { String appId = "1014"; String appKey = "ba863b0dc2f29e7d4fd7e21cadc0578b";
		 * 
		 * 
		 * 
		 */
		StringBuffer tempSign = new StringBuffer();
		if (rcuid != null && !rcuid.equals("")) {
			tempSign.append(appId);
			tempSign.append(rcuid);
			tempSign.append(serverId);
			tempSign.append(time);
			tempSign.append(appKey);
		}
		String sign1 = "";
		try {
			sign1 = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info(tempSign.toString() + "======sign1=================" + sign1);
		// logger.info("======进入验证==================");
		// logger.info("======进入验证==================");
		// if (sign1.equals(sign)) {
		int userId = Integer.parseInt(rcuid);
		Account account = accountService.getAccountByUserId(userId);
		if (account != null) {
			int id = Integer.parseInt(serverId);
			ServerInfo serverInfo = serverService.getServerInfo(id);
			String[] strList = serverInfo.getUrl().split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			StringBuffer args = new StringBuffer("c=1010");
			args.append("&platAccount=" + account.getUserId());
			args.append("&serverId=" + id);
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			PrintWriter writer = response.getWriter();
			writer.append(result);
			writer.close();
		} else {
			PrintWriter writer = response.getWriter();
			writer.append("errorCode:1");
			writer.close();
		}
	}

	@RequestMapping(value = "taiGuoGetRoleInfo", method = { RequestMethod.GET, RequestMethod.POST })
	public void getRoleByQuestTaiGuo(String rcuid, String serverId, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		logger.info("======taiGuoGetRoleInfo==================");
		logger.info("======rcuid==========" + rcuid);
		logger.info("======serverId=========" + serverId);

		StringBuffer tempSign = new StringBuffer();
		if (rcuid != null && !rcuid.equals("")) {
			tempSign.append(rcuid);
			tempSign.append(serverId);
		}

		Account account = accountService.getAccountBySuid(rcuid);
		if (account != null) {
			int id = Integer.parseInt(serverId);
			ServerInfo serverInfo = serverService.getServerInfo(id);
			String[] strList = serverInfo.getUrl().split(":");
			StringBuffer title = new StringBuffer("http://");
			StringBuffer ip = new StringBuffer(strList[0]);
			title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
			StringBuffer args = new StringBuffer("c=1010");
			args.append("&platAccount=" + account.getsUid());
			args.append("&serverId=" + id);
			String result = HttpRequest.sendGet(title.toString(), args.toString());
			PrintWriter writer = response.getWriter();
			writer.append(result);
			writer.close();
		} else {
			PrintWriter writer = response.getWriter();
			writer.append("errorCode:1");
			writer.close();
		}
	}

	@RequestMapping(value = "omRewardMessage", method = { RequestMethod.GET, RequestMethod.POST })
	public void getOMRoleByQuest(String gameId, String contentId, String titleId, String serverId, String reward, String userName, String roleId, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		logger.info("======omRewardMessage==================");
		logger.info("=======gameId====" + gameId + "====================");
		logger.info("=======serverId===" + serverId + "============");
		logger.info("======raward==================" + reward + "==============");
		logger.info("======userName==================" + userName + "==============");
		logger.info("======roleId==================" + roleId + "==============");
		if (titleId != null && !titleId.equals("")) {
			logger.info("======titleId==================" + titleId + "==============");
		}
		if (contentId != null && !contentId.equals("")) {
			logger.info("======contentId==================" + contentId + "==============");
		}
		logger.info("======sign==================" + sign + "==============");

		PrintWriter writer = response.getWriter();
		String result = "-1";
		String key = "w9i1e211l042b4qcjbax9hmyx3xxs6n3";
		StringBuffer tempSign = new StringBuffer();
		if (gameId != null && !gameId.equals("")) {
			tempSign.append(gameId);
			tempSign.append(serverId);
			tempSign.append(reward);
			tempSign.append(userName);
			tempSign.append(roleId);
			if (titleId != null && !titleId.equals("")) {
				tempSign.append(titleId);
			}
			if (contentId != null && !contentId.equals("")) {
				tempSign.append(contentId);
			}
			tempSign.append(key);
		}
		logger.info("======原始==================" + tempSign.toString() + "==============");
		String sign1 = "";
		try {
			sign1 = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("======newSign==================" + sign1 + "==============");
		if (sign1.equals(sign)) {
			Account account = accountService.findByNick(userName);
			logger.info("=====account===========" + userName);
			if (account != null) {
				int id = Integer.parseInt(serverId);
				ServerInfo serverInfo = serverService.getServerInfo(id);
				String[] strList = serverInfo.getUrl().split(":");
				StringBuffer title = new StringBuffer("http://");
				StringBuffer ip = new StringBuffer(strList[0]);
				title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));

				StringBuffer args = new StringBuffer("c=1011");
				args.append("&playerId=" + roleId);
				args.append("&reward=" + reward);
				if (titleId != null && !titleId.equals("")) {
					args.append("&titleId=" + titleId);
				}
				if (contentId != null && !contentId.equals("")) {
					args.append("&contentId=" + contentId);
				}
				logger.info("=====args===========" + args.toString());
				logger.info("=====title===========" + title.toString());
				result = HttpRequest.sendGet(title.toString(), args.toString());
				writer.append(result);
				logger.info("======结果==================" + result + "==============");
				writer.close();
			} else {
				writer.append("account not exit");
				writer.close();
			}

		}
		writer.append("sign is error");
		writer.close();
	}

	@RequestMapping(value = "dnyRewardMessage", method = { RequestMethod.GET, RequestMethod.POST })
	public void getDNYRoleByQuest(String appId, String award, String userId, String serverId, String roleId, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException, JSONException {
		logger.info("======dnyRewardMessage==================");
		PrintWriter writer = response.getWriter();
		String result = "{\"msg\":\"参数错误\",\"errorId\":\"0\"}";
		String newAppId = request.getParameter("appId");
		String awardW = request.getParameter("award");
		String awarId1 = "";
		String num1 = "";
		String awarId2 = "";
		String num2 = "";

		String newUserId = request.getParameter("userId");
		String newRoleId = request.getParameter("roleId");
		String newSign = request.getParameter("sign");
		logger.info("=======appId====" + newAppId + "====================");
		logger.info("=======award===" + awardW + "============");
		logger.info("======userId==================" + newUserId + "==============");
		logger.info("======roleId==================" + newRoleId + "==============");
		logger.info("======sign==================" + newSign + "==============");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		String appKey = "6428197728131dc2ca29ee14efae76d7";
		if (newAppId != null && newAppId.equals("1009")) {
			appKey = "04113623aa67cf9585b8ce3f7349c1ce";
		} else if (appId != null && appId.equals("1013")) {
			appKey = "bda010e7c4feca1e652f2ca19b51e235";
		} else if (appId != null && appId.equals("1014")) {
			appKey = "ba863b0dc2f29e7d4fd7e21cadc0578b";
		}

		StringBuffer tempSign = new StringBuffer();
		if (appId != null && !appId.equals("")) {
			tempSign.append(newAppId);
			tempSign.append(awardW);
			tempSign.append(roleId);
			tempSign.append(serverId);
			tempSign.append(userId);
			tempSign.append(appKey);
		}
		logger.info("======sign加密前==================" + tempSign.toString() + "==============");
		response.setCharacterEncoding("UTF-8");
		String sign1 = "";
		try {
			sign1 = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("======sign加密后==================" + sign1.toString() + "==============");
		if (sign1.equals(sign)) {
			JSONObject jsonObject = new JSONObject("{'award'=" + awardW + "}");
			JSONObject jo = (JSONObject) ((JSONArray) jsonObject.get("award")).get(0);
			String awardId = jo.get("awardId").toString();
			int accountId = Integer.parseInt(userId);
			Account account = accountService.getAccountByUserId(accountId);
			if (account != null) {
				int id = Integer.parseInt(serverId);
				ServerInfo serverInfo = serverService.getServerInfo(id);
				String[] strList = serverInfo.getUrl().split(":");
				StringBuffer title = new StringBuffer("http://");
				StringBuffer ip = new StringBuffer(strList[0]);
				title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));
				logger.info("======title===" + title + "===========");
				StringBuffer args = new StringBuffer("c=1011");
				args.append("&playerId=" + newRoleId);
				args.append("&reward=" + awardId);
				result = HttpRequest.sendGet(title.toString(), args.toString());
				writer.append(result);
				logger.info("======结果=======" + result + "==============");
				writer.close();
			} else {
				writer.append(result);
				writer.close();
			}
		}
		writer.append(result);
		writer.close();

	}

	@RequestMapping(value = "RewardMessage", method = { RequestMethod.GET, RequestMethod.POST })
	public void getRoleByQuest(String appId, String award, String userId, String serverId, String roleId, String sign, HttpServletResponse response, HttpServletRequest request) throws IOException {
		logger.info("======rewardMessage==================");
		PrintWriter writer = response.getWriter();
		String result = "{\"msg\":\"参数错误\",\"errorId\":\"0\"}";
		String newAppId = request.getParameter("appId");
		String awarId = request.getParameter("award[0][awardId]");
		String num = request.getParameter("award[0][num]");
		String awarId1 = "";
		String num1 = "";
		String awarId2 = "";
		String num2 = "";
		if (request.getParameter("award[1][awardId]") != null && !request.getParameter("award[1][awardId]").equals("") && request.getParameter("award[1][num]") != null && !request.getParameter("award[1][num]").equals("")) {
			awarId1 = request.getParameter("award[1][awardId]");
			num1 = request.getParameter("award[1][num]");
		} else if (request.getParameter("award[2][awardId]") != null && !request.getParameter("award[2][awardId]").equals("") && request.getParameter("award[2][num]") != null && !request.getParameter("award[2][num]").equals("")) {
			awarId2 = request.getParameter("award[2][awardId]");
			num2 = request.getParameter("award[2][num]");
		}
		String newUserId = request.getParameter("userId");
		String newRoleId = request.getParameter("roleId");
		String newSign = request.getParameter("sign");
		logger.info("=======appId====" + newAppId + "====================");
		logger.info("=======award[0][awardId]===" + awarId + "============");
		logger.info("=======award[0][num]=" + num + "================");
		logger.info("======userId==================" + newUserId + "==============");
		logger.info("======roleId==================" + newRoleId + "==============");
		logger.info("======sign==================" + newSign + "==============");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		String appKey = "6428197728131dc2ca29ee14efae76d7";
		if (newAppId != null && newAppId.equals("1009")) {
			appKey = "04113623aa67cf9585b8ce3f7349c1ce";
		} else if (appId != null && appId.equals("1013")) {
			appKey = "bda010e7c4feca1e652f2ca19b51e235";
		} else if (appId != null && appId.equals("1014")) {
			appKey = "ba863b0dc2f29e7d4fd7e21cadc0578b";
		}

		HashMap<String, String> newAward = new HashMap<String, String>();
		StringBuffer temp = new StringBuffer("[{\"awardId\":\"");
		temp.append(awarId).append("\",");
		temp.append("\"num\"").append(":\"");
		temp.append(num).append("\"}");
		if (awarId1 != null && !awarId1.equals("") && num1 != null && !num1.equals("")) {
			temp.append(",");
			temp.append("{\"awardId\":\"");
			temp.append(awarId1).append("\",");
			temp.append("\"num\"").append(":\"");
			temp.append(num1).append("\"}");
		} else if (awarId2 != null && !awarId2.equals("") && num2 != null && !num2.equals("")) {
			temp.append(",");
			temp.append("{\"awardId\":\"");
			temp.append(awarId2).append("\",");
			temp.append("\"num\"").append(":\"");
			temp.append(num2).append("\"}");
		}
		temp.append("]");
		newAward.put("award", temp.toString());

		StringBuffer tempSign = new StringBuffer();
		if (appId != null && !appId.equals("")) {
			tempSign.append(newAppId);
			tempSign.append(newAward.get("award"));
			tempSign.append(roleId);
			tempSign.append(serverId);
			tempSign.append(userId);
			tempSign.append(appKey);
		}
		logger.info("======sign加密前==================" + tempSign.toString() + "==============");
		response.setCharacterEncoding("UTF-8");
		String sign1 = "";
		try {
			sign1 = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("======sign加密后==================" + sign1.toString() + "==============");
		if (sign1.equals(sign)) {
			int accountId = Integer.parseInt(userId);
			Account account = accountService.getAccountByUserId(accountId);
			if (account != null) {
				int id = Integer.parseInt(serverId);
				ServerInfo serverInfo = serverService.getServerInfo(id);
				String[] strList = serverInfo.getUrl().split(":");
				StringBuffer title = new StringBuffer("http://");
				StringBuffer ip = new StringBuffer(strList[0]);
				title.append(ip).append(":").append(String.valueOf(serverInfo.getPort()));

				if (awarId1 != null && !awarId1.equals("") && num1 != null && !num1.equals("")) {
					StringBuffer args2 = new StringBuffer("c=1011");
					args2.append("&playerId=" + newRoleId);
					args2.append("&reward=" + awarId1);
					result = HttpRequest.sendGet(title.toString(), args2.toString());
				} else if (awarId2 != null && !awarId2.equals("") && num2 != null && !num2.equals("")) {
					StringBuffer args1 = new StringBuffer("c=1011");
					args1.append("&playerId=" + newRoleId);
					args1.append("&reward=" + awarId2);
					result = HttpRequest.sendGet(title.toString(), args1.toString());
				}
				StringBuffer args = new StringBuffer("c=1011");
				args.append("&playerId=" + newRoleId);
				args.append("&reward=" + awarId);
				result = HttpRequest.sendGet(title.toString(), args.toString());
				writer.append(result);
				logger.info("======结果==================" + result + "==============");
				writer.close();
			} else {
				writer.append(result);
				writer.close();
			}
		}
		writer.append(result);
		writer.close();
	}

	public RestResult doMiWan(String appKey, String uid, String tokenId, String type, String device, int flag) {

		StringBuffer tempSign = new StringBuffer();
		tempSign.append(appKey);
		tempSign.append(uid);
		tempSign.append(tokenId);
		logger.info("========Fistsign=========" + tempSign.toString());
		String sign = "";
		try {
			sign = MD5.GetMD5Code(tempSign.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		logger.info("========sign=========" + sign);
		StringBuffer contents = new StringBuffer();
		contents.append("uid=" + uid);
		contents.append("&tokenId=" + tokenId);
		contents.append("&sign=" + sign);
		String url = "http://sdk.miwan123.com/loginVerify";
		logger.info("========contents=========" + contents.toString());
		String result = HttpRequest.sendPost(url, contents.toString());
		logger.info("========result=========" + result);
		if (result.equals("success")) {
			Account account = new Account(uid, PublicMethod.getUniqueId(), "", "", type, device);
			accountService.createYYB(account);
			return serverService.verification(uid, flag);
		}

		return null;
	}

	@RequestMapping(value = "loginJapan", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult loginJapan(String id, String password, String visitor, String device, String check, String usr, String pwd, HttpServletRequest req) {
		logger.info("===loginJapan=====");
		logger.info("===id            =====" + id);
		logger.info("===password=====" + password);
		logger.info("===visitor=======" + visitor);
		logger.info("===usr=========" + usr);
		logger.info("===device=======" + device + "====IP=======" + getIpAddress(req));
		logger.info("===check=======" + check);
		int flag = 0;
		if (checkService.findCheckByStr(check) != null) {
			flag = 1;
		}
		String url = "https://apisrv.gsgame.jp/commonservice/httpreq.php";
		String gameid = "2951";
		String gamepw = "yHd#4$$58aF";
		StringBuffer contents = new StringBuffer();
		contents.append("gameid=" + gameid);
		contents.append("&gamepw=" + gamepw);
		contents.append("&req=" + 1);
		contents.append("&id=" + id);
		contents.append("&password=" + password);
		if (visitor != null && visitor.equals("1")) {
			logger.info("===usr            =====" + usr);
			logger.info("===pwd=====" + pwd);
			logger.info("===visitor==true=====");
			Account visitorAccount = accountService.findByNick(usr);
			if (visitorAccount == null) {
				visitorAccount = new Account();
				visitorAccount.setNick(usr);
				visitorAccount.setPassword(pwd);
				visitorAccount.setSex("1");
				visitorAccount.setsUid(PublicMethod.getUniqueId());
				visitorAccount.setUserId(11111);
				visitorAccount.setCoinWin(0.00);
				visitorAccount.setCoinNft(0.00);
				visitorAccount.setType("VISITOR");
				accountService.createVisitor(visitorAccount);

			}
			return accountService.visitorLogin(visitorAccount, flag);

		}
		logger.info("===正常登陆发送请求=====" + contents.toString());
		String result = HttpRequest.sendPost(url, contents.toString());
		logger.info("===正常登陆获取请求结果=====" + result);
		if (result.equals("0")) {
			logger.info("===check=====" + id);
			Account account = accountService.findByNick(id);
			if (account != null) {
				logger.info("===check=====OVER");
				account.setDevice(device);
				return accountService.login(account, flag);
			} else {
				logger.info("===免注册=====" + id);
				account = new Account();
				account.setNick(id);
				account.setPassword(password);
				account.setSex("1");
				account.setsUid(id);
				account.setUserId(11111);
				account.setCoinNft(0.00);
				account.setCoinWin(0.00);
				account.setType("JAPAN");
				account.setDevice(device);
				accountService.registerJapn(account);
				return accountService.login(account, flag);
			}
		} else if (result.equals("91")) {
			return RestResult.japanLoginFail("パスワードが正しくありません");
		} else if (result.equals("92")) {
			return RestResult.japanLoginFail("存在しないユーザーIDです");
		} else if (result.equals("98")) {
			return RestResult.japanLoginFail("しばらくしてからもう一度お試し下さい");
		} else if (result.equals("99")) {
			return RestResult.japanLoginFail("しばらくしてからもう一度お試し下さい");
		}
		return RestResult.japanLoginFail("しばらくしてからもう一度お試し下さい");
	}

	@RequestMapping(value = "registerJapan", method = { RequestMethod.GET, RequestMethod.POST })
	public RestResult registerJapan(String id, String password, String mail) {
		logger.info("===registerJapan=====");
		String gameid = "2951";
		String gamepw = "yHd#4$$58aF";
		String url = "https://apisrv.gsgame.jp/commonservice/httpreq.php";
		StringBuffer contents = new StringBuffer();
		contents.append("gameid=" + gameid);
		contents.append("&gamepw=" + gamepw);
		contents.append("&req=" + 2);
		contents.append("&id=" + id);
		contents.append("&password=" + password);
		contents.append("&mail=" + mail);
		logger.info("===正常登陆发送请求=====" + contents.toString());
		String result = HttpRequest.sendPost(url, contents.toString());
		logger.info("===正常登陆获取请求结果=====" + result);
		if (result.equals("0")) {
			Account account = new Account();
			account.setNick(id);
			account.setPassword(password);
			account.setSex("1");
			account.setsUid(id);
			account.setUserId(11111);
			account.setCoinNft(0.00);
			account.setCoinWin(0.00);
			account.setType("JAPAN");
			if (accountService.registerJapn(account)) {
				return RestResult.registerSuccess("账号创建成功");
			}
		} else if (result.equals("91")) {
			return RestResult.japanLoginFail("ユーザーIDが既に存在しています");
		} else if (result.equals("92")) {
			return RestResult.japanLoginFail("メールアドレスが既に存在しています");
		} else if (result.equals("93")) {
			return RestResult.japanLoginFail("文字数および記号が含まれていないか確認してください");
		} else if (result.equals("94")) {
			return RestResult.japanLoginFail("文字数および記号が含まれていないか確認してください");
		} else if (result.equals("95")) {
			return RestResult.japanLoginFail("メールアドレスを確認してください");
		} else if (result.equals("98")) {
			return RestResult.japanLoginFail("しばらくしてからもう一度お試し下さい");
		} else if (result.equals("99")) {
			return RestResult.japanLoginFail("しばらくしてからもう一度お試し下さい");
		}
		return RestResult.registerSuccess("しばらくしてからもう一度お試し下さい");
	}

	public RestResult YNLoginON(String appId, String appKey, String userId, String token, String type, String device, int flag) {
		logger.info("====== YNLoginON=============token=====" + token);
		logger.info("===========================userId=====" + userId);
		Account account = new Account(userId, PublicMethod.getUniqueId(), "", "", type, device);
		account.setNick(userId);
		this.accountService.createYYB(account);
		return this.serverService.getServerList(userId, flag);
	}

	public RestResult oMLogin(String appKey, String uid, String tokenId, String type, String device, String ext1, int flag) {
		logger.info("===================初始化==================");
		Account account = new Account(uid, PublicMethod.getUniqueId(), "", "", type, device);
		account.setNick(uid);
		if (ext1 != null && !ext1.equals("")) {
			account.setAndroidId(ext1);
		}
		this.accountService.createYYB(account);
		return this.serverService.getServerList(uid, flag);
	}

	public static String getIpAddress(HttpServletRequest request) {
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