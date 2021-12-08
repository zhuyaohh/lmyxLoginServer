package com.cvovo.gamemanager.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvovo.gamemanager.config.VersionConfig;
import com.cvovo.gamemanager.game.persist.entity.GroupInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.persist.entity.ServerType;
import com.cvovo.gamemanager.game.persist.entity.TeamInfoTemp;
import com.cvovo.gamemanager.game.persist.entity.TempAccount;
import com.cvovo.gamemanager.game.persist.entity.TempRandomRank;
import com.cvovo.gamemanager.game.persist.entity.WorldGuildTeam;
import com.cvovo.gamemanager.game.persist.entity.WorldGuildTemp;

public class RestResult extends HashMap<String, Object> {
	private static final Logger logger = LoggerFactory.getLogger(RestResult.class);
	private static final long serialVersionUID = 1L;

	public static final String KEY_ID = "id";
	public static final String KEY_DATA = "data";
	public static final String KEY_USERID = "userID";
	public static final String KEY_SUSERID = "suid";
	public static final String KEY_URI = "uri";
	public static final String KEY_VERSION = "version";
	public static final String KEY_VERSIONURI = "url";
	public static final String KEY_SERVERID = "serverId";
	public static final String KEY_INNERURI = "innerUrl";
	public static final String KEY_LOGIN = "login";
	public static final String KEY_TYPE = "type";
	public static final String CHECK_USER = "error";
	public static final String BILL_NO = "TCIOrderId";
	public static final String BILL_GUID = "guid";
	public static final String BILL_ZFB_URL = "zfbUrl";
	public static final String BILL_RESULT = "Flag";
	public static final String BILL_EXT1 = "Ext1";
	public static final String BILL_EXT2 = "Ext2";
	public static final String KEY_RESULT = "result";
	public static final String GROUP_NUM = "num";
	public static final String CHECK_FLAG = "check";
	public static final String BIND_IS = "bind";
	public static final String RECEIPT = "receipt";
	public static final String LAST_BALANCE = "last";
	public static final String ONLINE_NUM = "online";
	public static final String PAY_URI = "pay";
	public static final String UPDATE_URL = "updateURL";
	public static final String NOTICE_URI = "notice";

	public static final int SUCCESS = 0;
	public static final int FAILURE = 1;
	public static final RestResult USE_OR_PWD_ERROR = ID(1);
	public static final RestResult FAILURE_USE_OR_PWD_ERROR = ID(2);
	public static final RestResult FAILURE_SDK_ERROR = ID(3);
	public static final RestResult FAILURE_INVALID_USE_ERROR = ID(4);

	/*
	 * 日本返回信息
	 */
	public static final int JAPAN_LOGIN_ERROR = 999;

	/*
	 * 印尼返回信息
	 */
	public static final String YINNI_CODE = "code";
	public static final String YINNI_MESSAGE = "content";

	/*
	 * 港澳台 信息反馈
	 */
	public static final String KEY_CODE = "errorCode";
	public static final String KEY_MESSAGE = "errorMessage";
	public static final String KEY_LIST = "list";

	public static final String KEY_NAME = "name";
	public static final String KEY_HMTID = "id";
	public static final String KEY_STATUS = "status";
	public static final String REWARD = "award";

	public static final String TAIGUOPAY = "result";
	public static final String TAIGUOPAYMSG = "msg";
	/***
	 * 活动反馈
	 */
	public static final String KEY_START = "startDay";

	public RestResult() {
	}

	public RestResult(String key, Object value) {
		put(key, value);
	}

	public static RestResult errorF() {
		return new RestResult(KEY_ID, FAILURE_USE_OR_PWD_ERROR);
	}

	public static RestResult getOnlineNum(String value) {
		return new RestResult(ONLINE_NUM, value);
	}

	public static RestResult ID(int id) {
		return new RestResult(KEY_ID, id);
	}

	public static RestResult Money(String leftMoney) {
		return new RestResult(LAST_BALANCE, leftMoney);
	}

	public static RestResult receipt(String receipt) {
		return new RestResult(RECEIPT, receipt);
	}

	public static RestResult URI(String uri) {
		return new RestResult(KEY_URI, uri);
	}

	public static RestResult failure() {
		return new RestResult(KEY_ID, FAILURE);
	}

	public static RestResult failure(Object data) {
		RestResult r = failure();
		r.put(KEY_DATA, data);
		return r;
	}

	public static RestResult ForbesResult(String type, Object data) {
		RestResult r = new RestResult(KEY_TYPE, type);
		r.put(KEY_LIST, data);
		return r;
	}

