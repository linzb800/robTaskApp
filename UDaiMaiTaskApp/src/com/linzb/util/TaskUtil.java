package com.linzb.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.webkit.WebView.FindListener;

import com.linzb.model.SuggestTask;
import com.linzb.model.TaskDetail;
import com.linzb.model.User;
import com.linzb.model.Buyer;

public class TaskUtil {

	public static Random rand = new Random();

	/**
	 * 获取推荐任务
	 * 
	 * @throws Exception
	 */
	public static List<SuggestTask> getSuggestTask(String cookie) throws Exception {
		Document doc = HttpUtil.get(HttpUtil.SUGGEST_TASK_URL,cookie);
		// Document doc = Jsoup.parse(suggertDemo());
		if (doc == null || doc.html().indexOf("sorry,活动产品都被抢光了") > -1) {
			System.out.println("sorry,活动产品都被抢光了");
			return null;
		}
		// System.out.println(doc.html());

		List<SuggestTask> list = new ArrayList<SuggestTask>();
		Element ele = doc.getElementById("normalTaskList");
		Elements liEle = ele.getElementsByTag("li");
		for (Element e : liEle) {
			SuggestTask st = new SuggestTask();

			// 标题
			Elements h4ele = e.getElementsByTag("h4");
			st.setTitle(h4ele.text().trim());

			/*
			 * if(!cond.checkTitle(st)){ continue; }
			 */

			boolean isSex = false;
			Elements h4iele = h4ele.get(0).getElementsByTag("i");
			for (Element ie : h4iele) {
				if (ie.attributes().get("class").equals("sex_ico")) {// 成人用口
					isSex = true;
					break;
				} else if (ie.attributes().get("class").equals("cart_ico")) {// 加购物车
					st.setCart(true);
				} else if (ie.attributes().get("class").equals("fav_ico")) {// 加收藏
					st.setColl(true);
				} else if (ie.attributes().get("class").equals("card_ico")) {// 信用卡
					st.setCard(true);
				} else if (ie.attributes().get("class").equals("hb_ico")) {// 花呗
					st.setHuabe(true);
				}
			}

			// 直接过滤成人用品任务
			if (isSex) {
				System.out.println("过虑任务：成人用品任务");
				continue;
			}

			// p
			Elements pele = e.getElementsByTag("p");
			if (pele.size() != 3) {
				throw new Exception("推荐任务获取P标签不是3个：" + pele.html());
			}
			// 操作端，性别，支持花呗，信用卡等...
			Element p1 = pele.get(0);
			String clientText = p1.text().trim();
			String[] sa = clientText.split("&");
			String cname = sa[0].trim();
			if (cname.equals("不限") || cname.equals("手机端") || cname.equals("平板") || cname.equals("电脑端")) {
				st.setClientType(cname);
			}

			/*
			 * if(!cond.checkCleint(st)){ continue; }
			 */

			Elements iele = p1.getElementsByTag("i");
			for (Element ie : iele) {
				if (ie.attributes().get("class").equals("man_ico") && ie.attributes().get("title").endsWith("男")) {
					st.setSex("男");
				} else if (ie.attributes().get("class").equals("women_ico") && ie.attributes().get("title").endsWith("女")) {
					st.setSex("女");
				}
			}

			// 金币,金额
			Element p2 = pele.get(1);
			Elements p2pan = p2.getElementsByTag("span");
			if (p2pan.size() != 4) {
				throw new Exception("解析金额标签pan不是4个：" + p2.html());
			}
			st.setCoin(Double.parseDouble(p2pan.get(0).text()));
			st.setPrice(Double.parseDouble(p2pan.get(2).text()));

			/*
			 * if(!cond.checkPrice(st)){ continue; }
			 */

			// 任务ID
			Element p3 = pele.get(2);
			Elements aele = p3.getElementsByTag("a");
			String href = aele.get(0).attributes().get("href");
			st.setTaskId(Integer.parseInt(href.substring(href.indexOf("=") + 1)));

			list.add(st);
			System.out.println(st.toInfo());
		}

		//打乱
		Collections.shuffle(list);
		return list;
	}

