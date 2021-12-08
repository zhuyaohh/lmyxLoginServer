package com.cvovo.gamemanager.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.GroupInfoDao;
import com.cvovo.gamemanager.game.persist.entity.GroupInfo;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class GroupInfoService {

	@Autowired
	private GroupInfoDao groupDao;

	public RestResult findGroupInfoByType(int type) {
		return RestResult.getGroupInfoResult(groupDao.findByType(type));
	}

	public RestResult buyGroupInfoByType(int type) {

		GroupInfo info = groupDao.findByType(type);
		if (info == null) {
			groupDao.save(new GroupInfo(type));
		} else {
			info.increaseCount();
			groupDao.save(info);
		}
		return RestResult.groupBuy();
	}
}
