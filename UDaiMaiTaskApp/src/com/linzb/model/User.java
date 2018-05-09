package com.linzb.model;

import java.util.ArrayList;
import java.util.List;

import com.linzb.util.StringUtil;


public class User {
	
	private int id;
	private String loginName;
	private String udmName;
	private short minPrice = 1;//价格范围
	private short maxPrice = 300;
	private String clients;//允许客户端类型
	private String titleFilters = "";
	private byte huaba;//花呗,0允许(都可以),1不允许,2只要花呗
	private byte card;//信用卡,0允许(都可以),1不允许,2只要信用卡
	private byte cart;//加购物，0允许(都可以),1不允许,2只要加购物车
	private byte coll;//加购物，0允许(都可以),1不允许,2只要加收藏
	
	
	private List<String> clientList = new ArrayList<String>();//允许客户端类型
	private List<String> titleFilterList = new ArrayList<String>();//标题过虑
	
	
	public User() {
		titleFilterList.add("勿接");
		titleFilterList.add("福建");
		titleFilterList.add("京东");
		titleFilterList.add("大词");
		titleFilterList.add("成人用品");
		titleFilterList.add("两性");
		titleFilterList.add("情趣");
		clientList.add("手机端");
		clientList.add("平板端");
	}

	public boolean isValid(){
		if(id == 0 || StringUtil.isEmpty(loginName) || StringUtil.isEmpty(udmName)){
			return false;
		}
		return true;
	}
	
	public boolean isAccept(SuggestTask task,StringBuffer sb){
		try{
			System.out.println("判断是否能接受该任务：" + task.toInfo());
			//性别限制
//			String taskSex = task.getSex();
//			if(!StringUtil.isEmpty(taskSex) && !taskSex.equals(sex)){
//				System.out.println("性别不符，要求：" + taskSex);
//				return false;
//			}
			//价格
			if(!checkPrice(task)){
				sb.append("价格不符合");
				return false;
			}
			
			//客户端类型
			if(!checkCleint(task)){
				sb.append("操作端不符合");
				return false;
			}
			
			//花呗
//			if(!checkType(huaba, task.isHuabe())){
//				System.out.println("过滤花呗操作选择");
//				return false;
//			}
//			//信用卡
//			if(!checkType(card, task.isCard())){
//				System.out.println("过滤信用卡操作选择");
//				return false;
//			}
//			//加购物车
//			if(!checkType(cart, task.isCart())){
//				System.out.println("过滤加购物车操作选择");
//				return false;
//			}
//			//加收藏
//			if(!checkType(coll, task.isColl())){
//				System.out.println("过滤加收藏操作选择");
//				return false;
//			}
			
			//过滤标题
			if(!checkTitle(task)){
				sb.append("标题不符合");
				return false;
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	public boolean checkTitle(SuggestTask task){
		if(StringUtil.isEmpty(task.getTitle())){
			System.out.println("过虑标题：标题为空");
			return false;
		}
		
		for(String s:titleFilterList){
			if(task.getTitle().indexOf(s) >= 0){
				System.out.println("过虑标题：含有[" + s + "]");
				return false;
			}
		}
		return true;
	}
	
	public boolean checkCleint(SuggestTask task){
		if("不限".equals(task.getClient())){
			return true;
		}
		if(clientList != null && clientList.size() > 0){
			boolean can = false;
			for(String s:clientList){
				if(s.equals(task.getClient())){
					can = true;
					break;
				}
			}
			if(!can){
				System.out.println("过滤操作端：" + task.getClient());
				return false;
			}
		}
		return true;
	}
	
	public boolean checkPrice(SuggestTask task){
		if(task.getPrice() < minPrice || task.getPrice() > maxPrice){
			System.out.println("过虑价格：不在范围内" + task.getPrice());
			return false;
		}
		return true;
	}
	
	
	public String toInfo(){
		String wrap = "\n";
		StringBuilder sb = new StringBuilder();
		//sb.append("ID：" + id).append(wrap);
		sb.append("登录名：" + loginName).append(wrap);
		sb.append("帐号名称：" + udmName).append(wrap);
		sb.append("----------筛选条件----------").append(wrap);
		sb.append("允许操作端：");
		if(clientList == null){
			sb.append("不限").append(wrap);
		}else{
			StringBuilder sbt = new StringBuilder();
			for(String s:clientList){
				if(sbt.length() > 0)sbt.append("|");
				sbt.append(s);
			}
			sb.append(sbt.toString()).append(wrap);
		}
		sb.append("过滤标题：");
		if(titleFilterList == null || titleFilterList.size() == 0){
			sb.append("无过滤").append(wrap);
		}else{
			StringBuilder sbt = new StringBuilder();
			for(String s:titleFilterList){
				if(sbt.length() > 0)sbt.append("|");
				sbt.append(s);
			}
			sb.append(sbt.toString()).append(wrap);
		}
		sb.append("价格范围：" + minPrice + "-" + maxPrice).append(wrap);
		sb.append("花呗：" + getTypeDes(huaba)).append(wrap);
		sb.append("信用卡：" + getTypeDes(card)).append(wrap);
		sb.append("加购物车：" + getTypeDes(cart)).append(wrap);
		sb.append("加收藏：" + getTypeDes(coll)).append(wrap);
		return sb.toString();
	}
	
	
	private boolean checkType(byte type,boolean val){
		if(type == 0){//允许
			return true;
		}else if(type == 1){//不允许
			if(val){
				return false;
			}
			return true;
		}else if(type == 2){
			if(val)return true;
			return false;
		}else{
			return false;//未类型
		}
	}
	
	private String getTypeDes(byte type){
		if(type == 0){
			return "允许";
		}else if(type == 1){
			return "不允许";
		}else if(type == 2){
			return "只允许";
		}else{
			return "示知类型";
		}
	}
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public List<String> getClientList() {
		return clientList;
	}
	public void setClientList(List<String> clientList) {
		this.clientList = clientList;
	}
	public List<String> getTitleFilterList() {
		return titleFilterList;
	}
	public void setTitleFilterList(List<String> titleFilterList) {
		this.titleFilterList = titleFilterList;
	}

	public short getMinPrice() {
		return minPrice;
	}


	public void setMinPrice(short minPrice) {
		this.minPrice = minPrice;
	}


	public short getMaxPrice() {
		return maxPrice;
	}


	public void setMaxPrice(short maxPrice) {
		this.maxPrice = maxPrice;
	}


	public byte getHuaba() {
		return huaba;
	}
	public void setHuaba(byte huaba) {
		this.huaba = huaba;
	}
	public byte getCard() {
		return card;
	}
	public void setCard(byte card) {
		this.card = card;
	}
	public byte getCart() {
		return cart;
	}
	public void setCart(byte cart) {
		this.cart = cart;
	}
	public byte getColl() {
		return coll;
	}
	public void setColl(byte coll) {
		this.coll = coll;
	}

	public String getTitleFilters() {
		return titleFilters;
	}

	public void setTitleFilters(String titleFilters) {
		this.titleFilters = titleFilters;
		
		if(!StringUtil.isEmpty(titleFilters)){
			String[] s = this.titleFilters.split("\\|");
			for(String ss:s){
				titleFilterList.add(ss);
			}
		}
	}

	public String getClients() {
		return clients;
	}


	public void setClients(String clients) {
		this.clients = clients;
		if(!StringUtil.isEmpty(clients)){
			String[] s = this.clients.split("\\|");
			for(String ss:s){
				clientList.add(ss);
			}
		}
	}

	public String getUdmName() {
		return udmName;
	}

	public void setUdmName(String udmName) {
		this.udmName = udmName;
	}
}