	public static TaskDetail getTask(String cookie,List<Buyer> buyerList, int taskId) throws Exception {
		String url = HttpUtil.TASK_DETAIL_URL + taskId;
		if (buyerList == null || buyerList.size() == 0) {
			throw new Exception("无买号ID信息");
		}

		Document doc = HttpUtil.get(url,cookie);
//		Document doc = Jsoup.parse(taskDetailDemo());
		TaskDetail task = new TaskDetail();
		task.setTaskId(taskId);

		String docHtml = doc.html();
		if (docHtml.indexOf("已被抢完") > 0) {
			throw new Exception("慢了一步，已被抢完咯");
		}
		// System.out.println(docHtml);
		// 是否马上付款
		boolean nowPay = false;// 是否有马上付款
		Elements h2ele = doc.getElementsByTag("h2");
		for (Element h2 : h2ele) {
			String h2html = h2.text();
			if (h2html.startsWith("活动类型") && h2html.indexOf("马上付款") > 0) {
				nowPay = true;
			}
		}
		if (!nowPay) {
			throw new Exception("不是马上付款");
		}

		// 获取选择买号信息
		HashMap<Integer, Byte> filterBuyerId = new HashMap<>();
		HashMap<String,Integer> filterTaoBao = new HashMap<String,Integer>();
		HashMap<String,Integer> filterName = new HashMap<String,Integer>();
		Elements inputEles = doc.getElementsByTag("input");
		for (Element e : inputEles) {
			String name = e.attributes().get("name");
			if(name != null && name.startsWith("buyer_name_")){
				int buyerId = Integer.parseInt(name.substring("buyer_name_".length()));
				byte times = 0;
				String acStr = "";
				String taoname = "";
				String valName = e.attributes().get("value");
				if(e.nextElementSibling() == null){//单个号
					acStr = e.previousElementSibling().previousElementSibling().html();
					acStr = acStr.substring(0,acStr.indexOf("（"));
					taoname = e.previousElementSibling().previousElementSibling().previousElementSibling().html();
				}else{
					//多个号
					acStr = e.nextElementSibling().nextElementSibling().html();
					taoname =  e.nextElementSibling().nextSibling().toString();
				}
				
				if("".equals(taoname.trim())){
					throw new Exception("解析不到淘宝号");
				}
				
				taoname = taoname.substring(0, taoname.indexOf("（")).trim();
				
				String ss = "今日还可接手";
				String timesStr = acStr.substring(ss.length(),ss.length() + 1);
				times = Byte.parseByte(timesStr);
				System.out.println(buyerId + "(" + valName + ")" + taoname + "," + acStr);
				if(times > 0){
					filterBuyerId.put(buyerId, times);
					filterTaoBao.put(taoname,buyerId);
					filterName.put(valName,buyerId);
				}
			}
		}

		if (filterBuyerId.size() == 0) {
			throw new Exception("无买号可接");
		}

		Element shopNameEle = doc.getElementById("shopName");
		if (shopNameEle == null) {
			System.out.println("注意：获取不到店铺名称");
		} else {
			task.setShopName(shopNameEle.text());
		}
		Element wwEle = doc.getElementById("aliww");
		if (wwEle == null) {
			System.out.println("注意：获取不到旺旺号");
		} else {
			task.setAliww(wwEle.text());
		}
		Element priceEle = doc.getElementById("taskPrice");
		if (priceEle == null) {
			System.out.println("注意：获取不到任务价格");
		} else {
			task.setTaskPrice(priceEle.text());
		}

		Elements ele = doc.getElementsByTag("p");
		Iterator<Element> its = ele.iterator();
		while (its.hasNext()) {
			Element e = its.next();
			String html = e.html().trim();

			if (html.indexOf("成人用品") > 0) {
				// System.out.println("成人用品任务：" + html);
				throw new Exception("成人用品任务");
			}

			if (html.startsWith("付款方式")) {
				task.setPay(e.text());
			} else if (html.startsWith("操作端")) {
				Elements es = e.getElementsByClass("cl_red");
				if (es != null) {
					task.setClient(es.text());
				}
			} else if (html.startsWith("平台")) {
				Elements es = e.getElementsByClass("cl_red");
				if (es != null) {
					task.setPlatform(es.text().trim());
					if (task.getPlatform().indexOf("jd.com") >= 0) {
						System.out.println("过滤京东平台");
						throw new Exception("过滤京东平台");
					}
				}
			} else if (html.startsWith("买号性别")) {
				Elements es = e.getElementsByClass("cl_red");
				if (es != null) {
					task.setBuyerSex(es.text().trim());
				}
			}else if(html.startsWith("补充说明")){
				Element ne = e.nextElementSibling();
				if(ne != null && ne.attributes().get("introduction") != null){
					task.setContext(ne.text());
				}else{
					task.setContext("注意：获取不到补充说明");
				}
			} else {

			}
		}

		if (StringUtil.isEmpty(task.getBuyerSex())) {
			throw new Exception("解析不到买号性别");
		}

		System.out.println(task.toInfo());
		
		//封装买号ID
		HashMap<Integer, Buyer> buyerMap = new HashMap<>();
		for(Buyer b:buyerList){
			if(b.getBuyerId() > 100){
				buyerMap.put(b.getBuyerId(), b);
				continue;//已经买号ID了
			}
			Integer bid1 = filterName.get(b.getName());
			Integer bid2 = filterTaoBao.get(b.getTaobaoName());
			if(bid1 != null && bid2 != null && bid1.intValue() == bid2.intValue()){
				b.setBuyerId(bid1);
				buyerMap.put(b.getBuyerId(), b);
			}
		}

		Buyer selectUser = getBuyer(filterBuyerId, buyerMap, task.getBuyerSex());
		if (selectUser == null) {
			throw new Exception("找不到适合的买号");
		}

		System.out.println("接手信息->" + selectUser.toInfo());
		task.setBuyer(selectUser);
		// accetTask(task.getTaskId(), selectUser.getUserId());

		return task;
	}