	public static RestResult success() {
		return new RestResult(KEY_ID, SUCCESS);
	}

	public static RestResult versionSuc(String version) {
		if (version != null) {
			return new RestResult(KEY_VERSION, version);
		}
		return new RestResult(KEY_VERSION, VersionConfig.version);
	}

	public static RestResult success(String userId, Object data, String suid) {
		RestResult r = success();
		r.put(KEY_DATA, data);
		r.put(KEY_USERID, userId);
		r.put(KEY_SUSERID, suid);
		return r;
	}

	public static RestResult success(String userId, Object data) {
		RestResult r = success();
		r.put(KEY_DATA, data);
		r.put(KEY_USERID, userId);
		return r;
	}

	public static RestResult successLY(String userId, Object data, int flag, byte isBind) {
		RestResult r = success();
		r.put(KEY_DATA, data);
		r.put(KEY_USERID, userId);
		r.put(CHECK_FLAG, flag);
		r.put(BIND_IS, isBind);
		return r;
	}

	
	public static RestResult success(String userId, Object data, int flag) {
		RestResult r = success();
		r.put(KEY_DATA, data);
		r.put(KEY_USERID, userId);
		r.put(CHECK_FLAG, flag);
		return r;
	}
	public static RestResult registerSuccess(Object data) {
		RestResult r = success();
		r.put(KEY_DATA, data);
		r.put(KEY_ID, 0);
		return r;
	}

	public static RestResult registerFail(Object data) {
		RestResult r = success();
		r.put(KEY_DATA, data);
		r.put(KEY_ID, 1);
		return r;
	}

	public static RestResult versionSuccess() {
		RestResult r = versionSuc(null);
		r.put(KEY_VERSIONURI, "http://download.txigame.com/AssetBundle/");
		return r;
	}

	public static RestResult getBillSuc(String flag, String billNo) {
		logger.info("========flag==========" + flag);
		int result = Integer.parseInt(flag);
		RestResult rs = new RestResult(BILL_RESULT, result);
		if (flag != null && flag.equals("1")) {
			logger.info("========billNo==========" + billNo);
			rs.put(BILL_NO, billNo);
			rs.put(BILL_GUID, "");
		}
		return rs;
	}

	public static RestResult getBillSuc(String flag, String billNo, String guid) {
		logger.info("========flag==========" + flag);
		int result = Integer.parseInt(flag);
		RestResult rs = new RestResult(BILL_RESULT, result);
		if (flag != null && flag.equals("1")) {
			logger.info("========billNo==========" + billNo);
			rs.put(BILL_NO, billNo);
			rs.put(BILL_GUID, guid);
		}
		return rs;
	}

	public static RestResult getBillZFBSuc(String flag, String billNo) {
		int result = Integer.parseInt(flag);
		RestResult rs = new RestResult(BILL_RESULT, result);
		if (flag != null && flag.equals("1")) {
			rs.put(BILL_NO, billNo);

		}
		return rs;
	}

	public static RestResult getBillWXSuc(String flag, String billNo, String ext1, String ext2, String guid) {
		int result = Integer.parseInt(flag);
		RestResult rs = new RestResult(BILL_RESULT, result);
		if (flag != null && flag.equals("1")) {
			rs.put(BILL_NO, billNo);
			rs.put(BILL_EXT1, ext1);
			rs.put(BILL_EXT2, ext2);
			rs.put(BILL_GUID, guid);

		}
		return rs;
	}

	public static RestResult getBillSuc(String flag, String billNo, String guild, String ext1, String ext2) {

		int result = Integer.parseInt(flag);
		RestResult rs = new RestResult(BILL_RESULT, result);
		if (flag != null && flag.equals("1")) {
			rs.put(BILL_NO, billNo);
			rs.put(BILL_GUID, guild);
			rs.put(BILL_EXT1, ext1);
			rs.put(BILL_EXT2, ext2);
		}
		return rs;
	}

	public static RestResult checkUserIdSuc(String type) {
		RestResult r = new RestResult(KEY_RESULT, 0);
		r.put(KEY_TYPE, type);
		return r;
	}

	public static RestResult checkUserIdError() {
		RestResult r = new RestResult(KEY_RESULT, 1);
		r.put(CHECK_USER, "账号不存在");
		return r;
	}

	public static RestResult payError(String result) {
		RestResult r = new RestResult(CHECK_USER, result);
		return r;
	}

	public static RestResult fbsResult(String result) {
		RestResult r = new RestResult(KEY_RESULT, result);
		return r;
	}

