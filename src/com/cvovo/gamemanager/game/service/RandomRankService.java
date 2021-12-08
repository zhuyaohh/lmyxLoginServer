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

import com.cvovo.gamemanager.game.persist.dao.RandomRankDao;
import com.cvovo.gamemanager.game.persist.entity.RandomRank;
import com.cvovo.gamemanager.game.persist.entity.RandomRankConfig;
import com.cvovo.gamemanager.game.persist.entity.ServerInfo;
import com.cvovo.gamemanager.game.persist.entity.TempRandomRank;
import com.cvovo.gamemanager.util.RestResult;

@Service
@Transactional
public class RandomRankService {

	@Autowired
	private RandomRankDao randomRankDao;

	@Autowired
	private RandomRankConfigService randomRankConfigService;

	@Autowired
	private ServerService serverService;

	public void save(RandomRank randomRank) {
		randomRankDao.save(randomRank);
	}

	public RandomRank findByServerIdAndPlayerId(int serverId, int playerId) {
		return randomRankDao.findByServerIdAndPlayerId(serverId, playerId);
	}

	public RestResult getTop100ByRank() {
		PageRequest page = new PageRequest(0, 100, new Sort(Direction.DESC, "rank"));
		List<RandomRank> list = randomRankDao.getTop100ByRank(page);
		List<TempRandomRank> newList = new ArrayList<TempRandomRank>();
		for (RandomRank rr : list) {
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
			return RestResult.successRank(newList, randomRankConfig.getLastLoginTime());
		}
		return null;
	}

	public List<RandomRank> findAll() {
		return randomRankDao.findAll();
	}

	public void updatAllStatus() {
		randomRankDao.upRandomRank();
	}

	public List<RandomRank> getTop64ByRank() {

		List<String> list = new ArrayList<String>();
		list.add("rank");
		list.add("createTime");
		PageRequest page = new PageRequest(0, 64, new Sort(Direction.DESC, list));
		return randomRankDao.getTop100ByRank(page);
	}

	public RestResult getRandomRankBy(int rank, int playerId, int serverId) {

		List<RandomRank> list = getTop20ByRank(1, rank);
		RandomRank rRank = this.findByServerIdAndPlayerId(serverId, playerId);
		if (rRank != null) {
			list.remove(rRank);
		}

		List<TempRandomRank> newList = new ArrayList<TempRandomRank>();
		if (list.size() >= 3) {
			List<RandomRank> tempList = new ArrayList<RandomRank>();
			for (int i = 0; i < 3; i++) {
				Random random = new Random();
				int j = random.nextInt(list.size());
				System.out.println(j);
				tempList.add(list.get(j));
				list.remove(j);
			}
			for (RandomRank rr : tempList) {
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

	private List<RandomRank> getTop20ByRank(int base, int randomRank) {

		List<String> list = new ArrayList<String>();
		list.add("rank");
		list.add("createTime");
		PageRequest page = new PageRequest(0, 20, new Sort(Direction.DESC, list));
		// PageRequest d = new PageRequest(0, 30, new Sort(Direction.DESC, list));
		List<RandomRank> tempList = randomRankDao.getTop20ByNearbyRank(randomRank - 50 * base, randomRank + 50 * base, page);
		if (tempList.size() <= 3) {
			for (int i = 2; i < 100; i++) {
				int t = randomRank - 50 * i;
				if (t < 0) {
					t = 0;
				}
				tempList = randomRankDao.getTop20ByNearbyRank(t, randomRank + 50 * i, page);
				if (tempList.size() > 3) {
					return tempList;
				}
			}
		}
		return tempList;
	}
}