	/**
	 * 获取买号
	 */
	public static Buyer getBuyer(HashMap<Integer, Byte> buyersId,HashMap<Integer,Buyer> buyerMap, String sex) {
		List<Integer> list = new ArrayList<Integer>();
		int val = 0;// 当前可接手最大次数
		if ("不限".equals(sex)) {
			for (Entry<Integer, Byte> en : buyersId.entrySet()) {
				if (en.getValue() == 0)
					continue;
				if (list.size() == 0 || en.getValue() > val) {
					list.clear();
					list.add(en.getKey());
					val = en.getValue();
				} else if (en.getValue() == val) {
					list.add(en.getKey());
				}
			}
		} else if ("男".equals(sex) || "女".equals(sex)) {
			for (Entry<Integer, Byte> en : buyersId.entrySet()) {
				if (en.getValue() == 0)
					continue;
				Buyer ui = buyerMap.get(en.getKey());
				if (ui == null || !ui.getSex().equals(sex))
					continue;// 性别不一样
				if (list.size() == 0 || en.getValue() > val) {
					list.clear();
					list.add(en.getKey());
					val = en.getValue();
				} else if (en.getValue() == val) {
					list.add(en.getKey());
				}
			}
		} else {
			System.out.println("未知任务性别：" + sex);
			return null;
		}

		if (list.size() == 0) {
			System.out.println("性别筛选后无符合");
			return null;
		} else if (list.size() == 1) {
			return buyerMap.get(list.get(0));
		} else {
			// 随机一个
			int idx = rand.nextInt(list.size());
			return buyerMap.get(list.get(idx));
		}
	}

	/**
	 * 抢任务
	 * 
	 * @throws IOException
	 * @throws JSONException 
	 */
	public static JSONObject accetTask(String cookie,int taskId, int buyerId) throws Exception {
		
		if(taskId <1000 || buyerId < 1000){
			System.out.println("无效的任务，买号ID");
			return null;
		}
		
		String url = MessageFormat.format(HttpUtil.ACCET_TASK_URL, new Object[] { taskId + "", buyerId + "" });
		Document doc = HttpUtil.get(url,cookie);
//		Document doc =Jsoup.parse("{\"code\":1,\"msg\":\"失败啦\"}");
		System.out.println("doc=" + doc.text());
		String html = doc.getElementsByTag("body").text();
		System.out.println("html=" +html);
		JSONObject json = new JSONObject(html);
		System.out.println("json=" + json.toString());
		return json;
	}

