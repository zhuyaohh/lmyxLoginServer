package com.cvovo.gamemanager.game.persist.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = true, length = 65)
	private int userId;

	@Column(nullable = true, length = 200)
	private String uin;

	@Column(nullable = true, length = 200, unique = true)
	private String nick;

	@Column(nullable = true, length = 65)
	private String sUid;

	@Column(nullable = true, length = 100)
	private String fbUserId;

	@Column(nullable = true, length = 20)
	private String sex;

	@Column(nullable = true, length = 100)
	private String password;

	@Column(nullable = true, length = 20)
	private String type;

	@Column(nullable = true, length = 4)
	private Timestamp lastLoginTime;

	@Column(nullable = true, length = 4)
	private Timestamp createTime;

	@Column(nullable = true, length = 200)
	private String device;

	@Column(nullable = true, length = 200)
	private String androidId;

	@Column(nullable = true, length = 200)
	private String mail;

	@Column(nullable = true, length = 200)
	private Double coinNft;

	@Column(nullable = true, length = 200)
	private Double coinWin;

	@Column(nullable = true, length = 200)
	private String platformId;

	public Long getId() {
		return id;
	}

	public Double getCoinNft() {
		return coinNft;
	}

	public void setCoinNft(Double coinNft) {
		this.coinNft = coinNft;
	}

	public Double getCoinWin() {
		return coinWin;
	}

	public void setCoinWin(Double coinWin) {
		this.coinWin = coinWin;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public Account() {
		this.lastLoginTime = new Timestamp(System.currentTimeMillis());
		this.createTime = new Timestamp(System.currentTimeMillis());
	};

	public Account(int userId, String nick, String sUid, String sex, String password, String type, String device) {
		this.userId = userId;
		this.nick = nick;
		this.sUid = sUid;
		this.sex = sex;
		this.password = password;
		this.type = type;
		this.device = device;
		this.lastLoginTime = new Timestamp(System.currentTimeMillis());
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public Account(String uin, String sUid, String sex, String password, String type, String device) {
		this.uin = uin;
		this.sUid = sUid;
		this.sex = sex;
		this.password = password;
		this.type = type;
		this.device = device;
		this.lastLoginTime = new Timestamp(System.currentTimeMillis());
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public Account(int userId, String nick, String sUid, String sex, String password, String type) {
		this.userId = userId;
		this.nick = nick;
		this.sUid = sUid;
		this.sex = sex;
		this.password = password;
		this.type = type;
		this.lastLoginTime = new Timestamp(System.currentTimeMillis());
		this.createTime = new Timestamp(System.currentTimeMillis());
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getsUid() {
		return sUid;
	}

	public void setsUid(String sUid) {
		this.sUid = sUid;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	 * @return the lastLoginTime
	 */
	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * @param lastLoginTime the lastLoginTime to set
	 */
	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * @return the createTime
	 */
	public Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the device
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device the device to set
	 */
	public void setDevice(String device) {
		this.device = device;
	}

	/**
	 * @return the fbUserId
	 */
	public String getFbUserId() {
		return fbUserId;
	}

	/**
	 * @param fbUserId the fbUserId to set
	 */
	public void setFbUserId(String fbUserId) {
		this.fbUserId = fbUserId;
	}

	/**
	 * @return the uin
	 */
	public String getUin() {
		return uin;
	}

	/**
	 * @param uin the uin to set
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the androidId
	 */
	public String getAndroidId() {
		return androidId;
	}

	/**
	 * @param androidId the androidId to set
	 */
	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	};

}
