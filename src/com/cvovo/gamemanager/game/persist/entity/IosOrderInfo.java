package com.cvovo.gamemanager.game.persist.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ios_order_info")
public class IosOrderInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = true, length = 4)
	private Timestamp purchaseDate;
	@Column(nullable = true, length = 4)
	private Timestamp createOrderDate;
	@Column(nullable = true, length = 100)
	private String roleId;
	@Column(nullable = true, length = 100)
	private String roleName;
	@Column(nullable = true, length = 100)
	private String productId;
	@Column(nullable = true, length = 100)
	private String md5Receipt;
	@Column(nullable = true, length = 2)
	private String status; // 1:获取订单号；2:拿到回调；3成功处理游戏服务器

	@Column(nullable = true, length = 100)
	private String kind;
	@Column(nullable = true, length = 100)
	private String serverId; // 服务器ID

	@Column(nullable = true, length = 2)
	private String consumptionState;

	@Column(nullable = true, length = 100)
	private String developerPayload;

	@Column(nullable = true, length = 20)
	private String type;

	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind
	 *            the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	public IosOrderInfo() {

	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the purchaseDate
	 */
	public Timestamp getPurchaseDate() {
		return purchaseDate;
	}

	/**
	 * @param purchaseDate
	 *            the purchaseDate to set
	 */
	public void setPurchaseDate(Timestamp purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the md5Receipt
	 */
	public String getMd5Receipt() {
		return md5Receipt;
	}

	/**
	 * @param md5Receipt
	 *            the md5Receipt to set
	 */
	public void setMd5Receipt(String md5Receipt) {
		this.md5Receipt = md5Receipt;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the createOrderDate
	 */
	public Timestamp getCreateOrderDate() {
		return createOrderDate;
	}

	/**
	 * @param createOrderDate
	 *            the createOrderDate to set
	 */
	public void setCreateOrderDate(Timestamp createOrderDate) {
		this.createOrderDate = createOrderDate;
	}

	/**
	 * @return the roleId
	 */
	public String getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the consumptionState
	 */
	public String getConsumptionState() {
		return consumptionState;
	}

	/**
	 * @param consumptionState
	 *            the consumptionState to set
	 */
	public void setConsumptionState(String consumptionState) {
		this.consumptionState = consumptionState;
	}

	/**
	 * @return the developerPayload
	 */
	public String getDeveloperPayload() {
		return developerPayload;
	}

	/**
	 * @param developerPayload
	 *            the developerPayload to set
	 */
	public void setDeveloperPayload(String developerPayload) {
		this.developerPayload = developerPayload;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
