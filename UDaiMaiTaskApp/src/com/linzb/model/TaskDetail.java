package com.linzb.model;

import java.util.HashMap;

import com.linzb.util.StringUtil;

public class TaskDetail {

	private int taskId;
	private String pay;//付款方式
	private String client;//客户端
	private String platform;//平台
	private String buyerSex;//买号性别
	private String shopName;//店铺名称
	private String aliww;//旺旺号
	private String taskPrice;//任务价格
	private String context;//补充说明
	private HashMap<Integer, Byte> buyerMap = new HashMap<Integer, Byte>();//买号信息
	
	private Buyer buyer;//买号
	
	
	public String toInfo(){
		String wrap = "\r\n";
		StringBuilder sb = new StringBuilder();
		sb.append("任务ID：" + taskId).append(wrap);
		sb.append("付款方式：" + pay).append(wrap);
		sb.append("操作端：" + client).append(wrap);
		sb.append("平台：" + platform).append(wrap);
		sb.append("买号性别：" + buyerSex).append(wrap);
		sb.append("店铺名称：" + shopName).append(wrap);
		sb.append("店铺旺旺号：" + aliww).append(wrap);
		sb.append("任务价格：" + taskPrice).append(wrap);
		sb.append("补充说明：" + (StringUtil.isEmpty(context)?"无":context));
		/*sb.append("接单帐号信息：");
		for(Entry<Integer, Byte> en:buyerMap.entrySet()){
			sb.append(en.getKey() + "可接次数" + en.getValue() + "|");
		}*/
		
		return sb.toString();
	}
	
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getPay() {
		return pay;
	}
	public void setPay(String pay) {
		this.pay = pay;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getBuyerSex() {
		return buyerSex;
	}
	public void setBuyerSex(String buyerSex) {
		this.buyerSex = buyerSex;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getAliww() {
		return aliww;
	}
	public void setAliww(String aliww) {
		this.aliww = aliww;
	}
	public String getTaskPrice() {
		return taskPrice;
	}
	public void setTaskPrice(String taskPrice) {
		this.taskPrice = taskPrice;
	}
	public HashMap<Integer, Byte> getBuyerMap() {
		return buyerMap;
	}
	public void setBuyerMap(HashMap<Integer, Byte> buyerMap) {
		this.buyerMap = buyerMap;
	}

	public Buyer getBuyer() {
		return buyer;
	}

	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}
	
}
