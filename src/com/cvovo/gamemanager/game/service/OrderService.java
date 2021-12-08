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

import com.cvovo.gamemanager.game.persist.dao.OrderDao;
import com.cvovo.gamemanager.game.persist.entity.OrderInfo;

@Service
@Transactional
public class OrderService {
	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
	@Autowired
	private OrderDao orderDao;

	public boolean create(OrderInfo orderInfo) {
		logger.info("=========存储=========");
		OrderInfo exist = orderDao.findByOrderId(orderInfo.getOrderId());
		if (exist == null) {
			logger.info("启动保存================");
			orderDao.save(orderInfo);
			return true;
		}
		return false;
	}

	public boolean createStatus(OrderInfo orderInfo) {
		logger.info("=========存储=========");
		OrderInfo exist = orderDao.findByOrderId(orderInfo.getOrderId());
		if (exist == null) {
			logger.info("启动保存================");
			orderDao.save(orderInfo);
			return true;
		} else if (exist != null && exist.getStatus() != 3) {
			return true;
		}
		return false;
	}

	
	public void createStatusByGuoN(OrderInfo orderInfo) {
		logger.info("=========存储=========");
		orderDao.save(orderInfo);
	}
	public OrderInfo getOrderInfoByInfo(String serverId, String roleId, String subjectId) {
		PageRequest page = new PageRequest(0, 100, new Sort(Direction.DESC, "date"));
		List<OrderInfo> orderList = orderDao.findByServerIdAndRoleIdAndSubjectId(Integer.parseInt(serverId), roleId, subjectId, page);
		if (orderList != null && orderList.size() > 0) {
			return orderList.get(0);
		}
		return null;
	}

	public OrderInfo checkSaveOrCreate(OrderInfo orderInfo) {
		logger.info("=========判断是否存储=========");
		OrderInfo exist = orderDao.findByOrderId(orderInfo.getOrderId());
		if (exist == null) {
			logger.info("启动保存================");
			orderDao.save(orderInfo);
			return orderInfo;
		}
		return exist;
	}

	public OrderInfo getOrderByOrderId(String orderId) {
		try {
			return orderDao.findByOrderId(orderId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public OrderInfo getOrderByCpOrderId(String cpOrderId) {
		return orderDao.findByCpOrderId(cpOrderId);
	}

	public void update(OrderInfo orderInfo) {
		orderDao.save(orderInfo);
	}

	public int getTotalMoneyByServerId(int serverId) {
		List<OrderInfo> list = orderDao.findByServerId(serverId);
		int total = 0;
		if (list != null && list.size() > 0) {
			for (OrderInfo order : list) {
				if (order.getStatus() == 3) {
					total += Integer.parseInt(order.getTotalFee());
				}
			}
		}
		return total;
	}
}
