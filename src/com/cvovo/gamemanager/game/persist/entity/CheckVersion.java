package com.cvovo.gamemanager.game.persist.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "checkVersion")
public class CheckVersion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String checkStr;

	public CheckVersion() {
	}

	public CheckVersion(String checkStr) {
		this.checkStr = checkStr;
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
	 * @return the notice
	 */
	public String getCheckStr() {
		return checkStr;
	}

	/**
	 * @param notice
	 *            the notice to set
	 */
	public void setCheckStr(String checkStr) {
		this.checkStr = checkStr;
	}
}
