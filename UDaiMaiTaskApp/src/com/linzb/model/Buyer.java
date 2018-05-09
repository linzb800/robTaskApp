package com.linzb.model;

/**
 * 淘宝帐号
 * @author linzb
 */
public class Buyer {

	private int id;
	private int userId;
	private int buyerId;//买号ID
	private String sex;//性别
	private String name;//名称
	private String taobaoName;//旺旺名称
	
	
	public Buyer() {
		super();
	}
	
	

	public Buyer(int id, int userId, int buyerId, String sex, String name, String taobaoName) {
		super();
		this.id = id;
		this.userId = userId;
		this.buyerId = buyerId;
		this.sex = sex;
		this.name = name;
		this.taobaoName = taobaoName;
	}



	public Buyer(int id,int buyerId, String sex, String name, String taobaoName) {
		this.id = id;
		this.buyerId = buyerId;
		this.sex = sex;
		this.name = name;
		this.taobaoName = taobaoName;
	}
	
	public Buyer(String sex, String name, String taobaoName) {
		super();
		this.sex = sex;
		this.name = name;
		this.taobaoName = taobaoName;
	}

	public String toInfo(){
		return "买号ID：" + buyerId + "\n淘宝号：" + taobaoName + "\n名称：" + name + "（" + sex + "）";  
	}
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}

	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getTaobaoName() {
		return taobaoName;
	}

	public void setTaobaoName(String taobaoName) {
		this.taobaoName = taobaoName;
	}
	
}
