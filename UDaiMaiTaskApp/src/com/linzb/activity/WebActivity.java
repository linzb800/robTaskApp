package com.linzb.activity;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.linzb.database.DBHelper;
import com.linzb.database.DBManager;
import com.linzb.model.Buyer;
import com.linzb.model.User;
import com.linzb.udaimaitaskapp.R;
import com.linzb.util.StringUtil;

public class WebActivity extends Activity {

	
	public static final String homeUrl = "http://a.udamai.com/user/home";
	public static final String buyerUrl = "http://a.udamai.com/user/buyer";
	
	private TextView progressTextView;
	private WebView webView;
	private LinearLayout enterLinearLayout;
	private Button entryRobBtn;
	private String cookie;
	private DBHelper dbh;
	private String udmName;
	private User userCond;
	private List<Buyer> buyerList = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题拦
		setContentView(R.layout.webview);
		
		/*Document doc = Jsoup.parse(TaskUtil.getJsonDemo());
		String html = doc.getElementsByTag("body").text();
		try {
			JSONObject json = new JSONObject(html);
			Toast.makeText(WebActivity.this, json.getInt("code") + "," + json.getString("msg"),  Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(WebActivity.this, e.getMessage(),  Toast.LENGTH_LONG).show();
		}*/
		
		dbh = new DBHelper(WebActivity.this);
		userCond = DBManager.getInstance(WebActivity.this).queryUserCond(dbh);
		if(userCond == null){
			Intent intent = new Intent(WebActivity.this, LoginActivity.class);
			startActivity(intent);
			return;
		}
		buyerList = DBManager.getInstance(WebActivity.this).queryBuyers(dbh, userCond.getId());
		
		progressTextView = (TextView) findViewById(R.id.text_Loading);
		enterLinearLayout = (LinearLayout) findViewById(R.id.floatLinear);
		entryRobBtn = (Button) findViewById(R.id.enterBtn);
		entryRobBtn.setOnClickListener(entryRobListener);
		
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new InJavaScriptObj(), "javaObj");
		webView.getSettings().setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
		webView.getSettings().setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
		webView.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setLoadWithOverviewMode(true);//缩放至屏幕的大小
		webView.setInitialScale(25);
		webView.getSettings().setDisplayZoomControls(false); //隐藏原生的缩放控件
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口 
		webView.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片
		webView.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式
		//webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);//缓存设置
		//webView.getSettings().setDomStorageEnabled(true);
		//webView.getSettings().setBlockNetworkImage(true);
		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("urlLoding:" + url);
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progressTextView.setVisibility(View.VISIBLE);
				progressTextView.setText("正在打开网页,请稍后...");
				System.out.println("开始加载页面：" + url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				
				if(homeUrl.equals(url) || buyerUrl.equals(url)){
					view.loadUrl("javascript:window.javaObj.showSource('"+url+"',document.getElementsByTagName('html')[0].innerHTML);");
					CookieManager cookieManager = CookieManager.getInstance();  
					String ck = cookieManager.getCookie(url);
					if(ck.indexOf("au=") >= 0 && ck.indexOf("loginToken=") >= 0){
						cookie = ck;
					}
					
					//System.out.println("cookieUrl=" + url + ",cookie=" + cookie);
				}
				
				progressTextView.setVisibility(View.GONE);
				System.out.println("页面加载完成：" + url);
				
				enterLinearLayout.setVisibility(View.VISIBLE);
				super.onPageFinished(view, url);
		         
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
				//System.out.println("加载资源：" + url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				System.out.println("接收错误：code=" + errorCode);
			}

			@Override
			public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
				System.out.println("keyEvent：");
				return super.shouldOverrideKeyEvent(view, event);
			}

			@Override
			public void onScaleChanged(WebView view, float oldScale, float newScale) {
				//System.out.println("onScaleChange：" + oldScale + "," + newScale);
				super.onScaleChanged(view, oldScale, newScale);
			}

			@Override
			public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
				System.out.println("ReceivedLoginRequest");
				super.onReceivedLoginRequest(view, realm, account, args);
			}
		});
		
		
		webView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressTextView.setText("页面加载进度 " + newProgress + "%");
