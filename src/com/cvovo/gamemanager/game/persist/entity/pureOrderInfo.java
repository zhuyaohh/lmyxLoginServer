package com.cvovo.gamemanager.game.persist.entity;

public class pureOrderInfo {
	private int consumptionState;

	private String developerPayload;

	private String kind;

	private int purchaseState;

	/**
	 * @return the consumptionState
	 */
	public int getConsumptionState() {
		return consumptionState;
	}

	/**
	 * @param consumptionState
	 *            the consumptionState to set
	 */
	public void setConsumptionState(int consumptionState) {
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

	/**
	 * @return the purchaseState
	 */
	public int getPurchaseState() {
		return purchaseState;
	}

	/**
	 * @param purchaseState
	 *            the purchaseState to set
	 */
	public void setPurchaseState(int purchaseState) {
		this.purchaseState = purchaseState;
	}

	/**
	 * @return the purchaseTimeMillis
	 */
	public Long getPurchaseTimeMillis() {
		return purchaseTimeMillis;
	}

	/**
	 * @param purchaseTimeMillis
	 *            the purchaseTimeMillis to set
	 */
	public void setPurchaseTimeMillis(Long purchaseTimeMillis) {
		this.purchaseTimeMillis = purchaseTimeMillis;
	}

	private Long purchaseTimeMillis;
}
