package com.linzb.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.linzb.database.DBHelper;
import com.linzb.database.DBManager;
import com.linzb.model.Buyer;
import com.linzb.model.SuggestTask;
import com.linzb.model.TaskDetail;
import com.linzb.model.User;
import com.linzb.udaimaitaskapp.R;
import com.linzb.util.StringUtil;
import com.linzb.util.TaskUtil;

public class MainActivity extends Activity {
	
	private String cookie;
	private User userCond;
	private DBHelper dbh;
	private List<Buyer> buyerList = null;
	private boolean isHttpRequst;
	
	private Button startRobBtn50;
	private Button startRobBtn5;
	private Button startRobBtn1;
	private TextView showArea;
	private List<String> tipList = new ArrayList<>();
	private Random rand = new Random();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题拦
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		cookie = intent.getExtras().getString("userCookie");
		System.out.println("getCookie:" + cookie);
		
		dbh = new DBHelper(MainActivity.this);
		userCond = DBManager.getInstance(MainActivity.this).queryUserCond(dbh);
		buyerList = DBManager.getInstance(MainActivity.this).queryBuyers(dbh, userCond.getId());
		
		isHttpRequst = false;
		
		/*userCond = new User();
		Buyer user1 = new Buyer(1,36949, "女", "杨锦明", "期待ing121");
		Buyer user2 = new Buyer(2,37325, "男", "林智滨", "linzb800");
		
		userMap = new HashMap<>();
		userMap.put(user1.getBuyerId(),user1);
		userMap.put(user2.getBuyerId(),user2);*/
		