	public static RestResult versionSuccess(String url, String innerUrl, String serverId) {
		int reslut = 1;
		if (url != null && !url.equals("")) {
			reslut = 0;
		}
		RestResult r = new RestResult(KEY_RESULT, reslut);
		if (reslut != 1) {
			r.put(KEY_VERSIONURI, url);
			r.put(KEY_SERVERID, serverId);
			r.put(KEY_INNERURI, innerUrl);
		}
		return r;
	}

	public static RestResult groupBuy() {
		return new RestResult(KEY_RESULT, 0);
	}

	public static RestResult result(int result) {
		return new RestResult(KEY_RESULT, result);
	}

	public static RestResult getGroupInfoResult(GroupInfo info) {
		RestResult r = new RestResult(KEY_RESULT, 0);
		r.put(GROUP_NUM, info != null ? info.getCount() : 0);
		return r;
	}

	public static RestResult success(List<ServerInfo> data) {
		RestResult r = null;
		if (data != null && data.size() > 0) {
			r = new RestResult(KEY_CODE, SUCCESS);
			r.put(KEY_MESSAGE, "成功");
		} else {
			r = new RestResult(KEY_CODE, FAILURE);
			r.put(KEY_MESSAGE, "没有对应的服务器列表");
		}
		List<TempAccount> list = new ArrayList<TempAccount>();
		for (ServerInfo serverInfo : data) {
			list.add(new TempAccount(serverInfo.getId(), serverInfo.getName(), serverInfo.getState()));
		}
		r.put(KEY_LIST, list);
		return r;
	}

	public static RestResult billDNY(String errorCode, String errorMessage) {
		RestResult r = new RestResult(KEY_CODE, errorCode);
		r.put(KEY_MESSAGE, errorMessage);
		return r;
	}

	public static RestResult billYinNi(String code, String content) {
		RestResult r = new RestResult(YINNI_CODE, code);
		r.put(YINNI_MESSAGE, content);
		return r;
	}

	public static RestResult successRank(List<TempRandomRank> list) {
		RestResult r = new RestResult(KEY_LIST, list);
		return r;
	}

	public static RestResult successRank(List<TempRandomRank> list, String time) {
		RestResult r = new RestResult(KEY_START, time);
		r.put(KEY_LIST, list);
		return r;
	}

	public static RestResult FailRank() {
		RestResult r = new RestResult(KEY_START, "2016-01-09 00:00:00");
		r.put(KEY_LIST, null);
		return r;
	}

	public static RestResult successError() {
		RestResult r = new RestResult(KEY_CODE, "1");
		r.put(KEY_MESSAGE, "活动未开启");
		return r;
	}

	// public static RestResult successTeamInfo(List<TeamInfo> list) {
	// RestResult r = new RestResult(KEY_LIST, list);
	// return r;
	// }

	public static RestResult successTeamInfoTemp(List<TeamInfoTemp> list) {
		RestResult r = new RestResult(KEY_LIST, list);
		return r;
	}

	public static RestResult successWorldGuildInfo(List<WorldGuildTeam> list) {
		RestResult r = new RestResult(KEY_LIST, list);
		return r;
	}

	public static RestResult successWorldGuildInfo() {
		RestResult r = new RestResult("", "");
		return r;
	}

	public static RestResult successWorldGuildTemp(List<WorldGuildTemp> list) {
		RestResult r = new RestResult(KEY_LIST, list);
		return r;
	}

	public static RestResult japanLoginFail(String userId) {
		RestResult r = errorJapanLoginF();
		r.put(KEY_USERID, userId);
		return r;
	}

	public static RestResult errorJapanLoginF() {
		return new RestResult(KEY_ID, JAPAN_LOGIN_ERROR);
	}

	public static RestResult taiGuoSuccessError(String result, String msg) {
		RestResult r = new RestResult(TAIGUOPAY, result);
		r.put(TAIGUOPAYMSG, msg);
		return r;
	}

	public static RestResult successStart(int type, ServerType serverType) {
		RestResult r = versionSuc(serverType.getVersion());
		r.put(KEY_LOGIN, serverType.getLogin());
		r.put(KEY_VERSIONURI, serverType.getUrl());
		r.put(PAY_URI, serverType.getPayUrl());
		r.put(NOTICE_URI, serverType.getNotice());
		r.put(UPDATE_URL, serverType.getUpdateURL());
		return r;
	}

	public static RestResult successStart(String version, ServerType serverType) {
		RestResult r = versionSuc(version);
		r.put(KEY_LOGIN, serverType.getNextLogin());
		r.put(KEY_VERSIONURI, serverType.getUrl());
		r.put(PAY_URI, serverType.getNextPayUrl());
		r.put(UPDATE_URL, serverType.getUpdateURL());
		return r;
	}
}
