package com.cvovo.gamemanager.game.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "randomRankConfig")
public class RandomRankConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = true, length = 100)
	private String lastLoginTime;

	@Column(nullable = true, length = 45)
	private int status;

	@Column(nullable = true, length = 45)
	private int intervalDay;

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
	 * @return the lastLoginTime
	 */
	public String getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * @param lastLoginTime
	 *            the lastLoginTime to set
	 */
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the intervalDay
	 */
	public int getIntervalDay() {
		return intervalDay;
	}

	/**
	 * @param intervalDay
	 *            the intervalDay to set
	 */
	public void setIntervalDay(int intervalDay) {
		this.intervalDay = intervalDay;
	}

}
