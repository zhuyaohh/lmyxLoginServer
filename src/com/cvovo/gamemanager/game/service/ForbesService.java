package com.cvovo.gamemanager.game.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.ForbesDao;
import com.cvovo.gamemanager.game.persist.entity.ForbesEntity;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class ForbesService {
	private static final Logger logger = LoggerFactory.getLogger(ForbesService.class);
	@Autowired
	private ForbesDao forbesDao;

	public RestResult get100Rank(int type) {
		PageRequest page = new PageRequest(0, 100, new Sort(Direction.DESC, "count"));
		List<ForbesEntity> list = forbesDao.findByTypeId(type, page);
		return RestResult.ForbesResult(String.valueOf(type), list);
	}

	public RestResult saveForbesEntity(ForbesEntity fe) {
		if (fe != null) {
			forbesDao.save(fe);
			return RestResult.fbsResult("0");
		}
		return RestResult.fbsResult("1");
	}

	public ForbesEntity findForbesEntity(int type, int playerId) {
		return forbesDao.findForbesByTypeIdAndPlayerId(type, playerId);
	}
}