		//开始抢单按钮监听
		startRobBtn50 = (Button)findViewById(R.id.task_rob50);
		startRobBtn5 = (Button)findViewById(R.id.task_rob5);
		startRobBtn1 = (Button)findViewById(R.id.task_rob1);
		startRobBtn50.setOnClickListener(startRobListener);
		startRobBtn5.setOnClickListener(startRobListener);
		startRobBtn1.setOnClickListener(startRobListener);
		
		
		showArea = (TextView) findViewById(R.id.showArea);
		StringBuffer sb = new StringBuffer();
		if(userCond.getLoginName().equals("tiger")){
			sb.append("注：本工具目前完全免费，未经开发者同意，安装包不得转发，倒卖等。\n工具未经严格测试使用过程中出现问题造成损失概不负责。\\(^o^)/\n");
		}
		sb.append(userCond.toInfo());
		showArea.setText(sb.toString());
		showArea.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		createTip();
		
	}
	
	private void createTip(){
		tipList.add("胜利就在前方！");
		tipList.add("生活有度，人生添寿！");
		tipList.add("温馨提示：");
		tipList.add("温馨提示：");
		tipList.add("你手机里有没有装抖音或快手？");
		tipList.add("修身齐家治国平天下出自哪里？");
		tipList.add("知彼知己，百战不殆！");
		tipList.add("是不是等的花儿都快谢了！");
		tipList.add("两个黄鹂鸣翠柳...");
		tipList.add("关关雎鸠，在河之洲。下一句呢，");
		tipList.add("业精于勤，荒于嬉；行成于思，毁于随！");
		tipList.add("冷笑话：自己的饭量自己知道！");
		tipList.add("冷笑话：化学老师问，煤气泄露要怎么办?别慌，点根儿烟，冷静一下！");
		tipList.add("冷笑话：时间过的真快，刚起床就天黑了！");
		tipList.add("冷笑话：我哪是什么朴实，节俭，会过日子的人，我只是单纯的穷而已。");
		tipList.add("温馨提示：");
		tipList.add("温馨提示：");
		tipList.add("两个黄鹂鸣翠柳...");
		tipList.add("两个黄鹂鸣翠柳...");
	}
	
	private String getTip(){
		int v = rand.nextInt(100);
		if(v < 5){
			return tipList.get(rand.nextInt(tipList.size()));
		}
		return null;
	}
	
	
	

	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			
			if(msg.what == R.id.showArea){
				if(msg.arg1 == 0){//arg1==0是追加，=1是覆盖
					showArea.setText(showArea.getText() + "\n" + msg.obj.toString());
				}else{
					showArea.setText( msg.obj.toString());
				}
			}else if(msg.what == R.id.task_rob50){
				if(msg.arg1 == 0){
					startRobBtn50.setText(msg.obj.toString());
				}
			}else if(msg.what == R.id.task_rob5){
				if(msg.arg1 == 0){
					startRobBtn5.setText(msg.obj.toString());
				}
			}else if(msg.what == R.id.task_rob1){
				if(msg.arg1 == 0){
					startRobBtn1.setText(msg.obj.toString());
				}
			}
		}
	};
	
	
	public void updateRobButton(int viewId,String str){
		Message msg = new Message();
		msg.what = viewId;
		msg.obj = str;
		uiHandler.sendMessage(msg);
	}
	
	public void addShowAreaMsg(String str){
		addShowAreaMsg(str, 0);
	}
	public void addShowAreaMsg(String str,int arg1){
		Message msg = new Message();
		msg.what = R.id.showArea;
		msg.obj = str;
		msg.arg1 = arg1;
		uiHandler.sendMessage(msg);
	}
	
	private boolean isRun(){
		if(!isHttpRequst){
			addShowAreaMsg("离开抢单界面停止抢单！");
			return false;
		}
		return true;
	}
	
	private void startRobTask(final int viewId,final int count){
		
		isHttpRequst = true;
		updateRobButton(viewId,"抢单中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					for(int i=1;i<=count;i++){
						if(!isRun())break;
						addShowAreaMsg("第" + i + "次努力抢单中",1);
						List<SuggestTask> list = TaskUtil.getSuggestTask(cookie);
						if(list != null && list.size() > 0){
							addShowAreaMsg("本次刷新获得推荐任务个数：" + list.size());
							boolean haveTask = false;
							for(int k=0;k<list.size();k++){
								SuggestTask st = list.get(k);
								addShowAreaMsg("---------任务信息---------");
								addShowAreaMsg(st.toInfo());
								StringBuffer sb = new StringBuffer();
								boolean accept = userCond.isAccept(st,sb);
								if(sb.length() > 0){
									addShowAreaMsg("---->" + sb.toString());
								}
								if(accept){
									haveTask = true;
									addShowAreaMsg("---->尝试抢单taskID=" + st.getTaskId());
									try{
										Thread.sleep(50);
										TaskDetail taskDetail = TaskUtil.getTask(cookie,buyerList, st.getTaskId());
										if(taskDetail != null && taskDetail.getBuyer() != null){//抢单
											addShowAreaMsg("详细信息：===================");
											addShowAreaMsg(taskDetail.toInfo());
											
											for(String s:userCond.getTitleFilterList()){
												if(taskDetail.getShopName().indexOf(s) >= 0){
													throw new Exception("店铺名称：含有[" + s + "]");
												}
											}
											
										addShowAreaMsg("*************************************");
										JSONObject json = null;
										try{
											Thread.sleep(100);
											json = TaskUtil.accetTask(cookie,taskDetail.getTaskId(), taskDetail.getBuyer().getBuyerId());
										}catch(Exception e){
											e.printStackTrace();
											addShowAreaMsg("异常：" + e.getMessage());
										}
										if(json == null){
											addShowAreaMsg("抢单关键时返回异常，请到活动管理查看是否成功。");
											break;
										}
										if(json.getInt("code") == 0){
											addShowAreaMsg("买号信息：" + taskDetail.getBuyer().toInfo());
											addShowAreaMsg("恭喜抢单成功!");
										}else{
											addShowAreaMsg("抢单失败：" + json.getString("msg"));
										}
										addShowAreaMsg("*************************************");
										break;
										}
									}catch(Exception e){
										addShowAreaMsg("抢单失败：" + e.getMessage());
										break;
									}
									break;
								}
							}
							if(!haveTask){
								addShowAreaMsg("抢单失败：无符合条件任务");
							}
							break;
						}else{
							if(i<count){
								if(i % 5 == 0){//逢5倍数，休息延长10-20秒
									int second = 10 + rand.nextInt(25);
									addShowAreaMsg("被抢光了。本次休息" + second + "秒...");
									for(int k=second;k>=0;k--){
										if(!isRun())break;
										
										String tip = getTip();
										if(tip != null){
											addShowAreaMsg(tip + "还剩" + k + "秒...");
										}else{
											addShowAreaMsg("还剩" + k + "秒...");
										}
										Thread.sleep(1000);
									}
								}else{
									long sleep = 1000 + rand.nextInt(2000);
									addShowAreaMsg("被抢光了。休息" + sleep + "毫秒在继续抢...");
									Thread.sleep(sleep);
								}
							}
						}
						
						if(i == count){
							addShowAreaMsg("刷新结束，请重新开始抢单。");
							break;
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					addShowAreaMsg("异常：" + e.getMessage() + "\n如重复出现请稍后在试，避免被封号！");
				}
				
				String text = "抢单1次";
				if(viewId == R.id.task_rob50){
					text = "抢单50次";
				}else if(viewId == R.id.task_rob5){
					text = "抢单5次";
				}
				
				updateRobButton(viewId,text);
				isHttpRequst = false;
			}
		}).start();
	}
	
	
	
	 //创建按钮监听器对象
    OnClickListener startRobListener = new OnClickListener(){
		public void onClick(View arg0) {
			
			if(StringUtil.isEmpty(cookie) || cookie.indexOf("au=") == -1 || cookie.indexOf("loginToken=") == -1){
				Toast.makeText(MainActivity.this, "无效的cookie",  Toast.LENGTH_LONG).show();
				return;
			}
			
			if(userCond == null || !userCond.isValid()){
				Toast.makeText(MainActivity.this, "无效的登录信息",  Toast.LENGTH_LONG).show();
				return;
			}
			
			if(buyerList == null || buyerList.size() == 0){
				Toast.makeText(MainActivity.this, "无效的买号信息",  Toast.LENGTH_LONG).show();
			}
			
			if(isHttpRequst){
				Toast.makeText(MainActivity.this,"正在抢单中，请勿重复操作。", Toast.LENGTH_SHORT).show();
				return;
			}
			
			int count = 1;
			if(arg0.getId() == R.id.task_rob50){
				count = 50;
			}else if(arg0.getId() == R.id.task_rob5){
				count = 5;
			}
			
			startRobTask(arg0.getId(),count);
		}
    };

	@Override
	protected void onPause() {
		this.isHttpRequst = false;
		super.onPause();
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
