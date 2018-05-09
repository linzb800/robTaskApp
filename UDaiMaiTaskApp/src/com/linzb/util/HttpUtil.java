package com.linzb.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.linzb.model.User;
import com.linzb.model.Buyer;

public class HttpUtil {
	public static Random rand = new Random();
	public static final String SUGGEST_TASK_URL = "http://a.udamai.com/user/home/suggestTask";// 获取推荐任务地址
	public static final String TASK_DETAIL_URL = "http://a.udamai.com/task/detail?id=";// 详细任务
	public static final String ACCET_TASK_URL = "http://a.udamai.com/task/accetTask?id={0}&buyer_id={1}";// 接收任务
	public static long lastTm;// 最近获致到的时间

	public static HashMap<String, String> headerMap = null;
	public static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36";

	static{
		if (headerMap == null) {
			headerMap = new HashMap<String, String>();
			headerMap.put("Charsert", "UTF-8");
			headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			headerMap.put("Accept-Encoding", "gzip, deflate, sdch");
			headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
			headerMap.put("Cache-Control", "max-age=0");
			headerMap.put("Connection", "keep-alive");
			headerMap.put("Upgrade-Insecure-Requests", "1");
		}
	}
	
	public static HashMap<String, String> getCookieMap(String cookie) {
		HashMap<String, String> cookieMap = new HashMap<String, String>();
		String[] ss = cookie.split(";");
		for (String s : ss) {
			String[] kv = s.split("=");
			if (kv.length != 2)
				continue;
			cookieMap.put(kv[0].trim(), kv[1].trim());
		}
		return cookieMap;
	}
	

	/**
	 * @param url
	 * @param cookie
	 * @throws IOException
	 */
	public static Document get(String url,String cookie) throws IOException {
		System.out.println("URL：" + url);
		Connection conn = Jsoup.connect(url);
		conn.headers(headerMap);
		conn.cookies(getCookieMap(cookie));
		conn.userAgent(userAgent);
		Document doc = conn.get();
		return doc;
	}

	public static void main(String[] args) throws Exception {
		User userCond = new User();
		Buyer user1 = new Buyer(1,36949, "女", "杨锦明", "期待ing121");
		Buyer user2 = new Buyer(2,37325, "男", "林智滨", "linzb800");
		HashMap<Integer, Buyer> userMap = new HashMap<Integer, Buyer>();
		userMap.put(user1.getBuyerId(), user1);
		userMap.put(user2.getBuyerId(), user2);
		// Random rand = new Random();
		// for(int i=0;i<50;i++){
		// List<SuggestTask> list = getSuggestTask();
		// if(list != null && list.size() > 0){
		// for(int k=list.size();k>0;k--){
		// SuggestTask st = list.get(k-1);
		// // for(int k=0;k<list.size();k++){
		// // SuggestTask st = list.get(k);
		// if(userCond.isAccept(st)){
		// //Thread.sleep(100);
		// getTask(userMap, st.getTaskId());
		// break;
		// }
		// }
		// break;
		// }
		// long sleep = 1000 + rand.nextInt(2000);
		// System.out.println("休息：" + sleep + ",当前刷新次数：" + i);
		// Thread.sleep(sleep);
		// }

		// accetTask(121324, 21111);

		// Document ret = get(SUGGEST_TASK_URL);
		// System.out.println(ret.html());

		// TaskUtil.getTask(userMap,1);
		// System.out.println(doGet("http://www.baidu.com"));
		// Document doc = get(TASK_DETAIL_URL + 1488127);
		// Document doc = Jsoup.parse(htmlContext());
		// if(doc == null){
		// System.out.println("获取失败");
		// return;
		// }
		// System.out.println(doc.html());
		// Elements ele = doc.getElementsByClass("rec_content");

		// if(doc.html().indexOf("已被抢完") > 0){
		// System.out.println("已被抢完了");
		// }
		//
		// Elements ele = doc.getElementsByTag("p");
		// Elements ele = doc.getElementsByTag("input");
		// Iterator<Element> ite = ele.iterator();
		// while(ite.hasNext()){
		// Element e = ite.next();
		// System.out.println("=====================");
		// System.out.println(e.text());
		// System.out.println(e.text() + "tagName=" + e.tagName() + "," +
		// e.attributes().get("name") + "," + e.attributes().get("value"));

		/*
		 * String times = e.nextElementSibling().nextElementSibling().html();
		 * System.out.println(">>>" + times.substring("今日还可接手".length(),
		 * times.length() - 1) + "," +
		 * e.nextElementSibling().nextSibling().toString());
		 * 
		 * System.out.println("子节点数：" + e.childNodeSize()); for(Node
		 * n:e.childNodes()){ System.out.println(n.nodeName() + "," +
		 * n.outerHtml()); }
		 */
		// System.out.println(e.html());
		// }

		// for(String s:ele.eachText()){
		// System.out.println("--------");
		// System.out.println(s);
		// }

		// System.out.println(ele.html());

		// String s =
		// "<ul id=\"normalTaskList\" style=\" height:518px; overflow:auto\"><li> <span class=\"i1\"><img src=\"https://gd1.alicdn.com/imgextra/i3/3367246982/TB2UeOBaZH_F1JjSZFKXXbcvFXa_!!3367246982.png_400x400.jpg\" width=\"60\" height=\"60\"></span>"
		// +"<span class=\"i2\">" + "<h4>" +
		// " <i class=\"card_ico\" title=\"支持信用卡支付\">" +
		// "阿可法任务</h4>" +
		// "  <p class=\"cl_gray\">不限   &nbsp;" +
		// "	<span class=\"mg_l_12px\">" +
		// "  <i class=\"man_ico\" title=\"买号性别：女\"></i> " +
		// "  <i class=\"hb_ico\" title=\"支持花呗支付\"></i>" +
		//
		//
		// "			</span>" +
		// "			</p>" +
		// "  <p><span class=\"clorg f_28px\">11.96</span><span class=\"cl_gray\">金币</span> 返<span class=\"cl_red f_28px\">880.00</span><span class=\"cl_gray\">元</span> </p>"
		// +
		// "  </span>" +
		// "  <p class=\"btn_set\"><a href=\"/task/detail?id=1472367\" target=\"_blank\" class=\"button bigrounded medium orange\">马上抢</a></p>"
		// +
		// " </li>" +
		// "</ul>" +
		// "<ul id=\"hbTaskList\" style=\"display:none;height:518px; overflow:auto;\">"
		// +
		// "</ul>";
		//
		// List<SuggestTask> stList = parseSuggestTask(s);

	}
}