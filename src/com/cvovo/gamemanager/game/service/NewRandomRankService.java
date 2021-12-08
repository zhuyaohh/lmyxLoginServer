package com.cvovo.gamemanager.game.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cvovo.gamemanager.game.persist.dao.NewRandomRankDao;
import com.cvovo.gamemanager.game.persist.entity.NewRandomRank;
import com.cvovo.gamemanager.game.persist.entity.RandomRankConfig;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.persist.entity.TempRandomRank;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class NewRandomRankService {

	@Autowired
	private NewRandomRankDao newRandomRankDao;
	@Autowired
	private RandomRankConfigService randomRankConfigService;

	@Autowired
	private ServerService serverService;

	public void save(NewRandomRank newRandomRank) {
		newRandomRankDao.save(newRandomRank);
	}

	public NewRandomRank findByServerIdAndPlayerId(String type, int serverId, int playerId) {
		return newRandomRankDao.findByTypeAndPlayerIdAndType(type, serverId, playerId);
	}

	public RestResult getTop100ByRank(String type) {
		PageRequest page = new PageRequest(0, 100, new Sort(Direction.DESC, "rank"));
		List<NewRandomRank> list = newRandomRankDao.getTop100ByRank(type, page);
		List<TempRandomRank> newList = new ArrayList<TempRandomRank>();
		for (NewRandomRank rr : list) {
			TempRandomRank trr = new TempRandomRank();
			trr.setServerId(rr.getServerId());
			trr.setPlayerId(rr.getPlayerId());
			ServerInfo serverInfo = serverService.getServerInfo(rr.getServerId());
			if (serverInfo != null) {
				String[] strList = serverInfo.getUrl().split(":");
				StringBuffer ip = new StringBuffer("http://");
				ip.append(strList[0]);
				ip.append(":").append(serverInfo.getPort());
				if (serverInfo.getInnerUrl() != null && !serverInfo.getInnerUrl().equals("")) {
					StringBuffer inner = new StringBuffer("http://");
					inner.append(serverInfo.getInnerUrl());
					inner.append(":").append(serverInfo.getPort());
					trr.setInnerUrl(inner.toString());
				} else {
					trr.setInnerUrl(ip.toString());
				}
				trr.setCurrentRank(rr.getRank());
				trr.setUrl(ip.toString());
				newList.add(trr);
			}
		}
		RandomRankConfig randomRankConfig = randomRankConfigService.getRandomRankConfig();
		if (randomRankConfig != null) {
			// return RestResult.FailRank();
			return RestResult.successRank(newList, randomRankConfig.getLastLoginTime());
		}
		return RestResult.FailRank();
	}

	public List<NewRandomRank> findAll() {
		return newRandomRankDao.findAll();
	}

	public void updatAllStatus() {
		newRandomRankDao.upRandomRank();
	}

	public List<NewRandomRank> getTop64ByRank(String type) {

		List<String> list = new ArrayList<String>();
		list.add("rank");
		list.add("createTime");
		PageRequest page = new PageRequest(0, 64, new Sort(Direction.DESC, list));
		return newRandomRankDao.getTop100ByRank(type, page);
	}

	public RestResult getRandomRankBy(String type, int rank, int playerId, int serverId) {

		List<NewRandomRank> list = getTop20ByRank(type, 1, rank);
		NewRandomRank rRank = this.findByServerIdAndPlayerId(type, serverId, playerId);
		if (rRank != null) {
			list.remove(rRank);
		}

		List<TempRandomRank> newList = new ArrayList<TempRandomRank>();
		if (list.size() >= 3) {
			List<NewRandomRank> tempList = new ArrayList<NewRandomRank>();
			for (int i = 0; i < 3; i++) {
				Random random = new Random();
				int j = random.nextInt(list.size());
				System.out.println(j);
				tempList.add(list.get(j));
				list.remove(j);
			}
			for (NewRandomRank rr : tempList) {
				TempRandomRank trr = new TempRandomRank();
				trr.setServerId(rr.getServerId());
				trr.setPlayerId(rr.getPlayerId());
				ServerInfo serverInfo = serverService.getServerInfo(rr.getServerId());
				if (serverInfo != null) {
					String[] strList = serverInfo.getUrl().split(":");
					StringBuffer ip = new StringBuffer("http://");
					ip.append(strList[0]);
					ip.append(":").append(serverInfo.getPort());
					if (serverInfo.getInnerUrl() != null && !serverInfo.getInnerUrl().equals("")) {
						StringBuffer inner = new StringBuffer("http://");
						inner.append(serverInfo.getInnerUrl());
						inner.append(":").append(serverInfo.getPort());
						trr.setInnerUrl(inner.toString());
					} else {
						trr.setInnerUrl(ip.toString());
					}
					trr.setUrl(ip.toString());
					trr.setCurrentRank(rr.getRank());
					newList.add(trr);
				}
			}
			return RestResult.successRank(newList);
		}
		return RestResult.result(1);
	}

	private List<NewRandomRank> getTop20ByRank(String type, int base, int randomRank) {

		List<String> list = new ArrayList<String>();
		list.add("rank");
		list.add("createTime");
		PageRequest page = new PageRequest(0, 20, new Sort(Direction.DESC, list));
		List<NewRandomRank> tempList = newRandomRankDao.getTop20ByNearbyRank(type, randomRank - 50 * base, randomRank + 50 * base, page);
		if (tempList.size() <= 3) {
			for (int i = 2; i < 100; i++) {
				int t = randomRank - 50 * i;
				if (t < 0) {
					t = 0;
				}
				tempList = newRandomRankDao.getTop20ByNearbyRank(type, t, randomRank + 50 * i, page);
				if (tempList.size() > 3) {
					return tempList;
				}
			}
		}
		return tempList;
	}
}
