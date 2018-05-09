package com.linzb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.linzb.model.SuggestTask;
import com.linzb.model.TaskDetail;
import com.linzb.model.User;
import com.linzb.model.Buyer;

public class RobTask {

	public static Random rand = new Random();
	public static short loopTimes = 50;//每循环刷新次数
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		User userCond = new User();
		Buyer user1 = new Buyer(1,36949, "女", "杨锦明", "期待ing121");
		Buyer user2 = new Buyer(2,37325, "男", "林智滨", "linzb800");
		
		List<Buyer> userMap = new ArrayList<Buyer>();
		userMap.add(user1);
		userMap.add(user2);
		String cookie = "__root_domain_v=.udamai.com; captcha_key=9ad64beacfc6fd4fb6bdba75fb93cf3e; au=bGluemIsNjM4YjQxYzZjYWNiN2MyMjJjM2NkYTRmZTZiZjAxZDU=; loginToken=NTE5ODQ=; JSESSIONID=F73335F7F9D097DBDFF67BC2A3282D83; _qddaz=QD.t6su0z.idb9f0.jfny4rp4; _qddamta_2852157589=3-0; _qdda=3-1.1; _qddab=3-wk8nbj.jfs3htgo";
		System.out.println("开始抢单请输入Y(不区分大小写)按回车，按任意键回车退出");
		char in = (char) System.in.read();
		
		if(in == 'y' || in == 'Y'){
			System.out.println("开始抢单...");
		}else{
			System.exit(0);
		}
		
		for(int i=0;i<loopTimes;i++){
			try{
				List<SuggestTask> list = TaskUtil.getSuggestTask(cookie);
				
				if(list != null && list.size() > 0){
					Thread.sleep(50);
					for(int k=list.size();k>0;k--){
						SuggestTask st = list.get(k-1);
//					for(int k=0;k<list.size();k++){
//						SuggestTask st = list.get(k);
						StringBuffer sb = new StringBuffer();
						if(userCond.isAccept(st,sb)){
							TaskDetail taskDetail = TaskUtil.getTask(cookie,userMap, st.getTaskId());
							//抢单
							if(taskDetail != null && taskDetail.getBuyer() != null){
								TaskUtil.accetTask(cookie,taskDetail.getTaskId(), taskDetail.getBuyer().getBuyerId());
							}
							break;
						}
					}
					break;
				}
				
				long sleep = 1000 + rand.nextInt(2000);
				System.out.println("休息：" + sleep + "ms,当前刷新次数：" + i);
				Thread.sleep(sleep);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("刷新异常退出循环，请手动重新开启。");
				break;
			}
		}
		System.out.println("刷新结束...");
	}

}