//				if(newProgress == 100){
//					webView.getSettings().setBlockNetworkImage(false);
//				}
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onCloseWindow(WebView window) {
				super.onCloseWindow(window);
			}
		});
		//webView.loadUrl("http://a.udamai.com/");
		webView.loadUrl(homeUrl);
	}

	  OnClickListener entryRobListener = new OnClickListener(){
			public void onClick(View arg0) {
				if(StringUtil.isEmpty(udmName)){
					Toast.makeText(WebActivity.this, "帐号未登录",  Toast.LENGTH_LONG).show();
					return;
				}
				
				if(StringUtil.isEmpty(cookie) || cookie.indexOf("au=") == -1 || cookie.indexOf("loginToken=") == -1){
					Toast.makeText(WebActivity.this, "无效的cookie=" + cookie,  Toast.LENGTH_LONG).show();
					return;
				}
				
				if(buyerList == null || buyerList.size() == 0){
					Toast.makeText(WebActivity.this, "请先点击【淘宝号管理】获取买号信息",  Toast.LENGTH_LONG).show();
					return;
				}
				
				Intent intent = new Intent(WebActivity.this, MainActivity.class);
				intent.putExtra("userCookie", cookie);
				startActivity(intent);
			}
	    };
	
    public final class InJavaScriptObj{
    	@JavascriptInterface
        public void showSource(String url,String html) {
    		
    		if(url.equals(homeUrl)){
    			try{
    				String tmpUdmName = "";
    				Document doc = Jsoup.parse(html);
    				Element ele = doc.getElementById("m_r_r");
    				if(ele != null){
    					Elements eles = ele.getElementsByTag("span");
    					for(Element e:eles){
    						System.out.println(e.text());
    						if(e.text() != null && ele.text().startsWith("用户名")){
    							tmpUdmName = e.text().substring(4);
								break;
    						}
    					}
    				}
    				
    				if(StringUtil.isEmpty(tmpUdmName)){
    					Toast.makeText(WebActivity.this, "提到不到帐号名称，请先登录。",  Toast.LENGTH_LONG).show();
    				}else if(StringUtil.isEmpty(userCond.getUdmName())){
    					userCond.setUdmName(tmpUdmName);
    					if(DBManager.getInstance(WebActivity.this).updateUdmName(dbh, userCond)){
    						udmName = tmpUdmName;
    						Toast.makeText(WebActivity.this, "成功提到帐号：" + tmpUdmName,  Toast.LENGTH_LONG).show();
    					}else{
    						Toast.makeText(WebActivity.this, "保存帐号失败：" + tmpUdmName,  Toast.LENGTH_LONG).show();
    					}
    				}else if(!StringUtil.isEmpty(userCond.getUdmName()) && !tmpUdmName.equals(userCond.getUdmName())){
    					Toast.makeText(WebActivity.this, "提取帐号：" + tmpUdmName + "与之前登录("+userCond.getUdmName()+")不一致，请不要登录其它帐户",  Toast.LENGTH_LONG).show();
    				}else{
    					udmName = tmpUdmName;
    					Toast.makeText(WebActivity.this, "欢迎你：" + tmpUdmName,  Toast.LENGTH_SHORT).show();
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    				Toast.makeText(WebActivity.this, "解析帐号异常,e=" + e.getMessage(),  Toast.LENGTH_LONG).show();
    			}
    		}else if(url.equals(buyerUrl)){
    			try{
    				if(buyerList != null && buyerList.size() > 0){
    					return;
    				}
    				
    				List<Buyer> list = new ArrayList<>();
    				Document doc = Jsoup.parse(html);
    				Elements eles = doc.getElementsByTag("tbody");
					for(Element e:eles){
						Elements treles = e.getElementsByTag("tr");
						Element firstEle = treles.first();
						Elements th = firstEle.getElementsByTag("th");
						if(th == null || th.size() != 4 ||!th.get(0).text().equals("旺旺") || !th.get(1).text().equals("性别")){
							continue;
						}
						
						for(int i=1;i<treles.size();i++){
							Elements td = treles.get(i).getElementsByTag("td");
							String taobaoName = td.get(0).text().trim();
							String sex = td.get(1).text().trim();
							String name = td.get(2).text().trim().split("，")[0]; 
							Buyer tba = new Buyer(sex, name, taobaoName);
							tba.setUserId(userCond.getId());
							list.add(tba);
						}
						break;
					}
    				
    				if(list.size() > 0){
    					StringBuffer sb = new StringBuffer();
    					for(Buyer tba:list){
    						if(sb.length() > 0)sb.append(",");
    						sb.append(tba.getTaobaoName());
    						if(DBManager.getInstance(WebActivity.this).insertBuyer(dbh, tba) == null){
    							DBManager.getInstance(WebActivity.this).delBuyer(dbh, userCond.getId());
    							Toast.makeText(WebActivity.this, "保存买号失败：" + tba.toInfo(),  Toast.LENGTH_LONG).show();
    							break;
    						}
    					}
    					buyerList = list;
    					Toast.makeText(WebActivity.this, "成功提到买号：" + sb.toString(),  Toast.LENGTH_LONG).show();
    				}else{
    					Toast.makeText(WebActivity.this, "提取不到买号信息",  Toast.LENGTH_LONG).show();
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    				Toast.makeText(WebActivity.this, "解析买号异常,e=" + e.getMessage(),  Toast.LENGTH_LONG).show();
    			}
    		}
    		
           //System.out.println("====>url=" + url + "html=" + html);
        }
    }
	    
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
		 	webView.goBack();
            return true;
        }
		
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			dialog();
//			System.out.println("back key。。。。。。。。。。。");
//		}
//		return false;
		return super.onKeyDown(keyCode, event);
	}

//	protected void dialog() {
//		AlertDialog.Builder builder = new Builder(WebActivity.this);
//		builder.setMessage("確認完成支付，返回遊戲嗎？");
//		builder.setTitle("提示");
//		builder.setPositiveButton("確認", new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				WebActivity.this.finish();
//			}
//		});
//
//		builder.setNegativeButton("取消", new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//
//		builder.create().show();
//	}
}
