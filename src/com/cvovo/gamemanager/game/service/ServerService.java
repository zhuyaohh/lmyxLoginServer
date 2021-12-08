package com.cvovo.gamemanager.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.ServerInfoDao;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class ServerService {

	private static final Logger logger = LoggerFactory.getLogger(ServerService.class);
	@Autowired
	private ServerInfoDao serverInfoDao;

	public RestResult getServerList(String userId, String suid) {
		return RestResult.success(userId, serverInfoDao.findBySign(1), suid);
	}

	public RestResult getServerList(String userId) {
		return RestResult.success(userId, serverInfoDao.findBySign(1));
	}

	public List<ServerInfo> getServerList() {
		return serverInfoDao.findAll();
	}

	public List<ServerInfo> getServerListBySign(int id) {
		return serverInfoDao.findBySign(id);
	}

	public RestResult getServerList(String userId, int flag) {
		logger.info("============" + userId + "===========" + flag);
		switch (flag) {
		case 0:
		case 2:
			return RestResult.success(userId, serverInfoDao.findBySign(1), flag);
		case 1:
			return RestResult.success(userId, serverInfoDao.findBySign(0), flag);
		case 3:
			List<ServerInfo> servers = serverInfoDao.findBySign(3);
			for (ServerInfo serverInfo : servers) {
				serverInfo.setState(3);
			}
			return RestResult.success(userId, servers, 0);
		default:
			return RestResult.success(userId, serverInfoDao.findBySign(1), flag);
		}
	}

	public RestResult getServerListLY(String userId, int flag, byte isbind) {
		logger.info("============" + userId + "===========" + flag + "==============" + isbind);
		switch (flag) {
		case 0:
		case 2:
			return RestResult.successLY(userId, serverInfoDao.findBySign(1), flag, isbind);
		case 1:
			return RestResult.successLY(userId, serverInfoDao.findBySign(0), flag, isbind);
		case 3:
			List<ServerInfo> servers = serverInfoDao.findBySign(3);
			for (ServerInfo serverInfo : servers) {
				serverInfo.setState(3);
			}
			return RestResult.successLY(userId, servers, 0, isbind);
		default:
			return RestResult.successLY(userId, serverInfoDao.findBySign(1), flag, isbind);
		}
	}

	public RestResult changeServerStatus(String userId) {
		return RestResult.success(userId, null, 0);
	}

	public RestResult verification(String userId, String suid) {
		return RestResult.success(userId, serverInfoDao.findBySign(1), suid);
	}

	public RestResult verification(String userId) {
		return RestResult.success(userId, serverInfoDao.findBySign(1));
	}

	public RestResult verification(String userId, int flag) {
		if (flag == 0) {
			return RestResult.success(userId, serverInfoDao.findBySign(1), flag);
		}
		return RestResult.success(userId, serverInfoDao.findBySign(0), flag);
	}

	public RestResult verificationLY(String userId, int flag, byte isBind) {
		if (flag == 0) {
			return RestResult.successLY(userId, serverInfoDao.findBySign(1), flag, isBind);
		}
		return RestResult.successLY(userId, serverInfoDao.findBySign(0), flag, isBind);
	}

	public RestResult saveServerInfo(ServerInfo exit) {
		int id = new Long(exit.getId()).intValue();
		ServerInfo info = this.getServerInfo(id);
		if (info != null) {
			info.setInnerUrl(exit.getInnerUrl());
			info.setName(exit.getName());
			info.setPort(exit.getPort());
			info.setSign(1);
			info.setState(exit.getState());
			info.setUrl(exit.getUrl());
			serverInfoDao.save(info);
		}
		serverInfoDao.save(exit);
		return RestResult.success();
	}

	public RestResult updateServerInfo(int serverId) {
		ServerInfo info = this.getServerInfo(serverId);
		if (info != null) {
			info.setSign(0);
			serverInfoDao.save(info);
			return RestResult.success();
		}
		return RestResult.failure();
	}

	public ServerInfo getServerInfo(int id) {
		List<ServerInfo> list = serverInfoDao.findAll();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == id) {
				return list.get(i);
			}
		}
		return null;
	}

	public ServerInfo getRadomInfo(int id) {
		List<ServerInfo> list = serverInfoDao.findBySign(1);
		List<ServerInfo> radom = new ArrayList<ServerInfo>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() != id) {
				radom.add(list.get(i));
			}
		}
		if (radom.size() == 0) {
			return null;
		}
		Random random = new Random();
		int j = random.nextInt(radom.size());
		return radom.get(j);
	}

	public RestResult getServerListByHmt() {
		return RestResult.success(serverInfoDao.findBySign(1));
	}

	public RestResult getAllServerList() {
		return RestResult.success(serverInfoDao.findAll());
	}
}