	public static String suggertDemo() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head></head>");
		sb.append(" <body>");
		sb.append(" <ul id=\"normalTaskList\" style=\" height:518px; overflow:auto\"> ");
		sb.append(" <li> <span class=\"i1\">" + "<img src=\"https://img.alicdn.com/imgextra/i4/3430976830/TB2qYgAiv5TBuNjSspcXXbnGFXa_!!3430976830-2-item_pic.png_430x430q90.jpg\" width=\"60\" height=\"60\"></span>" + " <span class=\"i2\"> " + "<h4> <i class=\"fav_ico\" title=\"收藏再拍\"></i>按规矩做任务使用拍立淘立即冻结关小黑屋</h4> " + "<p class=\"cl_gray\"> 手机端&nbsp; <span class=\"mg_l_12px\"> " + "<i class=\"man_ico\" title=\"买号性别：男\"></i></span> <span class=\"mg_l_12px\"> </span> </p> " + "<p><span class=\"clorg f_28px\">8.74</span><span class=\"cl_gray\">金币</span> 返<span class=\"cl_red f_28px\">49.00</span><span class=\"cl_gray\">元</span> </p> </span>" + " <p class=\"btn_set\"><a href=\"/task/detail?id=1504063\" target=\"_blank\" class=\"button bigrounded medium orange\">马上抢</a></p> " + "</li> ");
		sb.append(" </ul> ");
		sb.append(" <ul id=\"hbTaskList\" style=\"display:none;height:518px; overflow:auto;\"> ");
		sb.append(" </ul>");
		sb.append(" </body>");
		sb.append("</html>");
		return sb.toString();
	}
	
	public static String getJsonDemo()	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head></head>");
		sb.append(" <body>");
		sb.append("{\"code\":1,\"msg\":\"失败啦\"}");
		sb.append(" </body>");
		sb.append("</html>");
		
		return sb.toString();
	}

	public static String taskDetailDemo() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head> ");
		sb.append("<title>任务编号：sea_liujing-20180412102307的详情--大麦家-一个购物更省钱的平台</title>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> ");
		sb.append(" <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\"> ");
		sb.append(" <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> ");
		sb.append(" <link rel=\"stylesheet\" href=\"/style/main.css?v=2.4\"> ");
		sb.append(" <link rel=\"stylesheet\" href=\"/style/form.css?v=2.4\"> ");
		sb.append(" <script type=\"text/javascript\" src=\"/plugins/jquery/jquery.min.js?v=2.4\"></script> ");
		sb.append(" <script type=\"text/javascript\" src=\"/plugins/jquery/jquery-migrate.min.js?v=2.4\"></script> ");
		sb.append(" <script type=\"text/javascript\" src=\"/plugins/layer/layer.js?v=2.4\"></script> ");
		sb.append(" <link rel=\"stylesheet\" href=\"/plugins/layer/skin/layer.css?v=2.4\"> ");
		sb.append(" <script type=\"text/javascript\" src=\"/plugins/validator/jquery.validator.js?v=2.4\"></script> ");
		sb.append(" <script type=\"text/javascript\" src=\"/plugins/validator/local/zh_CN.js?v=2.4\"></script> ");
		sb.append(" <link rel=\"stylesheet\" href=\"/plugins/validator/jquery.validator.css?v=2.4\"> ");
		sb.append(" <script type=\"text/javascript\" src=\"/plugins/webuploader/webuploader.min.js?v=2.4\"></script> ");
		sb.append(" <link rel=\"stylesheet\" href=\"/plugins/webuploader/webuploader.css?v=2.4\"> ");
		sb.append("<script type=\"text/javascript\" src=\"/plugins/fancybox/jquery.fancybox.pack.js?v=2.4\"></script> ");
		sb.append(" <link rel=\"stylesheet\" href=\"/plugins/fancybox/jquery.fancybox.css?v=2.4\"> ");
		sb.append("<script type=\"text/javascript\" src=\"/js/commons.js?v=2.4\"></script> ");
		sb.append("<script type=\"text/javascript\" src=\"/js/task.js?v=2.4\"></script> ");
		sb.append(" <script type=\"text/javascript\" src=\"/js/upload.js?v=2.4\"></script> ");
		sb.append("<style type=\"text/css\">");
		sb.append("body{");
		sb.append("background:#333;");
		sb.append("}");
		sb.append("img{");
		sb.append("border-radius: .3em;");
		sb.append("-moz-border-radius: .3em;");
		sb.append("-webkit-border-radius: .3em;");
		sb.append("}");
		sb.append("</style> ");
		sb.append(" <script type=\"text/javascript\">");
		sb.append("$(function(){");
		sb.append("$(\"img[src^='/upload']\").each(function(){");
		sb.append("$(this).css({cursor:\"pointer\"});");
		sb.append("});");
		sb.append("$(\"img[src^='/images']\").each(function(){");
		sb.append("$(this).css({cursor:\"pointer\"});");
		sb.append("});");
		sb.append("$(\"img[src^='/upload']\").click(function(){");
		sb.append("viewPic($(this).attr(\"src\"));");
		sb.append("});");
		sb.append("$(\"img[src^='http']\").each(function(){");
		sb.append("$(this).css({cursor:\"pointer\"});");
		sb.append("});");
		sb.append("$(\"img[src^='http']\").click(function(){");
		sb.append("viewPic($(this).attr(\"src\"));");
		sb.append("});");
		sb.append("hideShop();");
		sb.append("var now = $('#now').text();");
		sb.append("var end = $('#end').text();");

		sb.append("deadline(document.getElementById(\"DeadLine\"),now,Number(end));");

		sb.append("});");
		sb.append("setInterval(function() {");
		sb.append("$.get(\"/front/ping?t=\" + Math.random());");
		sb.append("}, 10*60*1000);");
		sb.append("function otherShow(){");
		sb.append("var otherReasonInput = $('input:radio[id=\"otherReason\"]:checked').val();");
		sb.append("if(otherReasonInput == null){");
		sb.append("	$(\"#otherReasonInput\").hide();");
		sb.append("} else {");
		sb.append("$(\"#otherReasonInput\").show();");
		sb.append("}");
		sb.append("}");
		sb.append("function giveupDiv(){");
		sb.append("layer.open({");
		sb.append("type: 1,");
		sb.append("content: $('#giveupDiv'),");
		sb.append("shade: 0.7,");
		sb.append("title: '放弃任务',");
		sb.append("skin: 'layui-layer-molv',");
		sb.append("shadeClose: false,");
		sb.append("});");
		sb.append("}");
		sb.append("</script> ");
		sb.append(" </head> ");
		sb.append("<body> ");
		sb.append(" <div class=\"rec_content\">");
		sb.append("<div style=\"border-bottom:#CCC 1px solid;\">");
		sb.append("  <div class=\"f_l\">");
		sb.append(" <a href=\"/user/home\">&lt; 返回首页</a>");
		sb.append("</div> ");
		sb.append("<div class=\"f_r\">");
		sb.append("  活动编号:sea_liujing-20180412102307，");
		sb.append("<span class=\"clorg\">8.28</span> 金币，");
		sb.append("<span class=\"clorg\"></span>");
		sb.append(" </div> ");
		sb.append(" <div class=\"clear\"></div> ");
		sb.append(" </div> ");
		sb.append(" <h2 class=\"mg_t_18px\">活动类型：<span class=\"clorg\">关键词</span> - ");
		sb.append(" <!--");
		sb.append("\">");
		sb.append("<i class=\"phf_ico\"></i>浏览下单");
		sb.append("--> ");
		sb.append("<span class=\"cl_red\">马上</span>付款 </h2> ");
		// sb.append("<p>付款方式： <i class=\"tmjf_ico mg_l_12px\"></i>天猫积分 </p> ");
		sb.append("<p>付款方式：");
		sb.append("  支付宝，余额宝或储蓄卡付款。<br />");
		sb.append("<span class=\"cl_red\">请勿使用花呗或信用卡支付，否则将赔付商家1%手续费，处罚5金币。</span>");
		sb.append(" </p>");
		sb.append(" <p class=\"cl_red bold\"><i class=\"sex_ico\"></i>提醒：此产品为成人用品</p>");

		sb.append(" <p class=\"dash_bottom mg_t_8px\">操作端：<span class=\"cl_red\"> 手机端</span><span class=\"cl_gray\">（用错操作端浏览下单，将作废，处罚<span class=\"cl_red\"> 5 </span>金币）</span></p>");
		sb.append("<p class=\"dash_bottom\">平台：<span class=\"cl_red\"> 京东（www.jd.com） </span></p> ");
		sb.append("<p class=\"dash_bottom mg_t_8px\">返现金额：<span class=\"newTaskPrice cl_red\"></span> <span style=\"color:#CDCDCD;\">（参考价：<del>￥236.0） </del></span></p> ");
		sb.append("<p class=\"dash_bottom\">买号性别：<span class=\"cl_red\">不限 </span></p> ");
		sb.append(" <p>补充说明：</p> ");
		sb.append(" <p class=\"introduction\" style=\"background-color:#FFF; \">严禁京挑客，补充说明：【重要重要重要：货比3家，搜索词“沙发垫 ”进，不能使用京东券和京豆！！要求关注单品、关注店铺，必须观看主图视频完整】谢谢配合！！ 订单实际下单为：斜纹全棉 绽放 70*70cm 两片套装59元=4个， 最后付款177元，请根据物流信息确认收货，不可以提前确认。</p>");
		// sb.append("<div class=\"mg_t_18px cl_red\" style=\"text-align:center; font-size:24px;\">");
		// sb.append(" 慢了一步，已被抢完咯~");
		// sb.append(" </div> ");

		sb.append(" <p class=\"mg_t_18px\">");
				
		sb.append(" <b>买号：</b><span class=\"cl_red\">期待ing121</span>（");
		sb.append("  女");
		sb.append("  ）<span class=\"cl_gray f_14px\">今日还可接手2次 （用错买号，处罚<span class=\"cl_red\">");
		sb.append("  5 </span>金币）</span>");
		sb.append(" <input type=\"hidden\" name=\"buyer_id\" value=\"36949\"/>");
		sb.append("  <input type=\"hidden\" name=\"buyer_name_36949\" value=\"杨锦明\"/>");
		
		
