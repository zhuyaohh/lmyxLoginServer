package com.cvovo.gamemanager.game.persist.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "order_info")
public class OrderInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(nullable = false, length = 500, unique = true)
	private String orderId;
	@Column(nullable = true, length = 45)
	private String cpOrderId;
	@Column(nullable = true, length = 100)
	private String dnyOrderId;
	@Column(nullable = true, length = 45)
	private String totalFee;
	@Column(nullable = true, length = 45)
	private String acturalFee;
	@Column(nullable = true, length = 45)
	private int serverId;
	@Column(nullable = true, length = 100)
	private String subjectId;
	@Column(nullable = true, length = 2)
	private int status; // 1:获取订单号；2:拿到回调；3成功处理游戏服务器
	@Column(nullable = true, length = 45)
	private String type;
	@Column(nullable = true, length = 4)
	private Timestamp date;
	@Column(nullable = true, length = 65)
	private int accountId;
	@Column(nullable = true, length = 65)
	private String userId;
	@Column(nullable = true, length = 4)
	private Timestamp finishDate;
	@Column(nullable = true, length = 50)
	private String priceUnit;
	@Column(nullable = true, length = 100)
	private String roleId;
	@Column(nullable = true, length = 100)
	private String roleName;
	@Column(nullable = true, length = 200)
	private String remak;

	/**
	 * @return the priceUnit
	 */
	public String getPriceUnit() {
		return priceUnit;
	}

	/**
	 * @param priceUnit the priceUnit to set
	 */
	public void setPriceUnit(String priceUnit) {
		this.priceUnit = priceUnit;
	}

	public OrderInfo() {
	}

	public OrderInfo(String orderId, String totalFee, String acturalFee, int serverId, String subjectId, String type, int accountId) {
		this.orderId = orderId;
		this.totalFee = totalFee;
		this.acturalFee = acturalFee;
		this.serverId = serverId;
		this.subjectId = subjectId;
		this.accountId = accountId;
		this.type = type;
		this.status = 1;
		this.date = new Timestamp(System.currentTimeMillis());
	}

	public OrderInfo(String orderId, String totalFee, String acturalFee, int serverId, String subjectId, String type, int accountId, String cpOrderId, String dnyOrderId) {
		this.orderId = orderId;
		this.totalFee = totalFee;
		this.acturalFee = acturalFee;
		this.serverId = serverId;
		this.subjectId = subjectId;
		this.accountId = accountId;
		this.type = type;
		this.status = 1;
		this.cpOrderId = cpOrderId;
		this.dnyOrderId = dnyOrderId;
		this.date = new Timestamp(System.currentTimeMillis());
	}

	public OrderInfo(String orderId, String totalFee, String acturalFee, int serverId, String subjectId, String type, String uin, String cpOrderId, String dnyOrderId) {
		this.orderId = orderId;
		this.totalFee = totalFee;
		this.acturalFee = acturalFee;
		this.serverId = serverId;
		this.subjectId = subjectId;
		this.userId = uin;
		this.type = type;
		this.status = 1;
		this.cpOrderId = cpOrderId;
		this.dnyOrderId = dnyOrderId;
		this.date = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the totalFee
	 */
	public String getTotalFee() {
		return totalFee;
	}

	/**
	 * @param totalFee the totalFee to set
	 */
	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	/**
	 * @return the acturalFee
	 */
	public String getActuralFee() {
		return acturalFee;
	}

	/**
	 * @param acturalFee the acturalFee to set
	 */
	public void setActuralFee(String acturalFee) {
		this.acturalFee = acturalFee;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the date
	 */
	public Timestamp getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Timestamp date) {
		this.date = date;
	}

	/**
	 * @return the serverId
	 */
	public int getServerId() {
		return serverId;
	}

	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the subjectId
	 */
	public String getSubjectId() {
		return subjectId;
	}

	/**
	 * @param subjectId the subjectId to set
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the accountId
	 */
	public int getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the cpOrderId
	 */
	public String getCpOrderId() {
		return cpOrderId;
	}

	/**
	 * @param cpOrderId the cpOrderId to set
	 */
	public void setCpOrderId(String cpOrderId) {
		this.cpOrderId = cpOrderId;
	}

	/**
	 * @return the finishDate
	 */
	public Timestamp getFinishDate() {
		return finishDate;
	}

	/**
	 * @param finishDate the finishDate to set
	 */
	public void setFinishDate(Timestamp finishDate) {
		this.finishDate = finishDate;
	}

	/**
	 * @return the dnyOrderId
	 */
	public String getDnyOrderId() {
		return dnyOrderId;
	}

	/**
	 * @param dnyOrderId the dnyOrderId to set
	 */
	public void setDnyOrderId(String dnyOrderId) {
		this.dnyOrderId = dnyOrderId;
	}

	/**
	 * @return the roleId
	 */
	public String getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the remak
	 */
	public String getRemak() {
		return remak;
	}

	/**
	 * @param remak the remak to set
	 */
	public void setRemak(String remak) {
		this.remak = remak;
	}

}
