package com.cvovo.gamemanager.game.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.controller.HttpRequest;
import com.cvovo.gamemanager.game.persist.entity.Account;
import com.cvovo.gamemanager.util.PublicMethod;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class FacebookAuthService {
	private static final Logger logger = LoggerFactory.getLogger(FacebookAuthService.class);
	@Autowired
	private AccountService accountService;

	@Autowired
	private ServerService serverService;

	public static final String FB_AUTH_LOGIN_URL = "https://graph.facebook.com/oauth/access_token";
	public static final String FB_USERINFO_URL = "https://graph.facebook.com/me";
	// appid和appSecret 是facebook上申请
	public static final String FB_APP_ID = "446571579591961";
	public static final String FB_APP_KEY = "7757d5d8f4ddad7df9df2dbf0ab61eb5";
	// 获取用户的那些信息
	public static final String FB_USER_FIELDS = "id";

	public RestResult checkLoginWithToken(String accessToken, String type, String uin, int flag) {
		logger.info("=========accessToken===========" + accessToken);

		StringBuffer contents = new StringBuffer("access_token=" + accessToken);
		contents.append("&fields=" + FB_USER_FIELDS);
		String userRet = HttpRequest.sendGet(FB_USERINFO_URL, contents.toString());
		Account account = new Account();
		logger.info("=========userRet===========" + userRet);
		JSONObject json = null;
		try {
			json = new JSONObject(userRet);
			String userId = json.getString("id");
			if (userId.equals(uin)) {
				logger.info("=========userRet验证通过===========");
				Account exist = accountService.findByFbUserId(userId);
				if (exist != null) {
					logger.info("=========已有账户===========");
					logger.info(serverService.verification(exist.getsUid(), flag).toString());
					return serverService.verificationLY(exist.getsUid(), flag, (byte) 1);
				} else {
					logger.info("=========新有账户===========");
					account.setType(type);
					account.setsUid(PublicMethod.getUniqueId());
					account.setFbUserId(userId);
					accountService.createFB(account);
					return serverService.verificationLY(account.getsUid(), flag, (byte) 1);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return RestResult.FAILURE_USE_OR_PWD_ERROR;
	}
}