//		sb.append("<table width=\"100%\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"mg_t_18px\">");
//		sb.append("<tbody>");
//		sb.append("<tr>");
//		sb.append("<td width=\"20%\" rowspan=\"3\" class=\"bold\">选择淘宝号：</td>");
//		sb.append("<td width=\"80%\">");
//		sb.append("<input type=\"hidden\" name=\"buyer_name_36949\" value=\"杨锦明\"/>");
//		sb.append("<input type=\"radio\" name=\"buyer_id\" value=\"36949\" class=\"vm\" checked=\"checked\"/>");
//		sb.append("  期待ing121（");
//		sb.append("女");
//		sb.append("）<span class=\"cl_gray\">今日还可接手2次</span></td>");
//		sb.append(" </tr>");
//		sb.append("<tr>");
//		sb.append(" <td>");
//		sb.append("<input type=\"hidden\" name=\"buyer_name_37325\" value=\"林智滨\"/>");
//		sb.append(" <input type=\"radio\" name=\"buyer_id\" value=\"37325\" class=\"vm\"/> linzb800（");
//		sb.append("	男）<span class=\"cl_gray\">今日还可接手3次</span></td>");
//		sb.append(" </tr>");
//		sb.append("</tbody></table>");
		sb.append(" <p><b>活动奖励金币：</b><span class=\" cl_red f_17px\">10.12</span> 金币</p>");
		sb.append("<div class=\"mg_t_8px\" style=\"text-align:center;\">");
		sb.append("<input name=\"\" type=\"button\" value=\"马上抢购\" onclick=\"accetTask(1491205)\" class=\"button red\" /></div>");
		sb.append(" <!-- 任务未被接手状态 --> ");
		sb.append("<span id=\"shopName\" style=\"display:none\">钟爱一生官方旗舰店</span> ");
		sb.append("<span id=\"aliww\" style=\"display:none\">钟爱一生</span> ");
		sb.append("<span id=\"taskPrice\" style=\"display:none\">118.0</span> ");
		sb.append(" <span id=\"now\" style=\"display:none\"></span> ");
		sb.append("<span id=\"end\" style=\"display:none\"></span> ");
		sb.append(" <div id=\"giveupDiv\" style=\" display:none;\"> ");
		sb.append("<div style=\"padding:28px 58px 38px 58px; font-size:14px; line-height:2em;\"> ");
		sb.append("<p class=\"cl_red\" style=\"font-size:16px; border-bottom:#CCC 1px dotted; padding-bottom:6px;\">请选择放弃理由：</p> ");
		sb.append("<p class=\"mg_t_8px\"><label><input type=\"radio\" id=\"notFound\" name=\"ReasonType\" onclick=\"otherShow();\" class=\"vm\" value=\"NOT_FOUND\"> 找不到任务 <span class=\"cl_red\">(此任务将被下架)</span></label></p>");
		sb.append("<p class=\"mg_t_8px\"><label><input type=\"radio\" id=\"notFound\" name=\"ReasonType\" onclick=\"otherShow();\" class=\"vm\" value=\"PERSONAL\"> 个人原因，需要放弃</label></p> ");
		sb.append("<p class=\"mg_t_8px\"><label><input type=\"radio\" id=\"otherReason\" name=\"ReasonType\" onclick=\"otherShow();\" class=\"vm\" value=\"OTHER\"> 其他</label> <input style=\"display:none\" id=\"otherReasonInput\" type=\"text\" class=\"ip_normal w_66\" placeholder=\"请输入放弃原因\"> </p>");
		sb.append(" <p class=\"mg_t_8px\">您今日还可以放弃<span class=\" cl_red bold\">20</span>次 <br> 如恶意放弃任务将封号</p> ");
		sb.append(" <p class=\"mg_t_18px\"><a href=\"javascript:;\" class=\"button orange\" onclick=\"giveUpTask(1488127)\">放弃任务</a> <a href=\"javascript:;\" onclick=\"layer.closeAll();\" class=\"button gray\">取消放弃</a></p> ");
		sb.append("</div> ");
		sb.append("</div> ");
		sb.append(" </div>   ");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	public static void main(String[] arg) throws Exception {

		// <p class="mg_t_18px">
		//
		// <b>买号：</b><span class="cl_red">期待ing121</span>（
		// 女
		// ）<span class="cl_gray f_14px">今日还可接手2次 （用错买号，处罚<span class="cl_red">
		// 5 </span>金币）</span>
		// <input type="hidden" name="buyer_id" value="36949"/>
		// <input type="hidden" name="buyer_name_36949" value="杨锦明"/>
		// <p><b>活动奖励金币：</b><span class=" cl_red f_17px">9.66</span> 金币</p>
		// <div class="mg_t_8px" style="text-align:center;">
		// <input name="" type="button" value="马上抢购"
		// onclick="accetTask(1579669)" class="button red" /></div>

//		String cookie = "__root_domain_v=.udamai.com; captcha_key=9ad64beacfc6fd4fb6bdba75fb93cf3e; au=bGluemIsNjM4YjQxYzZjYWNiN2MyMjJjM2NkYTRmZTZiZjAxZDU=; loginToken=NTE5ODQ=; JSESSIONID=F73335F7F9D097DBDFF67BC2A3282D83; _qddaz=QD.t6su0z.idb9f0.jfny4rp4; _qddamta_2852157589=3-0; _qdda=3-1.1; _qddab=3-wk8nbj.jfs3htgo";
//		HashMap<Integer, Byte> buyers = new HashMap<>();
//		buyers.put(36949, (byte) 2);
//		buyers.put(37325, (byte) 3);
//
//		Buyer user1 = new Buyer(1,36949, "女", "杨锦明", "期待ing121");
//		Buyer user2 = new Buyer(2,37325, "男", "林智滨", "linzb800");
//
//		List<Buyer> userMap = new ArrayList<Buyer>();
//		userMap.add(user1);
//		userMap.add(user2);

		
//		getTask(cookie,userMap, 1);
		accetTask("", 20000, 20000);
		
//		for (int i = 0; i < 1000; i++) {
//			UserInfo ui = getBuyer(buyers, userMap, "不限");
//			System.out.println(ui.getName() + "," + ui.getSex());
//		}
	}
}
