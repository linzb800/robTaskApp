package com.linzb.model;

public class SuggestTask {

	private String title;// 标题
	private String client;// 客户端类型
	private boolean cart;// 是否要求加入购物车
	private boolean coll;// 是否要求收藏
	private boolean huabe;// 是否支持花呗
	private boolean card;// 是否支持信用卡
	private String sex;// 性别
	private double coin;// 金币
	private double price;// 价格
	private int taskId;// 任务ID

	public SuggestTask() {
		super();
	}

	public boolean isValid() {
		if (isEmpty(title) || isEmpty(client) || coin <= 0 || price <= 0 || taskId <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否为空
	 * 
	 * @param s
	 * @return
	 */
	public boolean isEmpty(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public String toInfo() {
		String wrap = "\n";
		StringBuilder sb = new StringBuilder();
		sb.append("标题：" + title).append(wrap);
		sb.append("操作端：" + client).append(wrap);
		sb.append("性别：" + (sex == null ? "不限" : sex)).append(wrap);
		sb.append("金币：" + coin).append(wrap);
		sb.append("价格：" + price).append(wrap);
		sb.append("要求加购物车：" + cart).append(wrap);
		sb.append("要求收藏：" + coll).append(wrap);
		sb.append("支持花呗：" + (huabe ? "是" : "否")).append(wrap);
		sb.append("支持信用卡：" + (card ? "是" : "否")).append(wrap);
		sb.append("任务ID：" + taskId);
		return sb.toString();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public double getCoin() {
		return coin;
	}

	public void setCoin(double coin) {
		this.coin = coin;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getClient() {
		return client;
	}

	public void setClientType(String clientType) {
		this.client = clientType;
	}

	public boolean isHuabe() {
		return huabe;
	}

	public void setHuabe(boolean huabe) {
		this.huabe = huabe;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public boolean isCard() {
		return card;
	}

	public void setCard(boolean card) {
		this.card = card;
	}

	public boolean isCart() {
		return cart;
	}

	public void setCart(boolean cart) {
		this.cart = cart;
	}

	public boolean isColl() {
		return coll;
	}

	public void setColl(boolean coll) {
		this.coll = coll;
	}

}
