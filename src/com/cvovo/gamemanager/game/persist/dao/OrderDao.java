package com.cvovo.gamemanager.game.persist.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cvovo.gamemanager.game.persist.entity.OrderInfo;

public interface OrderDao extends JpaRepository<OrderInfo, Long> {
	List<OrderInfo> findByStatus(int status);

	List<OrderInfo> findAll();

	OrderInfo findByOrderId(String orderId);

	List<OrderInfo> findByServerId(int serverId);

	List<OrderInfo> findByServerIdAndRoleIdAndSubjectId(int serverId, String roleId, String subjectId, Pageable pageable);

	OrderInfo findByCpOrderId(String cpOrderId);

	@Override
	public List<OrderInfo> findAll(Sort sort);

}
