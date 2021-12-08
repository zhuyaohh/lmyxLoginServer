package com.cvovo.gamemanager.game.persist.entity;

public class ReceiptInfo {
	private String receipt;
	private String roleId;
	private String serverid;
	private String type;

	/**
	 * @return the receipt
	 */
	public String getReceipt() {
		return receipt;
	}

	/**
	 * @param receipt
	 *            the receipt to set
	 */
	public void setReceipt(String receipt) {
		this.receipt = receipt;
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
	 * @return the serverid
	 */
	public String getServerid() {
		return serverid;
	}

	/**
	 * @param serverid
	 *            the serverid to set
	 */
	public void setServerid(String serverid) {
		this.serverid = serverid;
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

}
