package com.cvovo.gamemanager.game.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.ServerTypeDao;
import com.cvovo.gamemanager.game.persist.entity.ServerType;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class ServerTypeService {
	@Autowired
	private ServerTypeDao serverTypeDao;

	public RestResult getServerMessageList(int channel) {
		List<ServerType> list = serverTypeDao.findByChannel(channel);
		if (list.size() == 1) {
			ServerType temp = list.get(0);
			return RestResult.successStart(channel, temp);
		}
		return null;
	}

	public RestResult getServerMessageList(int channel, String version) {
		List<ServerType> list = serverTypeDao.findByChannel(channel);
		if (list.size() == 1) {
			ServerType temp = list.get(0);
			int serverVersion = getVersionNumber(temp.getVersion());
			int clientVesion = getVersionNumber(version);
			if (clientVesion > serverVersion) {
				return RestResult.successStart(temp.getNetxVersion(), temp);
			}
			return RestResult.successStart(channel, temp);
		}
		return null;
	}

	public int getVersionNumber(String version) {
		String[] temp = version.split("\\.");
		System.out.println(temp.length);
		int A = 0;
		for (int i = 0; i < temp.length; i++) {
			int base = 10;
			for (int j = i; j < temp.length; j++) {
				base = base * 100;
			}
			A += Integer.parseInt(temp[i]) * base;
		}
		return A;
	}
}
